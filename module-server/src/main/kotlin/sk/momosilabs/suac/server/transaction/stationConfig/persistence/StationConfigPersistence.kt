package sk.momosilabs.suac.server.transaction.stationConfig.persistence

import java.time.Instant

interface StationConfigPersistence {

    fun setLastSuccessDownloadTimestamp(stationId: String, timestamp: Instant)

}
