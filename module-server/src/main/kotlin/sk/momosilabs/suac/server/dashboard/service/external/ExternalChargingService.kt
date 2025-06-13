package sk.momosilabs.suac.server.dashboard.service.external

import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient
import sk.momosilabs.suac.server.common.ApplicationProperties
import sk.momosilabs.suac.server.common.ApplicationPropertiesStation
import sk.momosilabs.suac.server.dashboard.model.charging.external.ExternalChargerDataWrapper
import kotlinx.serialization.json.Json
import org.springframework.http.HttpStatus
import sk.momosilabs.suac.server.dashboard.model.charging.ChargerStatus
import sk.momosilabs.suac.server.dashboard.model.charging.external.CarStateEnum
import sk.momosilabs.suac.server.dashboard.model.charging.external.ExternalChargerDataError
import sk.momosilabs.suac.server.dashboard.model.charging.external.ExternalChargerErrorResponse
import sk.momosilabs.suac.server.dashboard.model.charging.external.ExternalChargerSuccessResponse
import sk.momosilabs.suac.server.dashboard.model.charging.external.ExternalChargingLog
import sk.momosilabs.suac.server.dashboard.model.charging.external.ExternalDownloadDataResponse
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

@Service
open class ExternalChargingService(
    private val applicationProperties: ApplicationProperties,
): ExternalChargingApi {

    companion object {
        private val logger = LoggerFactory.getLogger(ExternalChargingService::class.java)
        private val deserializer = Json { ignoreUnknownKeys = true }

        private val refreshDelays = longArrayOf(0, 4000, 2500, 2000, 2000, 1500, 1500)
    }

    override fun getChargerStatus(): ExternalChargerDataWrapper {
        return connectionReadToStation(applicationProperties.station)
            .exchange { _, response ->
                val isOk = response.statusCode.is2xxSuccessful
                val is403 = response.statusCode === HttpStatus.FORBIDDEN
                ExternalChargerDataWrapper(
                    error = !isOk,
                    success = isOk,
                    ifSuccess = if (isOk) deserializer.decodeFromString<ExternalChargerSuccessResponse>(String(response.body.readAllBytes())).toModel() else null,
                    ifError = if (is403) deserializer.decodeFromString<ExternalChargerErrorResponse>(String(response.body.readAllBytes())).toModel() else null,
                )
            }!!.also { logger.debug(it.ifSuccess?.toString() ?: it.ifError?.toString()) }
    }

    override fun startCharging(trxNumber: Int, identifier: String): ExternalChargerDataWrapper {
        val started = connectionWriteToStation(applicationProperties.station, "lrn" to trxNumber, "trx" to trxNumber, "ct" to identifier)
            .exchange { _, response -> response.statusCode.is2xxSuccessful }!!
        if (!started) {
            return getChargerStatus()
        }

        logger.debug("Waiting for starting to propagate...")
        return waitForStatusChangeToPropagate { it.ifSuccess?.carState == CarStateEnum.Charging }
    }

    override fun stopCharging(): ExternalChargerDataWrapper {
        logger.debug("Stopping charging...")
        val stopped = connectionWriteToStation(applicationProperties.station, "frc" to 1)
            .exchange { _, response -> response.statusCode.is2xxSuccessful }!!
        if (!stopped) {
            return getChargerStatus()
        }

        logger.debug("Waiting for stopping to propagate...")
        return waitForStatusChangeToPropagate { it.ifSuccess?.carState == CarStateEnum.Complete }
    }

    override fun downloadTransactionsFromCloud(fromTimestampUtc: LocalDateTime): List<ExternalChargingLog> =
        connectionReadCloud(applicationProperties.station, fromTimestampUtc)
            .exchange { _, response ->
                deserializer.decodeFromString<ExternalDownloadDataResponse>(String(response.body.readAllBytes())).data
            }!!

    private fun waitForStatusChangeToPropagate(conditionFullfiled: (ExternalChargerDataWrapper) -> Boolean): ExternalChargerDataWrapper {
        var status = getChargerStatus()
        if (!conditionFullfiled.invoke(status)) {
            for (delay in refreshDelays) {
                Thread.sleep(delay)
                status = getChargerStatus()
                if (conditionFullfiled.invoke(status)) {
                    break
                }
            }
        }
        logger.debug("Propagation waiting finished.")
        return status
    }

    private fun ExternalChargerSuccessResponse.toModel() = ChargerStatus(
        carState = car,
        modelStatus = modelStatus,
        connectorStatusOcpp = ocppcs,
        forceState = frc,
        occupiedFrom = null,
        chargedKwh = BigDecimal.valueOf(wh).divide(BigDecimal.valueOf(1000), 3, RoundingMode.FLOOR),
        trxNumber = trx,
        meterEnergyTotal = etop,
        customIdentifier = ct,
        rfidUid = tsi,
    )

    private fun ExternalChargerErrorResponse.toModel() = ExternalChargerDataError(
        // lastAliveSecondsAgo = age.toLong(), // TODO maybe no longer sent? It changed from 404 to 403 with text reason
        lastAliveSecondsAgo = 0L,
    )

    private fun connectionReadToStation(station: ApplicationPropertiesStation) = RestClient.create().get()
        .uri(station.cloudStatusUrl)
        .header(HttpHeaders.AUTHORIZATION, "Bearer ${station.cloudToken}")

    private fun connectionWriteToStation(
        station: ApplicationPropertiesStation,
        vararg param: Pair<String, Any>,
    ): RestClient.RequestHeadersSpec<*> {
        val params = param.joinToString("&") { (key, value) -> "$key=$value" }
        return RestClient.create().get()
            .uri("${station.cloudSetUrl}?$params")
            .header(HttpHeaders.AUTHORIZATION, "Bearer ${station.cloudToken}")
    }

    private fun connectionReadCloud(station: ApplicationPropertiesStation, fromTimestampUtc: LocalDateTime): RestClient.RequestHeadersSpec<*> {
        val params = mapOf(
            "e" to station.cloudDownloadToken,
            "from" to fromTimestampUtc.toInstant(ZoneOffset.UTC).toEpochMilli().toString(),
            "to" to Instant.now().toEpochMilli().toString(), // without TO timestamp filtering is not working
            "timezone" to "Utc/UTC",
        ).entries.joinToString("&") { (key, value) -> "$key=$value" }
        return RestClient.create().get().uri("${station.cloudDownload}?$params")
    }

}
