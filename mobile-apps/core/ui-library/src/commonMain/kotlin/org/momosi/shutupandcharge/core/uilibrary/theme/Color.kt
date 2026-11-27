package org.momosi.shutupandcharge.core.uilibrary.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class SuacColorPalette(
    val surface: Color,
    val surfaceBright: Color,
    val surfaceDim: Color,
    val surfaceContainerLowest: Color,
    val surfaceContainerLow: Color,
    val surfaceContainer: Color,
    val surfaceContainerHigh: Color,
    val surfaceContainerHighest: Color,
    val surfaceVariant: Color,
    val inverseSurface: Color,
    val inverseOnSurface: Color,
    val outline: Color,
    val outlineVariant: Color,

    val onSurface: Color,
    val onSurfaceVariant: Color,

    val primary: Color,
    val onPrimary: Color,
    val primaryContainer: Color,
    val onPrimaryContainer: Color,
    val onPrimaryFixedVariant: Color,

    val secondary: Color,
    val onSecondary: Color,
    val secondaryContainer: Color,
    val onSecondaryContainer: Color,

    val open: Color,
    val onOpen: Color,
    val openContainer: Color,
    val onOpenContainer: Color,

    val positive: Color,
    val onPositive: Color,
    val positiveContainer: Color,
    val onPositiveContainer: Color,
    val error: Color,
    val onError: Color,
    val errorContainer: Color,
    val onErrorContainer: Color,
    val warning: Color,
    val onWarning: Color,
    val warningContainer: Color,
    val onWarningContainer: Color,

    val shadow: Color,

    val chartSeries: ImmutableList<Color>,
    val chartOther: Color,
)

private val LightPrimary = Color(0xFF005CBB)
private val LightOnPrimary = Color(0xFFFFFFFF)
private val LightPrimaryContainer = Color(0xFFD7E3FF)
private val LightOnPrimaryContainer = Color(0xFF001B3F)
private val OnPrimaryFixedVariant = Color(0xFF00458F)

private val DarkPrimary = Color(0xFFABC7FF)
private val DarkOnPrimary = Color(0xFF002F65)
private val DarkPrimaryContainer = Color(0xFF00458F)
private val DarkOnPrimaryContainer = Color(0xFFD7E3FF)

internal val LightSuacColors = SuacColorPalette(
    surface = Color(0xFFFAF9FD),
    surfaceBright = Color(0xFFFAF9FD),
    surfaceDim = Color(0xFFDBD9DD),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFF4F3F6),
    surfaceContainer = Color(0xFFEFEDF0),
    surfaceContainerHigh = Color(0xFFE9E7EB),
    surfaceContainerHighest = Color(0xFFE3E2E6),
    surfaceVariant = Color(0xFFE0E2EC),
    inverseSurface = Color(0xFF2F3033),
    inverseOnSurface = Color(0xFFF1F0F4),
    outline = Color(0xFF74777F),
    outlineVariant = Color(0xFFC4C6D0),
    onSurface = Color(0xFF1A1B1F),
    onSurfaceVariant = Color(0xFF44474E),
    primary = LightPrimary,
    onPrimary = LightOnPrimary,
    primaryContainer = LightPrimaryContainer,
    onPrimaryContainer = LightOnPrimaryContainer,
    onPrimaryFixedVariant = OnPrimaryFixedVariant,
    secondary = Color(0xFF535F70),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFD7E3F7),
    onSecondaryContainer = Color(0xFF101C2B),
    open = LightPrimary,
    onOpen = LightOnPrimary,
    openContainer = LightPrimaryContainer,
    onOpenContainer = LightOnPrimaryContainer,
    positive = Color(0xFF006D3C),
    onPositive = Color(0xFFFFFFFF),
    positiveContainer = Color(0xFFBEEFBB),
    onPositiveContainer = Color(0xFF00522C),
    error = Color(0xFFBA1A1A),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFF9DEDC),
    onErrorContainer = Color(0xFF410E0B),
    warning = Color(0xFFA64F00),
    onWarning = Color(0xFFFFFFFF),
    warningContainer = Color(0xFFFFDCC3),
    onWarningContainer = Color(0xFF753403),
    shadow = Color(0x40000000),
    chartSeries = persistentListOf(
        Color(0xFF005CBB),
        Color(0xFFE8710A),
        Color(0xFF1E8E3E),
        Color(0xFFD93025),
        Color(0xFF9334E6),
    ),
    chartOther = Color(0xFF9E9E9E),
)

internal val DarkSuacColors = SuacColorPalette(
    surface = Color(0xFF121316),
    surfaceBright = Color(0xFF38393C),
    surfaceDim = Color(0xFF121316),
    surfaceContainerLowest = Color(0xFF0D0E11),
    surfaceContainerLow = Color(0xFF1A1B1F),
    surfaceContainer = Color(0xFF1F2022),
    surfaceContainerHigh = Color(0xFF292A2C),
    surfaceContainerHighest = Color(0xFF343537),
    surfaceVariant = Color(0xFF44474E),
    inverseSurface = Color(0xFFE3E2E6),
    inverseOnSurface = Color(0xFF2F3033),
    outline = Color(0xFF8E9099),
    outlineVariant = Color(0xFF44474E),
    onSurface = Color(0xFFE3E2E6),
    onSurfaceVariant = Color(0xFFC4C6D0),
    primary = DarkPrimary,
    onPrimary = DarkOnPrimary,
    primaryContainer = DarkPrimaryContainer,
    onPrimaryContainer = DarkOnPrimaryContainer,
    onPrimaryFixedVariant = OnPrimaryFixedVariant,
    secondary = Color(0xFFBBC7DB),
    onSecondary = Color(0xFF253140),
    secondaryContainer = Color(0xFF3B4858),
    onSecondaryContainer = Color(0xFFD7E3F7),
    open = DarkPrimary,
    onOpen = DarkOnPrimary,
    openContainer = DarkPrimaryContainer,
    onOpenContainer = DarkOnPrimaryContainer,
    positive = Color(0xFF73DD8C),
    onPositive = Color(0xFF003920),
    positiveContainer = Color(0xFF80DA88),
    onPositiveContainer = Color(0xFF00522C),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    warning = Color(0xFFFFBE8A),
    onWarning = Color(0xFF5A2500),
    warningContainer = Color(0xFFFFB683),
    onWarningContainer = Color(0xFF5A2500),
    shadow = Color(0x00000000),
    chartSeries = persistentListOf(
        Color(0xFFAAC7FF),
        Color(0xFFF4B77A),
        Color(0xFF7DD99A),
        Color(0xFFFFB4AB),
        Color(0xFFD0BCFF),
    ),
    chartOther = Color(0xFF8F9BAD),
)

internal fun lightColorScheme(colors: SuacColorPalette) = lightColorScheme(
    primary = colors.primary,
    onPrimary = colors.onPrimary,
    primaryContainer = colors.primaryContainer,
    onPrimaryContainer = colors.onPrimaryContainer,
    secondary = colors.secondary,
    onSecondary = colors.onSecondary,
    secondaryContainer = colors.secondaryContainer,
    onSecondaryContainer = colors.onSecondaryContainer,
    background = colors.surfaceContainer,
    surface = colors.surface,
    onSurface = colors.onSurface,
    surfaceVariant = colors.surfaceVariant,
    onSurfaceVariant = colors.onSurfaceVariant,
    surfaceContainerLowest = colors.surfaceContainerLowest,
    surfaceContainerLow = colors.surfaceContainerLow,
    surfaceContainer = colors.surfaceContainer,
    surfaceContainerHigh = colors.surfaceContainerHigh,
    surfaceContainerHighest = colors.surfaceContainerHighest,
    inverseSurface = colors.inverseSurface,
    inverseOnSurface = colors.inverseOnSurface,
    error = colors.error,
    onError = colors.onError,
    errorContainer = colors.errorContainer,
    onErrorContainer = colors.onErrorContainer,
    outline = colors.outline,
    outlineVariant = colors.outlineVariant,
)

internal fun darkColorScheme(colors: SuacColorPalette) = darkColorScheme(
    primary = colors.primary,
    onPrimary = colors.onPrimary,
    primaryContainer = colors.primaryContainer,
    onPrimaryContainer = colors.onPrimaryContainer,
    secondary = colors.secondary,
    onSecondary = colors.onSecondary,
    secondaryContainer = colors.secondaryContainer,
    onSecondaryContainer = colors.onSecondaryContainer,
    background = colors.surfaceContainer,
    surface = colors.surface,
    onSurface = colors.onSurface,
    surfaceVariant = colors.surfaceVariant,
    onSurfaceVariant = colors.onSurfaceVariant,
    surfaceContainerLowest = colors.surfaceContainerLowest,
    surfaceContainerLow = colors.surfaceContainerLow,
    surfaceContainer = colors.surfaceContainer,
    surfaceContainerHigh = colors.surfaceContainerHigh,
    surfaceContainerHighest = colors.surfaceContainerHighest,
    inverseSurface = colors.inverseSurface,
    inverseOnSurface = colors.inverseOnSurface,
    error = colors.error,
    onError = colors.onError,
    errorContainer = colors.errorContainer,
    onErrorContainer = colors.onErrorContainer,
    outline = colors.outline,
    outlineVariant = colors.outlineVariant,
)

internal val LocalSuacColors = staticCompositionLocalOf { LightSuacColors }
