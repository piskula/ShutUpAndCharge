package sk.momosilabs.suac.server.transaction.pairingProcess.service

import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import sk.momosilabs.suac.server.account.persistence.AccountPersistence
import sk.momosilabs.suac.server.common.GlobalException
import sk.momosilabs.suac.server.dashboard.model.charging.external.CarStateEnum
import sk.momosilabs.suac.server.dashboard.model.charging.external.ExternalChargingLog
import sk.momosilabs.suac.server.dashboard.service.external.ExternalChargingApi
import sk.momosilabs.suac.server.transaction.finished.model.ChargingToCreate
import sk.momosilabs.suac.server.transaction.finished.persistence.TransactionFinishedPersistence
import sk.momosilabs.suac.server.transaction.stationConfig.persistence.entity.StationConfigEntity
import sk.momosilabs.suac.server.transaction.stationConfig.persistence.repository.StationConfigRepository
import sk.momosilabs.suac.server.transaction.temporary.model.TransactionTemporaryToMatch
import sk.momosilabs.suac.server.transaction.temporary.persistence.TransactionTemporaryPersistence
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.UUID

data class DownloadChargingReportsEvent(
    val stationId: String,
)

@Service
open class DownloadAndProcessTemporaryTransactionsList(
    private val externalChargingApi: ExternalChargingApi,
    private val stationConfigRepository: StationConfigRepository,
    private val transactionTemporaryPersistence: TransactionTemporaryPersistence,
    private val transactionFinishedPersistence: TransactionFinishedPersistence,
    private val accountPersistence: AccountPersistence,
) {

    companion object {
        private val logger = LoggerFactory.getLogger(DownloadAndProcessTemporaryTransactionsList::class.java)

        private val timeParsePattern = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")
        private fun ExternalChargingLog.getMeterStart() = (eto_start * 1000).toLong()
    }

    // TODO make sure this can be triggered from outside, so if more instances deployed, only 1 is processing this
    // TODO check if we can avoid parallel run
    @Scheduled(cron = "0 */15 * ? * *")
    @Transactional
    open fun scheduler() {
        downloadAndProcess(DownloadChargingReportsEvent("ETCC:Kutlik:1"))
    }

    @Async
    @Transactional
    @EventListener
    open fun downloadAndProcess(event: DownloadChargingReportsEvent) {
        val stationId = event.stationId
        logger.info("Download report process initiated for station: $stationId")

        val stationConfig = stationConfigRepository.findById(stationId)
            .orElseThrow { GlobalException("station not found") }
        val lastTimestamp = stationConfig.lastSuccessDownloadTimestampUtc ?: LocalDateTime.of(2010, 1, 1, 0, 0)

        val chargerStatus = externalChargingApi.getChargerStatus().ifSuccess?.carState

        val downloadedReports = externalChargingApi.downloadTransactionsFromCloud(lastTimestamp)
            .dropLast(if (chargerStatus != CarStateEnum.Idle) 1 else 0)

        val downloadedReportsByMeterStart = downloadedReports.associateBy { it.getMeterStart() }
        val downloadedReportsByChipUid = downloadedReports.associateBy { it.id_chip_uid }

        val ourTemporaryTransactions = transactionTemporaryPersistence.fetchAwaitingTransactionsForStation(stationId)
            .associateBy { it.energyMeter }
        val chipToUser = accountPersistence.getChipUidToUserIdMap(downloadedReportsByChipUid.keys)

        val meterStartMatches = downloadedReportsByMeterStart.keys intersect ourTemporaryTransactions.keys

        val toProcess = downloadedReports
            .filter { it.getMeterStart() in meterStartMatches || it.id_chip_uid in chipToUser.keys }

        if (toProcess.isEmpty()) {
            logger.debug("Nothing to process, process stops")
            return
        } else {
            logger.info("Following $stationId chargings will be synced: ${toProcess.map { it.getMeterStart() }}")
        }

        val toStore = mergeIntoEntities(toProcess, ourTemporaryTransactions, stationConfig, chipToUser)
            .sortedByDescending { it.energyMeter }

        transactionFinishedPersistence.saveFinishedChargingBulk(toStore)
        transactionTemporaryPersistence.deleteAwaitingTransactionsForStation(stationId, meterStartMatches)

        stationConfig.lastSuccessDownloadTimestampUtc = LocalDateTime.ofInstant(toStore.first().timeEnd, ZoneOffset.UTC)
    }

    private fun mergeIntoEntities(
        downloadedTransactions: List<ExternalChargingLog>,
        ourTemporaryTransactions: Map<Long, TransactionTemporaryToMatch>,
        station: StationConfigEntity,
        chipToUserResolver: Map<String, Long>,
    ) = downloadedTransactions.map { downloaded ->
        val tmp = ourTemporaryTransactions[downloaded.getMeterStart()]
        val kwh = BigDecimal.valueOf(downloaded.eto_diff).setScale(3, RoundingMode.FLOOR)
        ChargingToCreate(
            guid = tmp?.trxIdentifier?.let { UUID.fromString(it) } ?: UUID.randomUUID(),
            userId = tmp?.accountId ?: chipToUserResolver[downloaded.id_chip_uid]!!,
            timeStart = LocalDateTime.parse(downloaded.start, timeParsePattern).atZone(ZoneOffset.UTC).toInstant(),
            timeEnd = LocalDateTime.parse(downloaded.end, timeParsePattern).atZone(ZoneOffset.UTC).toInstant(),
            kwh = kwh,
            stationId = station.stationId,
            price = kwh.multiply(station.pricePerKwh).setScale(2, RoundingMode.FLOOR).negate(),
            stationSession = downloaded.session_number,
            energyMeter = downloaded.getMeterStart(),
            chipUid = downloaded.id_chip_uid.let { if (it.isBlank()) null else it },
            link = downloaded.link,
        )
    }

}
