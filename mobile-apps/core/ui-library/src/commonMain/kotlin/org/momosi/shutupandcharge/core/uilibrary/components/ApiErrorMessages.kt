package org.momosi.shutupandcharge.core.uilibrary.components

import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.stringResource
import org.momosi.shutupandcharge.core.common.network.ApiException
import org.momosi.shutupandcharge.core.uilibrary.resources.Res
import org.momosi.shutupandcharge.core.uilibrary.resources.error_generic
import org.momosi.shutupandcharge.core.uilibrary.resources.error_no_connection
import org.momosi.shutupandcharge.core.uilibrary.resources.error_unauthorized

internal data class ApiErrorMessages(val noConnection: String, val unauthorized: String, val generic: String)

@Composable
internal fun rememberApiErrorMessages(): ApiErrorMessages = ApiErrorMessages(
    noConnection = stringResource(Res.string.error_no_connection),
    unauthorized = stringResource(Res.string.error_unauthorized),
    generic = stringResource(Res.string.error_generic),
)

internal fun Throwable.toMessage(messages: ApiErrorMessages): String = when (this) {
    is ApiException.NoConnection -> messages.noConnection
    is ApiException.Unauthorized -> messages.unauthorized
    is ApiException.HttpError -> serverMessage?.takeIf { statusCode in 400..499 } ?: messages.generic
    else -> messages.generic
}
