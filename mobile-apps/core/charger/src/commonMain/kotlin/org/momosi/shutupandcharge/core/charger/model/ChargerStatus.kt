package org.momosi.shutupandcharge.core.charger.model

import kotlin.time.Instant

enum class ChargerState {
    Unknown,
    Offline,
    Free,
    CarConnected,
    Charging,
    Complete,
    Error,
}

data class ChargerStatus(val state: ChargerState, val occupiedFrom: Instant?, val chargedKwh: Double, val availableCurrents: List<Int>, val current: Int) {
    val canStart: Boolean get() = state == ChargerState.CarConnected
    val showStart: Boolean get() = state != ChargerState.Charging && state != ChargerState.Complete
    val showStop: Boolean get() = state == ChargerState.Charging

    companion object {
        val Unknown = ChargerStatus(
            state = ChargerState.Unknown,
            occupiedFrom = null,
            chargedKwh = 0.0,
            availableCurrents = emptyList(),
            current = 0,
        )
    }
}
