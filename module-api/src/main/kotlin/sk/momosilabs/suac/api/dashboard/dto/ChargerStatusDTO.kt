package sk.momosilabs.suac.api.dashboard.dto

import java.math.BigDecimal
import java.time.OffsetDateTime

data class ChargerStatusDTO(
    val carState: CarStateDTO,
    val connectorStatusOcpp: OcppConnectorStatusDTO,
    val occupiedFrom: OffsetDateTime? = null,
    val chargedKwh: BigDecimal,
)

enum class CarStateDTO {
    UnknownOrError,
    Idle,
    Charging,
    WaitCar,
    Complete,
    Error;
}

enum class OcppConnectorStatusDTO {
    Available,
    Preparing,
    Charging,
    SuspendedEVSE,
    SuspendedEV,
    Finishing,
    Reserved,
    Unavailable,
    Faulted,
}
