package org.momosi.shutupandcharge.core.charger

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.shareIn
import org.momosi.shutupandcharge.core.charger.api.ChargerApi
import org.momosi.shutupandcharge.core.charger.api.ChargerStatusStreamApi
import org.momosi.shutupandcharge.core.charger.dto.toChargerStatus
import org.momosi.shutupandcharge.core.charger.model.ChargerStatus
import org.momosi.shutupandcharge.core.common.coroutines.runCatchingCancellable
import org.momosi.shutupandcharge.core.common.logger.Logger
import org.momosi.shutupandcharge.core.common.network.isTransientNetworkFailure
import org.momosi.shutupandcharge.core.common.network.onNetworkFailure
import org.momosi.shutupandcharge.core.common.network.onNoConnectionFailure
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

private const val TAG = "ChargerRepository"
private const val STOP_SHARING_TIMEOUT_MILLIS = 5_000L

internal class ChargerRepositoryImpl(private val chargerApi: ChargerApi, private val streamApi: ChargerStatusStreamApi, appCoroutineScope: CoroutineScope, private val reconnectDelay: Duration = 5.seconds) : ChargerRepository {

    private val actionUpdates = MutableSharedFlow<ChargerStatus>(extraBufferCapacity = 1)

    override val status: Flow<ChargerStatus> = merge(liveStatus(), actionUpdates)
        .shareIn(appCoroutineScope, SharingStarted.WhileSubscribed(STOP_SHARING_TIMEOUT_MILLIS), replay = 1)

    private fun liveStatus(): Flow<ChargerStatus> = flow {
        while (true) {
            runCatchingCancellable {
                streamApi.stream().map { it.toChargerStatus() }.collect { emit(it) }
            }.onFailure { logStreamFailure(it) }
            refresh().onSuccess { emit(it) }
            delay(reconnectDelay)
        }
    }

    override suspend fun refresh(): Result<ChargerStatus> = runCatchingCancellable {
        chargerApi.getChargerStatus().toChargerStatus()
    }
        .onSuccess { actionUpdates.tryEmit(it) }
        .onNoConnectionFailure { Logger.w(TAG, it, "Failed to load charger status (offline)") }
        .onNetworkFailure { Logger.e(TAG, it, "Failed to load charger status") }

    override suspend fun startCharging(): Result<ChargerStatus> = runCatchingCancellable {
        chargerApi.startCharging().toChargerStatus()
    }
        .onSuccess { actionUpdates.tryEmit(it) }
        .onNoConnectionFailure { Logger.w(TAG, it, "Failed to start charging (offline)") }
        .onNetworkFailure { Logger.e(TAG, it, "Failed to start charging") }

    override suspend fun stopCharging(): Result<ChargerStatus> = runCatchingCancellable {
        chargerApi.stopCharging().toChargerStatus()
    }
        .onSuccess { actionUpdates.tryEmit(it) }
        .onNoConnectionFailure { Logger.w(TAG, it, "Failed to stop charging (offline)") }
        .onNetworkFailure { Logger.e(TAG, it, "Failed to stop charging") }

    override suspend fun setCurrent(current: Int): Result<Int> = runCatchingCancellable {
        chargerApi.setChargingParams(current)
    }
        .onNoConnectionFailure { Logger.w(TAG, it, "Failed to set charging current (offline)") }
        .onNetworkFailure { Logger.e(TAG, it, "Failed to set charging current") }

    private fun logStreamFailure(throwable: Throwable) {
        if (throwable.isTransientNetworkFailure()) {
            Logger.w(TAG, throwable, "Charger status stream disconnected (offline)")
        } else {
            Logger.w(TAG, throwable, "Charger status stream disconnected")
        }
    }
}
