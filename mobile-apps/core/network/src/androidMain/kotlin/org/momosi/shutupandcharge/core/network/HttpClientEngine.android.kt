package org.momosi.shutupandcharge.core.network

import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.okhttp.OkHttp

actual fun httpClientEngineFactory(): HttpClientEngineFactory<*> = OkHttp
