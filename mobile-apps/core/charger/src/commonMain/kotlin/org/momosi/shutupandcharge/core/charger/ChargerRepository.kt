package org.momosi.shutupandcharge.core.charger

import kotlinx.coroutines.flow.Flow
import org.momosi.shutupandcharge.core.charger.model.ChargerStatus

interface ChargerRepository {

    val status: Flow<ChargerStatus>

    suspend fun refresh(): Result<ChargerStatus>

    suspend fun startCharging(): Result<ChargerStatus>

    suspend fun stopCharging(): Result<ChargerStatus>

    suspend fun setCurrent(current: Int): Result<Int>
}
