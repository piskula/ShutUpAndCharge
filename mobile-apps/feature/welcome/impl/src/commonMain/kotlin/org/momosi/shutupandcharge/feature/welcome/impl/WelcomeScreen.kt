package org.momosi.shutupandcharge.feature.welcome.impl

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.momosi.shutupandcharge.core.charger.model.ChargerState
import org.momosi.shutupandcharge.core.charger.model.ChargerStatus
import org.momosi.shutupandcharge.core.uilibrary.charger.ChargerStatusCard
import org.momosi.shutupandcharge.core.uilibrary.components.Button
import org.momosi.shutupandcharge.core.uilibrary.components.Text
import org.momosi.shutupandcharge.core.uilibrary.illustrations.SuacIllustrations
import org.momosi.shutupandcharge.core.uilibrary.theme.AppTheme
import org.momosi.shutupandcharge.core.uilibrary.theme.SuacTheme
import org.momosi.shutupandcharge.feature.welcome.impl.resources.Res
import org.momosi.shutupandcharge.feature.welcome.impl.resources.welcome_app_name
import org.momosi.shutupandcharge.feature.welcome.impl.resources.welcome_error_failed
import org.momosi.shutupandcharge.feature.welcome.impl.resources.welcome_error_no_internet
import org.momosi.shutupandcharge.feature.welcome.impl.resources.welcome_error_unable_to_start
import org.momosi.shutupandcharge.feature.welcome.impl.resources.welcome_sign_in
import org.momosi.shutupandcharge.feature.welcome.impl.resources.welcome_subtitle

@Composable
internal fun WelcomeScreen(viewModel: WelcomeViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    WelcomeContent(state = state, onAction = viewModel::onAction)
}

@Composable
private fun WelcomeContent(
    state: WelcomeState,
    onAction: (WelcomeAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val errorMessage = when (state.signIn) {
        SignInStatus.Failed.NoInternet -> stringResource(Res.string.welcome_error_no_internet)
        SignInStatus.Failed.NoActivity -> stringResource(Res.string.welcome_error_unable_to_start)
        SignInStatus.Failed.Generic -> stringResource(Res.string.welcome_error_failed)
        else -> null
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(AppTheme.colors.primaryContainer, AppTheme.colors.surface),
                ),
            ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Image(
                painter = painterResource(SuacIllustrations.Charger),
                contentDescription = null,
                modifier = Modifier.size(180.dp),
            )
            Text(
                text = stringResource(Res.string.welcome_app_name),
                style = AppTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = AppTheme.colors.onSurface,
                textAlign = TextAlign.Center,
            )
            Text(
                text = stringResource(Res.string.welcome_subtitle),
                style = AppTheme.typography.bodyMedium,
                color = AppTheme.colors.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
            ChargerStatusCard(
                status = state.charger,
                chargedKwhText = state.chargedKwhText,
                occupiedSinceText = null,
                isSignedIn = false,
                isRefreshing = state.isRefreshingCharger,
                isActionInProgress = false,
                isCurrentChangeInProgress = false,
                onRefresh = { onAction(WelcomeAction.RefreshCharger) },
                onStart = {},
                onStop = {},
                onSelectCurrent = {},
            )
            Button(
                text = stringResource(Res.string.welcome_sign_in),
                onClick = { onAction(WelcomeAction.SignIn) },
                loading = state.signIn == SignInStatus.InProgress,
                modifier = Modifier.fillMaxWidth().testTag("sign_in_button"),
            )
            if (errorMessage != null) {
                Text(
                    text = errorMessage,
                    style = AppTheme.typography.bodySmall,
                    color = AppTheme.colors.error,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Preview
@Composable
private fun WelcomeContentPreview() {
    SuacTheme {
        WelcomeContent(
            state = WelcomeState(
                charger = ChargerStatus.Unknown.copy(state = ChargerState.Free),
                chargedKwhText = "0.00 kWh",
            ),
            onAction = {},
        )
    }
}

@Preview
@Composable
private fun WelcomeContentErrorPreview() {
    SuacTheme(isDarkTheme = true) {
        WelcomeContent(
            state = WelcomeState(
                charger = ChargerStatus.Unknown.copy(state = ChargerState.Charging, chargedKwh = 7.5),
                chargedKwhText = "7.50 kWh",
                signIn = SignInStatus.Failed.NoInternet,
            ),
            onAction = {},
        )
    }
}
