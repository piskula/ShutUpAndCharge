package org.momosi.shutupandcharge.user.di

import org.koin.core.module.Module
import org.koin.dsl.module
import org.momosi.shutupandcharge.core.common.config.AppConfig
import org.momosi.shutupandcharge.user.AuthManager
import org.momosi.shutupandcharge.user.AuthManagerImpl
import org.momosi.shutupandcharge.user.UserRepository
import org.momosi.shutupandcharge.user.UserRepositoryImpl
import org.momosi.shutupandcharge.user.internal.TokenVerifier
import org.momosi.shutupandcharge.user.internal.api.AuthApi
import org.publicvalue.multiplatform.oidc.OpenIdConnectClient

expect fun platformUserModule(): Module

val userModule = module {
    single {
        val appConfig: AppConfig = get()
        OpenIdConnectClient(discoveryUri = "${appConfig.realmUrl}.well-known/openid-configuration") {
            clientId = appConfig.oauthClientId
            scope = "openid offline_access"
            redirectUri = appConfig.oauthRedirectUrl
            postLogoutRedirectUri = appConfig.oauthLogoutRedirectUrl
        }
    }
    single { AuthApi(get()) }
    single { TokenVerifier(get(), get(), get()) }
    single<UserRepository> { UserRepositoryImpl(get()) }
    single<AuthManager> { AuthManagerImpl(get(), get(), get(), get(), get(), get()) }
}
