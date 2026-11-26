package org.momosi.shutupandcharge.app

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.momosi.shutupandcharge.core.common.crashreporting.CrashReporting
import org.momosi.shutupandcharge.core.common.network.ConnectivityManager
import org.momosi.shutupandcharge.user.UserRepository

class CrashReportingSync(userRepository: UserRepository, connectivityManager: ConnectivityManager, appCoroutineScope: CoroutineScope) {
    init {
        userRepository.user
            .onEach { user ->
                CrashReporting.setUserId(user?.id)
                CrashReporting.setCustomKey("userRole", user?.roles?.joinToString(",") { it.name }.orEmpty())
            }
            .launchIn(appCoroutineScope)

        connectivityManager.observeConnectionType()
            .onEach { CrashReporting.setCustomKey("connectionType", it.name) }
            .launchIn(appCoroutineScope)
    }
}
