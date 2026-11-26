package org.momosi.shutupandcharge.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import org.momosi.shutupandcharge.core.common.error.ErrorReporter
import org.momosi.shutupandcharge.user.AuthManager
import org.momosi.shutupandcharge.user.UserRepository

class SuacViewModel(authManager: AuthManager, userRepository: UserRepository, errorReporter: ErrorReporter) : ViewModel() {

    internal val access: StateFlow<AppAccess> = authManager.authenticationState
        .map(::appAccessOf)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = appAccessOf(authManager.authenticationState.value),
        )

    internal val isAdmin: StateFlow<Boolean> = userRepository.user
        .map { it?.isAdmin == true }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = userRepository.user.value?.isAdmin == true,
        )

    val errors: Flow<Throwable> = errorReporter.errors
}
