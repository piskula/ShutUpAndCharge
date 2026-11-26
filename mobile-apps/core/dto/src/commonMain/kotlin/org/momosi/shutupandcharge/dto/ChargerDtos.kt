package org.momosi.shutupandcharge.dto

import kotlinx.serialization.Serializable

@Serializable
enum class CarStateDto {
    UnknownOrError,
    Idle,
    Charging,
    WaitCar,
    Complete,
    Error,
}

@Serializable
enum class OcppConnectorStatusDto {
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

@Serializable
data class ChargerStatusDto(
    val carState: CarStateDto = CarStateDto.UnknownOrError,
    val connectorStatusOcpp: OcppConnectorStatusDto = OcppConnectorStatusDto.Unavailable,
    val occupiedFrom: String? = null,
    val chargedKwh: Double = 0.0,
    val currentList: List<Int> = emptyList(),
    val current: Int = 0,
)
