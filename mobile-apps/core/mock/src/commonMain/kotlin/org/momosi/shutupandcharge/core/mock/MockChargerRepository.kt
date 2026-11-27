package org.momosi.shutupandcharge.core.mock

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.momosi.shutupandcharge.core.charger.ChargerRepository
import org.momosi.shutupandcharge.core.charger.model.ChargerState
import org.momosi.shutupandcharge.core.charger.model.ChargerStatus
import org.momosi.shutupandcharge.core.common.network.ApiException
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

private const val HTTP_CONFLICT = 409
private const val VOLTAGE = 230.0
private const val PHASES = 3

/** One real second simulates one charging minute so the kWh counter visibly moves. */
private const val SIMULATED_SECONDS_PER_TICK = 60.0
private const val SECONDS_PER_HOUR = 3600.0
private const val WATTS_PER_KW = 1000.0

internal val MOCK_AVAILABLE_CURRENTS = listOf(6, 10, 16)

/**
 * Charger state machine: Free -(refresh: car plugs in)-> CarConnected -(start)-> Charging -(stop)-> Complete
 * -(refresh: car leaves)-> Free. Stopping records a charging transaction for the demo account.
 */
internal class MockChargerRepository(private val backend: MockBackend, private val scope: CoroutineScope, private val tick: Duration = 1.seconds) : ChargerRepository {

    private val state = MutableStateFlow(
        ChargerStatus(state = ChargerState.Free, occupiedFrom = null, chargedKwh = 0.0, availableCurrents = MOCK_AVAILABLE_CURRENTS, current = MOCK_AVAILABLE_CURRENTS.last()),
    )
    private var chargingJob: Job? = null

    override val status: Flow<ChargerStatus> = state.asStateFlow()

    override suspend fun refresh(): Result<ChargerStatus> = mockCall {
        state.update { current ->
            when (current.state) {
                ChargerState.Free -> current.copy(state = ChargerState.CarConnected, occupiedFrom = backend.now(), chargedKwh = 0.0)
                ChargerState.Complete -> current.copy(state = ChargerState.Free, occupiedFrom = null, chargedKwh = 0.0)
                else -> current
            }
        }
        state.value
    }

    override suspend fun startCharging(): Result<ChargerStatus> = mockCall {
        if (state.value.state != ChargerState.CarConnected) throw ApiException.HttpError(HTTP_CONFLICT, "Connect the car first (pull to refresh in demo mode)")
        state.update { it.copy(state = ChargerState.Charging, chargedKwh = 0.0) }
        chargingJob?.cancel()
        chargingJob = scope.launch {
            while (isActive) {
                delay(tick)
                state.update { it.copy(chargedKwh = it.chargedKwh + kwhPerTick(it.current)) }
            }
        }
        state.value
    }

    override suspend fun stopCharging(): Result<ChargerStatus> = mockCall {
        if (state.value.state != ChargerState.Charging) throw ApiException.HttpError(HTTP_CONFLICT, "Charger is not charging")
        chargingJob?.cancel()
        chargingJob = null
        state.update { it.copy(state = ChargerState.Complete) }
        val kwh = roundCents(state.value.chargedKwh)
        if (kwh > 0) backend.recordCharging(DEMO_ACCOUNT_ID, kwh)
        state.value
    }

    override suspend fun setCurrent(current: Int): Result<Int> = mockCall {
        state.update { it.copy(current = current) }
        current
    }

    private fun kwhPerTick(current: Int): Double = current * VOLTAGE * PHASES / WATTS_PER_KW * SIMULATED_SECONDS_PER_TICK / SECONDS_PER_HOUR
}
