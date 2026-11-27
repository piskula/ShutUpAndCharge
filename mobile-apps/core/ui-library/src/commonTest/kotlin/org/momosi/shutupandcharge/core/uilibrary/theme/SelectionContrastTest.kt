package org.momosi.shutupandcharge.core.uilibrary.theme

import androidx.compose.ui.graphics.Color
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.test.Test
import kotlin.test.assertTrue

class SelectionContrastTest {

    @Test
    fun lightSelectionLabelsMeetNormalTextContrast() {
        assertSelectionContrast(LightSuacColors)
    }

    @Test
    fun darkSelectionLabelsMeetNormalTextContrast() {
        assertSelectionContrast(DarkSuacColors)
    }

    private fun assertSelectionContrast(colors: SuacColorPalette) {
        val background = luminance(colors.secondaryContainer)
        val foreground = luminance(colors.onSecondaryContainer)
        val contrast = (max(background, foreground) + 0.05) / (min(background, foreground) + 0.05)
        assertTrue(contrast >= 4.5, "Selected labels must meet 4.5:1 contrast, got $contrast")
    }

    private fun luminance(color: Color): Double = 0.2126 * linearize(color.red) + 0.7152 * linearize(color.green) + 0.0722 * linearize(color.blue)

    private fun linearize(channel: Float): Double {
        val value = channel.toDouble()
        return if (value <= 0.04045) value / 12.92 else ((value + 0.055) / 1.055).pow(2.4)
    }
}
