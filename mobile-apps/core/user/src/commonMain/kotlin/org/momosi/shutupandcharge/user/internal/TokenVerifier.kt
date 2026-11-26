package org.momosi.shutupandcharge.user.internal

import kotlinx.coroutines.withContext
import org.momosi.shutupandcharge.core.common.coroutines.DispatcherProvider
import org.momosi.shutupandcharge.core.common.coroutines.runCatchingCancellable
import org.momosi.shutupandcharge.core.common.logger.Logger
import org.momosi.shutupandcharge.core.common.network.onNetworkFailure
import org.momosi.shutupandcharge.core.common.network.onNoConnectionFailure
import org.momosi.shutupandcharge.user.internal.api.AuthApi
import org.publicvalue.multiplatform.oidc.types.Jwt
import kotlin.io.encoding.Base64

data class TokenVerificationException(override val message: String? = null, override val cause: Throwable? = null) : RuntimeException(message, cause)

class TokenVerifier(private val userStorage: UserStorage, private val authApi: AuthApi, private val dispatcherProvider: DispatcherProvider) {

    internal suspend fun verifyAndParse(accessToken: String, refreshPublicKey: Boolean = false): Result<Jwt> = withContext(dispatcherProvider.io()) {
        val currentPublicKey = userStorage.serverPublicKey
        val publicKey = if (refreshPublicKey || currentPublicKey == null) {
            runCatchingCancellable {
                authApi.getRealmInfo().publicKey
            }.onSuccess {
                userStorage.serverPublicKey = it
            }
                .onNoConnectionFailure { Logger.w("TokenVerifier", it, "Error fetching server public key (offline)") }
                .onNetworkFailure { Logger.e("TokenVerifier", it, "Error fetching server public key") }
                .getOrNull()
        } else {
            currentPublicKey
        }

        if (publicKey == null) {
            Result.failure(TokenVerificationException("Public key for token verification is not available"))
        } else {
            runCatchingCancellable {
                verifySignature(accessToken, publicKey)
            }.recoverWith {
                if (!refreshPublicKey) {
                    verifyAndParse(accessToken, refreshPublicKey = true)
                } else {
                    Result.failure(TokenVerificationException(cause = it))
                }
            }
        }
    }
}

private val base64Url = Base64.UrlSafe.withPadding(Base64.PaddingOption.ABSENT)

private fun verifySignature(compactJwt: String, publicKeyPem: String): Jwt {
    val jwt = Jwt.parse(compactJwt)
    val signatureB64 = jwt.signature ?: throw TokenVerificationException("Token has no signature")
    val signedData = compactJwt.substringBeforeLast('.').encodeToByteArray()
    val signatureBytes = base64Url.decode(signatureB64)
    val publicKeyDer = Base64.decode(publicKeyPem)

    if (!verifyRs256(signedData = signedData, signature = signatureBytes, publicKeyDer = publicKeyDer)) {
        throw TokenVerificationException("Invalid token signature")
    }
    return jwt
}

inline fun <T> Result<T>.recoverWith(transform: (Throwable) -> Result<T>): Result<T> = fold(
    onSuccess = { this },
    onFailure = { transform(it) },
)
