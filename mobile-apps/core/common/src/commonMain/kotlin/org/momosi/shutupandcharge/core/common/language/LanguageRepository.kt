package org.momosi.shutupandcharge.core.common.language

import kotlinx.coroutines.flow.StateFlow

interface LanguageRepository {
    val language: StateFlow<AppLanguage>
    val requiresRestartToApply: Boolean

    fun setLanguage(language: AppLanguage)
}
