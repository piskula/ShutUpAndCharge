package org.momosi.shutupandcharge.core.charger.dto

import org.momosi.shutupandcharge.core.charger.model.ChargerState
import org.momosi.shutupandcharge.core.charger.model.ChargerStatus
import org.momosi.shutupandcharge.core.network.dto.toInstantOrNull
import org.momosi.shutupandcharge.dto.CarStateDto
import org.momosi.shutupandcharge.dto.ChargerStatusDto

internal fun ChargerStatusDto.toChargerStatus(): ChargerStatus = ChargerStatus(
    state = carState.toChargerState(),
    occupiedFrom = occupiedFrom?.toInstantOrNull(),
    chargedKwh = chargedKwh,
    availableCurrents = currentList,
    current = current,
)

private fun CarStateDto.toChargerState(): ChargerState = when (this) {
    CarStateDto.UnknownOrError -> ChargerState.Offline
    CarStateDto.Idle -> ChargerState.Free
    CarStateDto.WaitCar -> ChargerState.CarConnected
    CarStateDto.Charging -> ChargerState.Charging
    CarStateDto.Complete -> ChargerState.Complete
    CarStateDto.Error -> ChargerState.Error
}
