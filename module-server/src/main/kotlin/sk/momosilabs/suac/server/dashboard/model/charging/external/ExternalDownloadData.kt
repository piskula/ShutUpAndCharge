package sk.momosilabs.suac.server.dashboard.model.charging.external

import kotlinx.serialization.Serializable

@Serializable
data class ExternalDownloadDataResponse(
    val data: List<ExternalChargingLog>,
)

@Serializable
data class ExternalChargingLog(
    val session_number: Long,
//    val session_identifier: String,
    val id_chip_uid: String,
//    val id_chip_name: String,
    val start: String,
    val end: String,
//    val seconds_total: String,
//    val seconds_charged: String,
//    val max_power: Double,
//    val max_current: Double,
//    val energy: Double,
    val eto_diff: Double,
    val eto_start: Double,
//    val eto_end: Double,
//    val wifi: String,
    val link: String,
)
