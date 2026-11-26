package org.momosi.shutupandcharge.core.network

import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.HttpRequestRetry

fun HttpClientConfig<*>.installOptInRequestRetry() {
    install(HttpRequestRetry) { noRetry() }
}
