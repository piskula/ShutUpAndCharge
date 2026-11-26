package org.momosi.shutupandcharge.feature.profile.impl

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.momosi.shutupandcharge.core.common.language.AppLanguage
import org.momosi.shutupandcharge.core.uilibrary.components.Button
import org.momosi.shutupandcharge.core.uilibrary.components.ButtonRole
import org.momosi.shutupandcharge.core.uilibrary.components.ButtonStyle
import org.momosi.shutupandcharge.core.uilibrary.components.Chip
import org.momosi.shutupandcharge.core.uilibrary.components.ChipVariant
import org.momosi.shutupandcharge.core.uilibrary.components.DashboardTopBar
import org.momosi.shutupandcharge.core.uilibrary.components.Icon
import org.momosi.shutupandcharge.core.uilibrary.components.InfoDialog
import org.momosi.shutupandcharge.core.uilibrary.components.Text
import org.momosi.shutupandcharge.core.uilibrary.icons.SuacIcons
import org.momosi.shutupandcharge.core.uilibrary.modifier.card
import org.momosi.shutupandcharge.core.uilibrary.theme.AppTheme
import org.momosi.shutupandcharge.core.uilibrary.theme.SuacTheme
import org.momosi.shutupandcharge.feature.profile.impl.resources.Res
import org.momosi.shutupandcharge.feature.profile.impl.resources.profile_language_english
import org.momosi.shutupandcharge.feature.profile.impl.resources.profile_language_restart_message
import org.momosi.shutupandcharge.feature.profile.impl.resources.profile_language_restart_title
import org.momosi.shutupandcharge.feature.profile.impl.resources.profile_language_slovak
import org.momosi.shutupandcharge.feature.profile.impl.resources.profile_language_title
import org.momosi.shutupandcharge.feature.profile.impl.resources.profile_no_charging_role
import org.momosi.shutupandcharge.feature.profile.impl.resources.profile_role_admin
import org.momosi.shutupandcharge.feature.profile.impl.resources.profile_role_user
import org.momosi.shutupandcharge.feature.profile.impl.resources.profile_sign_out
import org.momosi.shutupandcharge.feature.profile.impl.resources.profile_subtitle
import org.momosi.shutupandcharge.feature.profile.impl.resources.profile_title
import org.momosi.shutupandcharge.feature.profile.impl.resources.profile_username
import org.momosi.shutupandcharge.feature.profile.impl.resources.profile_version_app_label
import org.momosi.shutupandcharge.feature.profile.impl.resources.profile_version_dialog_title
import org.momosi.shutupandcharge.feature.profile.impl.resources.profile_version_dismiss
import org.momosi.shutupandcharge.feature.profile.impl.resources.profile_version_loading
import org.momosi.shutupandcharge.feature.profile.impl.resources.profile_version_server_label
import org.momosi.shutupandcharge.feature.profile.impl.resources.profile_version_unavailable
import org.momosi.shutupandcharge.user.model.User
import org.momosi.shutupandcharge.user.model.UserRole

@Composable
internal fun ProfileScreen(viewModel: ProfileViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ProfileContent(state = state, onAction = viewModel::onAction)
}

@Composable
private fun ProfileContent(
    state: ProfileState,
    onAction: (ProfileAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize()) {
        DashboardTopBar(title = stringResource(Res.string.profile_title), subtitle = stringResource(Res.string.profile_subtitle))
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            state.user?.let { UserCard(it) }
            Column(modifier = Modifier.fillMaxWidth().card()) {
                SettingRow(
                    icon = SuacIcons.Language,
                    title = stringResource(Res.string.profile_language_title),
                    value = state.language.label(),
                    tag = "profile_language_row",
                    onClick = { onAction(ProfileAction.ShowLanguageSelector) },
                )
                SettingRow(
                    icon = SuacIcons.Info,
                    title = stringResource(Res.string.profile_version_dialog_title),
                    value = state.appVersion,
                    tag = "profile_version_row",
                    onClick = { onAction(ProfileAction.ShowVersionInfo) },
                )
            }
            Button(
                text = stringResource(Res.string.profile_sign_out),
                onClick = { onAction(ProfileAction.SignOut) },
                loading = state.isSigningOut,
                icon = SuacIcons.Logout,
                role = ButtonRole.Warning,
                style = ButtonStyle.Tonal,
                modifier = Modifier.fillMaxWidth().testTag("profile_sign_out_button"),
            )
        }
    }

    if (state.isLanguageDialogVisible) LanguageDialog(selected = state.language, onAction = onAction)
    if (state.isVersionDialogVisible) VersionDialog(state = state, onAction = onAction)
    if (state.isRestartNoticeVisible) {
        InfoDialog(
            title = stringResource(Res.string.profile_language_restart_title),
            dismissText = stringResource(Res.string.profile_version_dismiss),
            onDismiss = { onAction(ProfileAction.DismissRestartNotice) },
        ) {
            Text(
                text = stringResource(Res.string.profile_language_restart_message),
                style = AppTheme.typography.bodyMedium,
                color = AppTheme.colors.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun UserCard(user: User) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxWidth().card().padding(16.dp).testTag("profile_user_card"),
    ) {
        Icon(imageVector = SuacIcons.AccountCircle, tint = AppTheme.colors.primary, modifier = Modifier.size(56.dp))
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(text = user.name, style = AppTheme.typography.titleLarge, color = AppTheme.colors.onSurface)
            if (user.email.isNotBlank()) {
                Text(text = user.email, style = AppTheme.typography.bodyMedium, color = AppTheme.colors.onSurfaceVariant)
            }
            if (user.username.isNotBlank()) {
                Text(
                    text = stringResource(Res.string.profile_username, user.username),
                    style = AppTheme.typography.bodySmall,
                    color = AppTheme.colors.onSurfaceVariant,
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (UserRole.User in user.roles) Chip(label = stringResource(Res.string.profile_role_user), variant = ChipVariant.Tonal)
                if (UserRole.Admin in user.roles) Chip(label = stringResource(Res.string.profile_role_admin), variant = ChipVariant.Warning)
            }
            if (UserRole.User !in user.roles) {
                Text(
                    text = stringResource(Res.string.profile_no_charging_role),
                    style = AppTheme.typography.bodySmall,
                    color = AppTheme.colors.error,
                )
            }
        }
    }
}

@Composable
private fun SettingRow(
    icon: ImageVector,
    title: String,
    value: String,
    tag: String,
    onClick: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp)
            .testTag(tag),
    ) {
        Icon(imageVector = icon, tint = AppTheme.colors.onSurfaceVariant)
        Text(text = title, style = AppTheme.typography.bodyLarge, color = AppTheme.colors.onSurface, modifier = Modifier.weight(1f))
        Text(text = value, style = AppTheme.typography.bodyMedium, color = AppTheme.colors.onSurfaceVariant)
    }
}

@Composable
private fun LanguageDialog(selected: AppLanguage, onAction: (ProfileAction) -> Unit) {
    InfoDialog(
        title = stringResource(Res.string.profile_language_title),
        dismissText = stringResource(Res.string.profile_version_dismiss),
        onDismiss = { onAction(ProfileAction.DismissLanguageSelector) },
    ) {
        AppLanguage.entries.forEach { language ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onAction(ProfileAction.SelectLanguage(language)) }
                    .padding(vertical = 12.dp)
                    .testTag("profile_language_${language.languageTag}"),
            ) {
                Icon(
                    imageVector = if (language == selected) SuacIcons.CheckCircle else SuacIcons.Language,
                    tint = if (language == selected) AppTheme.colors.primary else AppTheme.colors.onSurfaceVariant,
                )
                Text(text = language.label(), style = AppTheme.typography.bodyLarge, color = AppTheme.colors.onSurface)
            }
        }
    }
}

@Composable
private fun VersionDialog(state: ProfileState, onAction: (ProfileAction) -> Unit) {
    InfoDialog(
        title = stringResource(Res.string.profile_version_dialog_title),
        dismissText = stringResource(Res.string.profile_version_dismiss),
        onDismiss = { onAction(ProfileAction.DismissVersionInfo) },
    ) {
        VersionLine(label = stringResource(Res.string.profile_version_app_label), value = state.appVersion)
        VersionLine(
            label = stringResource(Res.string.profile_version_server_label),
            value = when (val server = state.serverVersion) {
                ServerVersionContent.Loading -> stringResource(Res.string.profile_version_loading)
                is ServerVersionContent.Loaded -> server.text
                ServerVersionContent.Error -> stringResource(Res.string.profile_version_unavailable)
            },
        )
    }
}

@Composable
private fun VersionLine(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(text = label, style = AppTheme.typography.labelMedium, color = AppTheme.colors.onSurfaceVariant)
        Text(text = value, style = AppTheme.typography.bodyLarge, color = AppTheme.colors.onSurface)
    }
}

@Composable
private fun AppLanguage.label(): String = when (this) {
    AppLanguage.English -> stringResource(Res.string.profile_language_english)
    AppLanguage.Slovak -> stringResource(Res.string.profile_language_slovak)
}

@Preview
@Composable
private fun ProfileContentPreview() {
    SuacTheme {
        ProfileContent(
            state = ProfileState(
                user = User(
                    id = "1",
                    name = "Martin Styk",
                    email = "martin@example.com",
                    username = "martin",
                    roles = setOf(UserRole.User, UserRole.Admin),
                ),
                appVersion = "1.0.0 (10000)",
            ),
            onAction = {},
        )
    }
}

@Preview
@Composable
private fun ProfileContentNoRolePreview() {
    SuacTheme(isDarkTheme = true) {
        ProfileContent(
            state = ProfileState(
                user = User(id = "1", name = "Guest User", email = "", username = "guest", roles = emptySet()),
                appVersion = "dev (1)",
            ),
            onAction = {},
        )
    }
}
