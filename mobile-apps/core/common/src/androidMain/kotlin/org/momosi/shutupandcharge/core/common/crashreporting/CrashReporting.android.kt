package org.momosi.shutupandcharge.core.common.crashreporting

import android.content.Context
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.initialize
import org.koin.core.context.GlobalContext

internal actual fun initializeFirebase(): Boolean = Firebase.initialize(GlobalContext.get().get<Context>()) != null
