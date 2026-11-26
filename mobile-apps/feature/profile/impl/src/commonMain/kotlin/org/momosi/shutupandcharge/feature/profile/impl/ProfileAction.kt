package org.momosi.shutupandcharge.feature.profile.impl

import org.momosi.shutupandcharge.core.common.language.AppLanguage

sealed interface ProfileAction {
    data object SignOut : ProfileAction
    data object ShowVersionInfo : ProfileAction
    data object DismissVersionInfo : ProfileAction
    data object ShowLanguageSelector : ProfileAction
    data object DismissLanguageSelector : ProfileAction
    data class SelectLanguage(val language: AppLanguage) : ProfileAction
    data object DismissRestartNotice : ProfileAction
}
