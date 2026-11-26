package org.momosi.shutupandcharge.core.charger.dto

import org.momosi.shutupandcharge.core.charger.model.ChargerState
import org.momosi.shutupandcharge.dto.CarStateDto
import org.momosi.shutupandcharge.dto.ChargerStatusDto
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ChargerDtoMapperTest {

    @Test
    fun carStatesMapToChargerStates() {
        val expected = mapOf(
            CarStateDto.UnknownOrError to ChargerState.Offline,
            CarStateDto.Idle to ChargerState.Free,
            CarStateDto.WaitCar to ChargerState.CarConnected,
            CarStateDto.Charging to ChargerState.Charging,
            CarStateDto.Complete to ChargerState.Complete,
            CarStateDto.Error to ChargerState.Error,
        )
        expected.forEach { (dto, state) -> assertEquals(state, ChargerStatusDto(carState = dto).toChargerStatus().state) }
    }

    @Test
    fun startIsOnlyPossibleWithConnectedCarAndStopOnlyWhileCharging() {
        val connected = ChargerStatusDto(carState = CarStateDto.WaitCar).toChargerStatus()
        val free = ChargerStatusDto(carState = CarStateDto.Idle).toChargerStatus()
        val charging = ChargerStatusDto(carState = CarStateDto.Charging, currentList = listOf(6, 16), current = 16).toChargerStatus()

        assertTrue(connected.canStart && connected.showStart && !connected.showStop)
        assertTrue(free.showStart && !free.canStart)
        assertTrue(charging.showStop)
        assertFalse(charging.showStart)
        assertEquals(listOf(6, 16), charging.availableCurrents)
    }
}
