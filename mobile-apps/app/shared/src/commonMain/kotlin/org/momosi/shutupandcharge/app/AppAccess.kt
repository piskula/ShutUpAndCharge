package org.momosi.shutupandcharge.app

import androidx.navigation3.runtime.NavKey
import org.momosi.shutupandcharge.feature.dashboard.api.DashboardNavKey
import org.momosi.shutupandcharge.feature.welcome.api.WelcomeNavKey
import org.momosi.shutupandcharge.user.model.AuthenticationState

internal enum class AppAccess {
    SignedOut,
    Ready,
}

internal fun appAccessOf(authenticationState: AuthenticationState): AppAccess = when (authenticationState) {
    AuthenticationState.Guest -> AppAccess.SignedOut
    AuthenticationState.Authorized -> AppAccess.Ready
}

internal fun AppAccess.startKey(): NavKey = when (this) {
    AppAccess.SignedOut -> WelcomeNavKey
    AppAccess.Ready -> DashboardNavKey
}

internal fun AppAccess.redirectFrom(currentKey: NavKey): NavKey? = when (this) {
    AppAccess.SignedOut -> WelcomeNavKey.takeUnless { currentKey == WelcomeNavKey }
    AppAccess.Ready -> DashboardNavKey.takeIf { currentKey == WelcomeNavKey }
}
