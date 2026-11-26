package org.momosi.shutupandcharge.user.di

import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module
import org.momosi.shutupandcharge.user.internal.UserStorage
import org.momosi.shutupandcharge.user.internal.UserStorageImpl
import org.publicvalue.multiplatform.oidc.appsupport.IosCodeAuthFlowFactory
import org.publicvalue.multiplatform.oidc.flows.CodeAuthFlowFactory
import platform.Foundation.NSUserDefaults

actual fun platformUserModule(): Module = module {
    single<UserStorage> { UserStorageImpl(NSUserDefaults.standardUserDefaults) }
    single { IosCodeAuthFlowFactory() } bind CodeAuthFlowFactory::class
}
