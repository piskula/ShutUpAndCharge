package org.momosi.shutupandcharge.core.uilibrary.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import org.momosi.shutupandcharge.core.uilibrary.rememberSystemFontScale

@Composable
fun SuacTheme(isDarkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val suacColors = if (isDarkTheme) DarkSuacColors else LightSuacColors
    val materialColorScheme = remember(isDarkTheme) {
        if (isDarkTheme) darkColorScheme(suacColors) else lightColorScheme(suacColors)
    }
    val ambientDensity = LocalDensity.current
    val systemFontScale = rememberSystemFontScale()

    CompositionLocalProvider(
        LocalDensity provides Density(ambientDensity.density, ambientDensity.fontScale * systemFontScale),
        LocalSuacColors provides suacColors,
        LocalSuacTypography provides SuacTypography.toSuacTypographyStyles(),
    ) {
        MaterialTheme(
            colorScheme = materialColorScheme,
            typography = SuacTypography,
            content = content,
        )
    }
}

object AppTheme {
    val colors: SuacColorPalette
        @Composable
        @ReadOnlyComposable
        get() = LocalSuacColors.current

    val typography: SuacTypographyStyles
        @Composable
        @ReadOnlyComposable
        get() = LocalSuacTypography.current
}
