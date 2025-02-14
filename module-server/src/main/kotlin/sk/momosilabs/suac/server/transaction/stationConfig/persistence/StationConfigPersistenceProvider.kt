package sk.momosilabs.suac.server.transaction.stationConfig.persistence

import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import sk.momosilabs.suac.server.transaction.stationConfig.persistence.repository.StationConfigRepository
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

@Repository
open class StationConfigPersistenceProvider(
    private val stationConfigRepository: StationConfigRepository,
): StationConfigPersistence {

    @Transactional
    override fun setLastSuccessDownloadTimestamp(stationId: String, timestamp: Instant) {
        val stationConfig = stationConfigRepository.findById(stationId).get()
        stationConfig.lastSuccessDownloadTimestampUtc = LocalDateTime.ofInstant(timestamp, ZoneOffset.UTC)
    }

}
