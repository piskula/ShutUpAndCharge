package org.momosi.shutupandcharge.core.network.di

import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.sse.SSE
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineScope
import kotlinx.serialization.json.Json
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.momosi.shutupandcharge.core.common.config.AppConfig
import org.momosi.shutupandcharge.core.common.language.LanguageRepository
import org.momosi.shutupandcharge.core.common.logger.Logger
import org.momosi.shutupandcharge.core.common.network.installApiExceptionMapping
import org.momosi.shutupandcharge.core.common.network.isTransientNetworkFailure
import org.momosi.shutupandcharge.core.network.AcceptLanguage
import org.momosi.shutupandcharge.core.network.httpClientEngineFactory
import org.momosi.shutupandcharge.core.network.installOptInRequestRetry
import org.momosi.shutupandcharge.core.network.invalidateAuthTokensOn
import org.momosi.shutupandcharge.user.AuthManager
import org.momosi.shutupandcharge.user.model.TokenResponse

val networkModule = module {
    single {
        Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
        }
    }

    single {
        val json: Json = get()
        val authManager: AuthManager = get()
        val appCoroutineScope: CoroutineScope = get()
        buildHttpClient(authManager, appCoroutineScope, get<LanguageRepository>()) {
            defaultRequest {
                url(get<AppConfig>().apiBaseUrl)
                contentType(ContentType.Application.Json)
            }
            install(ContentNegotiation) {
                json(json)
            }
        }
    }

    single {
        Ktorfit.Builder()
            .baseUrl(get<AppConfig>().apiBaseUrl)
            .httpClient(get<HttpClient>())
            .build()
    }

    single(named(SSE_HTTP_CLIENT)) {
        val authManager: AuthManager = get()
        val appCoroutineScope: CoroutineScope = get()
        buildHttpClient(authManager, appCoroutineScope, get<LanguageRepository>(), logLevel = LogLevel.HEADERS) {
            defaultRequest {
                url(get<AppConfig>().apiBaseUrl)
            }
            install(SSE)
            install(HttpTimeout) {
                socketTimeoutMillis = SSE_SOCKET_TIMEOUT_MILLIS
            }
        }
    }
}

const val SSE_HTTP_CLIENT = "sse"
private const val SSE_SOCKET_TIMEOUT_MILLIS = 30_000L

internal fun buildHttpClient(
    authManager: AuthManager,
    appCoroutineScope: CoroutineScope,
    languageRepository: LanguageRepository,
    engineFactory: HttpClientEngineFactory<*> = httpClientEngineFactory(),
    logLevel: LogLevel = LogLevel.BODY,
    block: HttpClientConfig<*>.() -> Unit = {},
): HttpClient {
    val client = HttpClient(engineFactory) {
        expectSuccess = true

        block()

        install(AcceptLanguage) {
            language {
                languageRepository.language.value.languageTag
            }
        }

        install(Auth) {
            bearer {
                loadTokens {
                    authManager.token().toBearerTokens()
                }
                refreshTokens {
                    authManager.token().toBearerTokens()
                }
            }
        }

        installOptInRequestRetry()

        install(Logging) {
            level = logLevel
            logger = object : io.ktor.client.plugins.logging.Logger {
                override fun log(message: String) {
                    Logger.d("Network", message)
                }
            }
        }

        installApiExceptionMapping { cause, request ->
            val response = (cause as? ResponseException)?.response
            val durationMs = response?.let { it.responseTime.timestamp - it.requestTime.timestamp }

            Logger.d(
                "Network",
                "Request failed: ${request.method.value} ${request.url.encodedPath}" +
                    (response?.let { " status=${it.status.value} durationMs=$durationMs" } ?: " (no response)"),
            )
            if (response?.status == HttpStatusCode.Unauthorized) {
                response.call.request.headers[HttpHeaders.Authorization]
                    ?.takeIf { it.startsWith("Bearer ") }
                    ?.removePrefix("Bearer ")
                    ?.let { authManager.invalidateSession(it) }
            }
        }
    }

    client.invalidateAuthTokensOn(authManager, appCoroutineScope)

    return client
}

private fun TokenResponse.toBearerTokens(): BearerTokens? = when (this) {
    is TokenResponse.Token -> BearerTokens(accessToken = token, refreshToken = "")

    is TokenResponse.TokenError -> {
        if (exception.isTransientNetworkFailure()) {
            Logger.w("Network", exception, "Token refresh failed, sending request unauthenticated (offline)")
        } else {
            Logger.e("Network", exception, "Token refresh failed, sending request unauthenticated")
        }
        null
    }

    TokenResponse.GuestWithoutToken -> null
}
