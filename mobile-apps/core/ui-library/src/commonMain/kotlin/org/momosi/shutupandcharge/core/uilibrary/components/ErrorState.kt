@file:Suppress("FunctionNaming")

package org.momosi.shutupandcharge.core.uilibrary.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.momosi.shutupandcharge.core.common.network.FailureReason
import org.momosi.shutupandcharge.core.uilibrary.illustrations.SuacIllustrations
import org.momosi.shutupandcharge.core.uilibrary.resources.Res
import org.momosi.shutupandcharge.core.uilibrary.resources.error_no_connection
import org.momosi.shutupandcharge.core.uilibrary.resources.retry
import org.momosi.shutupandcharge.core.uilibrary.theme.AppTheme
import org.momosi.shutupandcharge.core.uilibrary.theme.SuacTheme

@Composable
fun ErrorState(
    reason: FailureReason,
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    retryButtonModifier: Modifier = Modifier,
) {
    val isNoConnection = reason == FailureReason.NoConnection
    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp, vertical = 24.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .heightIn(min = maxHeight),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Image(
                painter = painterResource(
                    if (isNoConnection) {
                        SuacIllustrations.NoConnection
                    } else {
                        SuacIllustrations.GenericError
                    },
                ),
                contentDescription = null,
                modifier = Modifier.size(200.dp),
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = if (isNoConnection) stringResource(Res.string.error_no_connection) else message,
                style = AppTheme.typography.bodyLarge,
                color = AppTheme.colors.onSurface,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                text = stringResource(Res.string.retry),
                onClick = onRetry,
                modifier = retryButtonModifier,
            )
        }
    }
}

@Preview(widthDp = 360, heightDp = 640)
@Preview(name = "Large text", widthDp = 320, heightDp = 480, fontScale = 2f)
@Preview(name = "Landscape", widthDp = 640, heightDp = 320)
@Composable
private fun ErrorStatePreview() {
    SuacTheme {
        Box(modifier = Modifier.background(AppTheme.colors.surface)) {
            ErrorState(
                reason = FailureReason.Other,
                message = "Could not load transactions. Please try again.",
                onRetry = {},
            )
        }
    }
}

@Preview(widthDp = 360, heightDp = 640)
@Composable
private fun ErrorStateNoConnectionPreview() {
    SuacTheme {
        Box(modifier = Modifier.background(AppTheme.colors.surface)) {
            ErrorState(
                reason = FailureReason.NoConnection,
                message = "Could not load transactions. Please try again.",
                onRetry = {},
            )
        }
    }
}

@Preview(widthDp = 360, heightDp = 640)
@Composable
private fun ErrorStateDarkPreview() {
    SuacTheme(isDarkTheme = true) {
        Box(modifier = Modifier.background(AppTheme.colors.surface)) {
            ErrorState(
                reason = FailureReason.Other,
                message = "Could not load transactions. Please try again.",
                onRetry = {},
            )
        }
    }
}

@Preview(widthDp = 360, heightDp = 640)
@Composable
private fun ErrorStateNoConnectionDarkPreview() {
    SuacTheme(isDarkTheme = true) {
        Box(modifier = Modifier.background(AppTheme.colors.surface)) {
            ErrorState(
                reason = FailureReason.NoConnection,
                message = "Could not load transactions. Please try again.",
                onRetry = {},
            )
        }
    }
}
