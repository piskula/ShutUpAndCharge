package org.momosi.shutupandcharge.core.charger.di

import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.HttpClient
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.momosi.shutupandcharge.core.charger.ChargerRepository
import org.momosi.shutupandcharge.core.charger.ChargerRepositoryImpl
import org.momosi.shutupandcharge.core.charger.api.ChargerStatusStreamApi
import org.momosi.shutupandcharge.core.charger.api.createChargerApi
import org.momosi.shutupandcharge.core.network.di.SSE_HTTP_CLIENT

val chargerModule = module {
    single { get<Ktorfit>().createChargerApi() }
    single { ChargerStatusStreamApi(get<HttpClient>(named(SSE_HTTP_CLIENT)), get()) }
    single<ChargerRepository> { ChargerRepositoryImpl(get(), get(), get()) }
}
