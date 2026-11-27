package org.momosi.shutupandcharge.core.uilibrary.charger

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import org.momosi.shutupandcharge.core.charger.model.ChargerState
import org.momosi.shutupandcharge.core.charger.model.ChargerStatus
import org.momosi.shutupandcharge.core.uilibrary.components.Button
import org.momosi.shutupandcharge.core.uilibrary.components.ButtonRole
import org.momosi.shutupandcharge.core.uilibrary.components.ButtonStyle
import org.momosi.shutupandcharge.core.uilibrary.components.Chip
import org.momosi.shutupandcharge.core.uilibrary.components.Icon
import org.momosi.shutupandcharge.core.uilibrary.components.IconButton
import org.momosi.shutupandcharge.core.uilibrary.components.IconButtonStyle
import org.momosi.shutupandcharge.core.uilibrary.components.Text
import org.momosi.shutupandcharge.core.uilibrary.icons.SuacIcons
import org.momosi.shutupandcharge.core.uilibrary.modifier.card
import org.momosi.shutupandcharge.core.uilibrary.resources.Res
import org.momosi.shutupandcharge.core.uilibrary.resources.charger_check_connection
import org.momosi.shutupandcharge.core.uilibrary.resources.charger_current_label
import org.momosi.shutupandcharge.core.uilibrary.resources.charger_current_value
import org.momosi.shutupandcharge.core.uilibrary.resources.charger_description_charging
import org.momosi.shutupandcharge.core.uilibrary.resources.charger_description_complete
import org.momosi.shutupandcharge.core.uilibrary.resources.charger_description_connected
import org.momosi.shutupandcharge.core.uilibrary.resources.charger_description_error
import org.momosi.shutupandcharge.core.uilibrary.resources.charger_description_free
import org.momosi.shutupandcharge.core.uilibrary.resources.charger_description_offline
import org.momosi.shutupandcharge.core.uilibrary.resources.charger_description_unknown
import org.momosi.shutupandcharge.core.uilibrary.resources.charger_occupied_since
import org.momosi.shutupandcharge.core.uilibrary.resources.charger_refresh
import org.momosi.shutupandcharge.core.uilibrary.resources.charger_sign_in_hint
import org.momosi.shutupandcharge.core.uilibrary.resources.charger_start
import org.momosi.shutupandcharge.core.uilibrary.resources.charger_state_charging
import org.momosi.shutupandcharge.core.uilibrary.resources.charger_state_complete
import org.momosi.shutupandcharge.core.uilibrary.resources.charger_state_connected
import org.momosi.shutupandcharge.core.uilibrary.resources.charger_state_error
import org.momosi.shutupandcharge.core.uilibrary.resources.charger_state_free
import org.momosi.shutupandcharge.core.uilibrary.resources.charger_state_offline
import org.momosi.shutupandcharge.core.uilibrary.resources.charger_state_unknown
import org.momosi.shutupandcharge.core.uilibrary.resources.charger_station_name
import org.momosi.shutupandcharge.core.uilibrary.resources.charger_stop
import org.momosi.shutupandcharge.core.uilibrary.theme.AppTheme
import org.momosi.shutupandcharge.core.uilibrary.theme.SuacTheme

@Composable
fun ChargerStatusCard(
    status: ChargerStatus,
    chargedKwhText: String,
    occupiedSinceText: String?,
    isSignedIn: Boolean,
    isRefreshing: Boolean,
    isActionInProgress: Boolean,
    isCurrentChangeInProgress: Boolean,
    onRefresh: () -> Unit,
    onStart: () -> Unit,
    onStop: () -> Unit,
    onSelectCurrent: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .card()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = SuacIcons.EvStation, tint = AppTheme.colors.primary)
            Text(
                text = stringResource(Res.string.charger_station_name),
                style = AppTheme.typography.titleLarge,
                color = AppTheme.colors.onSurface,
                modifier = Modifier.weight(1f).padding(start = 8.dp),
            )
            if (isRefreshing) {
                CircularProgressIndicator(modifier = Modifier.padding(12.dp).size(24.dp), strokeWidth = 2.dp, color = AppTheme.colors.primary)
            } else {
                IconButton(
                    imageVector = SuacIcons.Refresh,
                    onClick = onRefresh,
                    style = IconButtonStyle.StandardMuted,
                    contentDescription = stringResource(Res.string.charger_refresh),
                    modifier = Modifier.testTag("charger_refresh_button"),
                )
            }
        }

        StatusPill(status = status, chargedKwhText = chargedKwhText)

        Text(
            text = status.state.description(),
            style = AppTheme.typography.bodyMedium,
            color = AppTheme.colors.onSurfaceVariant,
        )

        if (occupiedSinceText != null && status.state == ChargerState.Charging) {
            Text(
                text = stringResource(Res.string.charger_occupied_since, occupiedSinceText),
                style = AppTheme.typography.bodySmall,
                color = AppTheme.colors.onSurfaceVariant,
            )
        }

        if (isSignedIn) {
            if (status.showStop && status.availableCurrents.isNotEmpty()) {
                CurrentSelector(
                    status = status,
                    isChangeInProgress = isCurrentChangeInProgress || isRefreshing,
                    onSelectCurrent = onSelectCurrent,
                )
            }
            if (status.showStart) {
                if (status.canStart) {
                    Button(
                        text = stringResource(Res.string.charger_start),
                        onClick = onStart,
                        loading = isActionInProgress,
                        icon = SuacIcons.StartCharging,
                        modifier = Modifier.fillMaxWidth().testTag("charger_start_button"),
                    )
                } else {
                    Button(
                        text = stringResource(if (status.state == ChargerState.Free) Res.string.charger_check_connection else Res.string.charger_refresh),
                        onClick = onRefresh,
                        loading = isRefreshing,
                        icon = SuacIcons.Refresh,
                        style = if (status.state == ChargerState.Free) ButtonStyle.Filled else ButtonStyle.Tonal,
                        modifier = Modifier.fillMaxWidth().testTag("charger_check_connection_button"),
                    )
                }
            }
            if (status.showStop) {
                Button(
                    text = stringResource(Res.string.charger_stop),
                    onClick = onStop,
                    loading = isActionInProgress,
                    icon = SuacIcons.StopCharging,
                    role = ButtonRole.Warning,
                    modifier = Modifier.fillMaxWidth().testTag("charger_stop_button"),
                )
            }
        } else {
            Text(
                text = stringResource(Res.string.charger_sign_in_hint),
                style = AppTheme.typography.bodySmall,
                color = AppTheme.colors.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun StatusPill(status: ChargerStatus, chargedKwhText: String) {
    val (container, content) = status.state.colors()
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .background(container, RoundedCornerShape(50))
            .padding(horizontal = 14.dp, vertical = 8.dp)
            .testTag("charger_status_pill"),
    ) {
        Icon(imageVector = status.state.icon(), tint = content, modifier = Modifier.size(20.dp))
        Text(text = status.state.label(chargedKwhText), style = AppTheme.typography.labelLarge, color = content)
    }
}

@Composable
private fun CurrentSelector(
    status: ChargerStatus,
    isChangeInProgress: Boolean,
    onSelectCurrent: (Int) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(Res.string.charger_current_label),
                style = AppTheme.typography.labelLarge,
                color = AppTheme.colors.onSurfaceVariant,
            )
            if (isChangeInProgress) {
                CircularProgressIndicator(modifier = Modifier.padding(start = 8.dp).size(16.dp), strokeWidth = 2.dp, color = AppTheme.colors.primary)
            }
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.horizontalScroll(rememberScrollState()),
        ) {
            status.availableCurrents.forEach { amps ->
                Chip(
                    label = stringResource(Res.string.charger_current_value, amps),
                    selected = amps == status.current,
                    onClick = { if (!isChangeInProgress && amps != status.current) onSelectCurrent(amps) },
                    modifier = Modifier.testTag("charger_current_$amps"),
                )
            }
        }
    }
}

@Composable
private fun ChargerState.colors(): Pair<Color, Color> = when (this) {
    ChargerState.Free -> AppTheme.colors.positiveContainer to AppTheme.colors.onPositiveContainer
    ChargerState.CarConnected -> AppTheme.colors.primaryContainer to AppTheme.colors.onPrimaryContainer
    ChargerState.Charging -> AppTheme.colors.warningContainer to AppTheme.colors.onWarningContainer
    ChargerState.Complete -> AppTheme.colors.primaryContainer to AppTheme.colors.onPrimaryContainer
    ChargerState.Offline, ChargerState.Error -> AppTheme.colors.errorContainer to AppTheme.colors.onErrorContainer
    ChargerState.Unknown -> AppTheme.colors.surfaceVariant to AppTheme.colors.onSurfaceVariant
}

private fun ChargerState.icon(): ImageVector = when (this) {
    ChargerState.Free -> SuacIcons.CheckCircle
    ChargerState.CarConnected -> SuacIcons.CarConnected
    ChargerState.Charging -> SuacIcons.Power
    ChargerState.Complete -> SuacIcons.PowerOff
    ChargerState.Offline -> SuacIcons.Offline
    ChargerState.Error -> SuacIcons.Error
    ChargerState.Unknown -> SuacIcons.UnknownState
}

@Composable
private fun ChargerState.label(chargedKwhText: String): String = when (this) {
    ChargerState.Unknown -> stringResource(Res.string.charger_state_unknown)
    ChargerState.Offline -> stringResource(Res.string.charger_state_offline)
    ChargerState.Free -> stringResource(Res.string.charger_state_free)
    ChargerState.CarConnected -> stringResource(Res.string.charger_state_connected)
    ChargerState.Charging -> stringResource(Res.string.charger_state_charging, chargedKwhText)
    ChargerState.Complete -> stringResource(Res.string.charger_state_complete, chargedKwhText)
    ChargerState.Error -> stringResource(Res.string.charger_state_error)
}

@Composable
private fun ChargerState.description(): String = when (this) {
    ChargerState.Unknown -> stringResource(Res.string.charger_description_unknown)
    ChargerState.Offline -> stringResource(Res.string.charger_description_offline)
    ChargerState.Free -> stringResource(Res.string.charger_description_free)
    ChargerState.CarConnected -> stringResource(Res.string.charger_description_connected)
    ChargerState.Charging -> stringResource(Res.string.charger_description_charging)
    ChargerState.Complete -> stringResource(Res.string.charger_description_complete)
    ChargerState.Error -> stringResource(Res.string.charger_description_error)
}

private val PreviewCharging = ChargerStatus(
    state = ChargerState.Charging,
    occupiedFrom = null,
    chargedKwh = 12.4,
    availableCurrents = listOf(6, 10, 16),
    current = 10,
)

@Preview
@Composable
private fun ChargerStatusCardChargingPreview() {
    SuacTheme {
        ChargerStatusCard(
            status = PreviewCharging,
            chargedKwhText = "12.40 kWh",
            occupiedSinceText = "18:42",
            isSignedIn = true,
            isRefreshing = false,
            isActionInProgress = false,
            isCurrentChangeInProgress = false,
            onRefresh = {},
            onStart = {},
            onStop = {},
            onSelectCurrent = {},
        )
    }
}

@Preview
@Composable
private fun ChargerStatusCardConnectedPreview() {
    SuacTheme {
        ChargerStatusCard(
            status = PreviewCharging.copy(state = ChargerState.CarConnected),
            chargedKwhText = "0.00 kWh",
            occupiedSinceText = null,
            isSignedIn = true,
            isRefreshing = false,
            isActionInProgress = false,
            isCurrentChangeInProgress = false,
            onRefresh = {},
            onStart = {},
            onStop = {},
            onSelectCurrent = {},
        )
    }
}

@Preview
@Composable
private fun ChargerStatusCardFreePreview() {
    SuacTheme(isDarkTheme = true) {
        ChargerStatusCard(
            status = PreviewCharging.copy(state = ChargerState.Free),
            chargedKwhText = "0.00 kWh",
            occupiedSinceText = null,
            isSignedIn = true,
            isRefreshing = false,
            isActionInProgress = false,
            isCurrentChangeInProgress = false,
            onRefresh = {},
            onStart = {},
            onStop = {},
            onSelectCurrent = {},
        )
    }
}

@Preview
@Composable
private fun ChargerStatusCardGuestPreview() {
    SuacTheme(isDarkTheme = true) {
        ChargerStatusCard(
            status = PreviewCharging.copy(state = ChargerState.Free),
            chargedKwhText = "0.00 kWh",
            occupiedSinceText = null,
            isSignedIn = false,
            isRefreshing = false,
            isActionInProgress = false,
            isCurrentChangeInProgress = false,
            onRefresh = {},
            onStart = {},
            onStop = {},
            onSelectCurrent = {},
        )
    }
}
