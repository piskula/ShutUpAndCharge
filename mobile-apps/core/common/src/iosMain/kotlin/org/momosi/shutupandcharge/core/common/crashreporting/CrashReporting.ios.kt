package org.momosi.shutupandcharge.core.common.crashreporting

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.initialize
import platform.Foundation.NSBundle

internal actual fun initializeFirebase(): Boolean {
    if (NSBundle.mainBundle.pathForResource("GoogleService-Info", ofType = "plist") == null) return false
    Firebase.initialize()
    return true
}
