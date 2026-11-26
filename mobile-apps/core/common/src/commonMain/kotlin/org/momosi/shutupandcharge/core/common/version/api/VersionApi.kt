package org.momosi.shutupandcharge.core.common.version.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.momosi.shutupandcharge.core.common.config.AppConfig
import org.momosi.shutupandcharge.core.common.network.installApiExceptionMapping
import org.momosi.shutupandcharge.dto.BuildInfoDto

class VersionApi(private val appConfig: AppConfig) {

    private val client by lazy {
        HttpClient {
            expectSuccess = true
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
            installApiExceptionMapping()
        }
    }

    suspend fun getBuildInfo(): BuildInfoDto = client.get("${appConfig.apiBaseUrl}info/version").body()
}
