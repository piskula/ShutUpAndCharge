package org.momosi.shutupandcharge.feature.welcome.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.momosi.shutupandcharge.core.charger.ChargerRepository
import org.momosi.shutupandcharge.core.charger.model.ChargerStatus
import org.momosi.shutupandcharge.core.common.error.ErrorReporter
import org.momosi.shutupandcharge.core.common.formatter.NumberFormatter
import org.momosi.shutupandcharge.core.common.logger.Logger
import org.momosi.shutupandcharge.user.AuthManager
import org.momosi.shutupandcharge.user.model.AuthActionResult

class WelcomeViewModel(private val authManager: AuthManager, private val chargerRepository: ChargerRepository, private val numberFormatter: NumberFormatter, private val errorReporter: ErrorReporter) : ViewModel() {

    private val signIn = MutableStateFlow<SignInStatus>(SignInStatus.Idle)
    private val isRefreshingCharger = MutableStateFlow(false)

    val state: StateFlow<WelcomeState> = combine(
        chargerRepository.status.onStart { emit(ChargerStatus.Unknown) },
        isRefreshingCharger,
        signIn,
    ) { charger, refreshing, signInStatus ->
        WelcomeState(
            charger = charger,
            chargedKwhText = numberFormatter.formatKwh(charger.chargedKwh),
            isRefreshingCharger = refreshing,
            signIn = signInStatus,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = WelcomeState(chargedKwhText = numberFormatter.formatKwh(0.0)),
    )

    fun onAction(action: WelcomeAction) {
        Logger.d("Action:Welcome", action.toString())
        when (action) {
            WelcomeAction.SignIn -> signIn()
            WelcomeAction.RefreshCharger -> refreshCharger()
        }
    }

    private fun signIn() {
        if (signIn.value == SignInStatus.InProgress) return
        signIn.value = SignInStatus.InProgress
        viewModelScope.launch {
            signIn.value = when (authManager.signIn()) {
                is AuthActionResult.Success -> SignInStatus.Idle
                AuthActionResult.Failed.UserCancelled -> SignInStatus.Idle
                AuthActionResult.Failed.NoInternet -> SignInStatus.Failed.NoInternet
                AuthActionResult.Failed.NoActivity -> SignInStatus.Failed.NoActivity
                is AuthActionResult.Failed.Error -> SignInStatus.Failed.Generic
            }
        }
    }

    private fun refreshCharger() {
        if (isRefreshingCharger.value) return
        viewModelScope.launch {
            isRefreshingCharger.value = true
            chargerRepository.refresh().onFailure { errorReporter.report(it) }
            isRefreshingCharger.value = false
        }
    }
}
