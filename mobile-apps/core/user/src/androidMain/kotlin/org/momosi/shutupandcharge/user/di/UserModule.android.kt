package org.momosi.shutupandcharge.user.di

import android.content.Context
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module
import org.momosi.shutupandcharge.user.internal.USER_AUTH_STORAGE
import org.momosi.shutupandcharge.user.internal.UserStorage
import org.momosi.shutupandcharge.user.internal.UserStorageImpl
import org.publicvalue.multiplatform.oidc.appsupport.AndroidCodeAuthFlowFactory
import org.publicvalue.multiplatform.oidc.flows.CodeAuthFlowFactory

actual fun platformUserModule(): Module = module {
    single(named(USER_AUTH_STORAGE)) {
        get<Context>().getSharedPreferences(USER_AUTH_STORAGE, Context.MODE_PRIVATE)
    }
    single<UserStorage> { UserStorageImpl(get(named(USER_AUTH_STORAGE))) }
    single { AndroidCodeAuthFlowFactory() } bind CodeAuthFlowFactory::class
}
