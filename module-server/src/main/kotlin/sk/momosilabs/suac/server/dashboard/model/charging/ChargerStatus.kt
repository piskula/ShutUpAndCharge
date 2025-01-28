package sk.momosilabs.suac.server.dashboard.model.charging

import sk.momosilabs.suac.server.dashboard.model.charging.external.CarStateEnum
import sk.momosilabs.suac.server.dashboard.model.charging.external.ExternalChargerStatusEnum
import sk.momosilabs.suac.server.dashboard.model.charging.external.ForceStateEnum
import sk.momosilabs.suac.server.dashboard.model.charging.external.OcppConnectorStatusEnum
import java.math.BigDecimal
import java.time.Instant

data class ChargerStatus(
    val carState: CarStateEnum,
    val modelStatus: ExternalChargerStatusEnum,
    val connectorStatusOcpp: OcppConnectorStatusEnum,
    val forceState: ForceStateEnum,
    val occupiedFrom: Instant?,
    val chargedKwh: BigDecimal,
    val trxNumber: Int?,
    val meterEnergyTotal: Long,
    val customIdentifier: String,
    val rfidUid: String?,
)
