package org.momosi.shutupandcharge.feature.profile.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.momosi.shutupandcharge.core.common.coroutines.combine
import org.momosi.shutupandcharge.core.common.error.ErrorReporter
import org.momosi.shutupandcharge.core.common.formatter.DateFormatter
import org.momosi.shutupandcharge.core.common.language.AppLanguage
import org.momosi.shutupandcharge.core.common.language.LanguageRepository
import org.momosi.shutupandcharge.core.common.logger.Logger
import org.momosi.shutupandcharge.core.common.network.ApiException
import org.momosi.shutupandcharge.core.common.version.AppVersionProvider
import org.momosi.shutupandcharge.core.common.version.VersionRepository
import org.momosi.shutupandcharge.user.AuthManager
import org.momosi.shutupandcharge.user.UserRepository
import org.momosi.shutupandcharge.user.model.AuthActionResult

class ProfileViewModel(
    userRepository: UserRepository,
    private val authManager: AuthManager,
    private val versionRepository: VersionRepository,
    appVersionProvider: AppVersionProvider,
    private val dateFormatter: DateFormatter,
    private val languageRepository: LanguageRepository,
    private val errorReporter: ErrorReporter,
) : ViewModel() {

    private val appVersion = "${appVersionProvider.versionName} (${appVersionProvider.versionCode})"

    private val isSigningOut = MutableStateFlow(false)
    private val isVersionDialogVisible = MutableStateFlow(false)
    private val serverVersion = MutableStateFlow<ServerVersionContent>(ServerVersionContent.Loading)
    private val isLanguageDialogVisible = MutableStateFlow(false)
    private val isRestartNoticeVisible = MutableStateFlow(false)

    val state: StateFlow<ProfileState> = combine(
        userRepository.user,
        isSigningOut,
        isVersionDialogVisible,
        serverVersion,
        languageRepository.language,
        isLanguageDialogVisible,
        isRestartNoticeVisible,
    ) { user, signingOut, versionDialogVisible, serverVersionState, language, languageDialogVisible, restartNoticeVisible ->
        ProfileState(
            user = user,
            isSigningOut = signingOut,
            appVersion = appVersion,
            isVersionDialogVisible = versionDialogVisible,
            serverVersion = serverVersionState,
            language = language,
            isLanguageDialogVisible = languageDialogVisible,
            isRestartNoticeVisible = restartNoticeVisible,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ProfileState(appVersion = appVersion),
    )

    fun onAction(action: ProfileAction) {
        Logger.d("Action:Profile", action.toString())
        when (action) {
            ProfileAction.SignOut -> signOut()
            ProfileAction.ShowVersionInfo -> showVersionInfo()
            ProfileAction.DismissVersionInfo -> isVersionDialogVisible.value = false
            ProfileAction.ShowLanguageSelector -> isLanguageDialogVisible.value = true
            ProfileAction.DismissLanguageSelector -> isLanguageDialogVisible.value = false
            is ProfileAction.SelectLanguage -> selectLanguage(action.language)
            ProfileAction.DismissRestartNotice -> isRestartNoticeVisible.value = false
        }
    }

    private fun selectLanguage(language: AppLanguage) {
        languageRepository.setLanguage(language)
        isLanguageDialogVisible.value = false
        if (languageRepository.requiresRestartToApply) {
            isRestartNoticeVisible.value = true
        }
    }

    private fun showVersionInfo() {
        isVersionDialogVisible.value = true
        if (serverVersion.value !is ServerVersionContent.Loaded) {
            serverVersion.value = ServerVersionContent.Loading
            viewModelScope.launch {
                serverVersion.value = versionRepository.getServerVersion().fold(
                    onSuccess = { version ->
                        val builtAt = version.builtAt?.let(dateFormatter::formatShortDate)
                        ServerVersionContent.Loaded(listOfNotNull(version.version, builtAt).joinToString(" · "))
                    },
                    onFailure = { ServerVersionContent.Error },
                )
            }
        }
    }

    private fun signOut() {
        if (isSigningOut.value) return
        viewModelScope.launch {
            isSigningOut.value = true
            val result = authManager.signOut()
            isSigningOut.value = false
            if (result == AuthActionResult.Failed.NoInternet) errorReporter.report(ApiException.NoConnection)
        }
    }
}
