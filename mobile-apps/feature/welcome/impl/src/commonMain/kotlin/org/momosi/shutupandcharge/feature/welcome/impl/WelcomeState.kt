package org.momosi.shutupandcharge.feature.welcome.impl

import androidx.compose.runtime.Immutable
import org.momosi.shutupandcharge.core.charger.model.ChargerStatus

@Immutable
data class WelcomeState(val charger: ChargerStatus = ChargerStatus.Unknown, val chargedKwhText: String = "", val isRefreshingCharger: Boolean = false, val signIn: SignInStatus = SignInStatus.Idle)

@Immutable
sealed interface SignInStatus {
    data object Idle : SignInStatus
    data object InProgress : SignInStatus

    sealed interface Failed : SignInStatus {
        data object NoInternet : Failed
        data object NoActivity : Failed
        data object Generic : Failed
    }
}
