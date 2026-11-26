package org.momosi.shutupandcharge.core.common.crashreporting

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.crashlytics.crashlytics
import org.momosi.shutupandcharge.core.common.logger.Logger

object CrashReporting {
    private var enabled = false

    fun init() {
        enabled = initializeFirebase()
        if (!enabled) {
            Logger.w("CrashReporting", "Firebase is not configured, crash reporting is disabled")
            return
        }
        Firebase.crashlytics.setCrashlyticsCollectionEnabled(true)
        Logger.addWriter(CrashlyticsLogWriter())
    }

    fun setUserId(userId: String?) {
        if (enabled) Firebase.crashlytics.setUserId(userId.orEmpty())
    }

    fun setCustomKey(key: String, value: String) {
        if (enabled) Firebase.crashlytics.setCustomKey(key, value)
    }
}

internal expect fun initializeFirebase(): Boolean
