package org.momosi.shutupandcharge.feature.welcome.impl

sealed interface WelcomeAction {
    data object SignIn : WelcomeAction
    data object RefreshCharger : WelcomeAction
}
