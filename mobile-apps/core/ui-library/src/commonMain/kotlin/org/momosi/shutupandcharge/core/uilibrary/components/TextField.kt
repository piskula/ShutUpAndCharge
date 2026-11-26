package org.momosi.shutupandcharge.core.uilibrary.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.momosi.shutupandcharge.core.uilibrary.theme.AppTheme
import org.momosi.shutupandcharge.core.uilibrary.theme.SuacTheme

@Composable
fun TextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = AppTheme.typography.bodyLarge.copy(color = AppTheme.colors.onSurface),
    enabled: Boolean = true,
    singleLine: Boolean = false,
    minLines: Int = 1,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    decorationBox: @Composable (innerTextField: @Composable () -> Unit) -> Unit = { innerTextField -> innerTextField() },
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        textStyle = textStyle,
        cursorBrush = SolidColor(AppTheme.colors.onSurface),
        enabled = enabled,
        singleLine = singleLine,
        minLines = minLines,
        keyboardOptions = keyboardOptions,
        decorationBox = decorationBox,
    )
}

@Preview
@Composable
private fun TextFieldPreview() {
    SuacTheme {
        TextField(value = "04A1B2C3", onValueChange = {}, modifier = Modifier.padding(16.dp))
    }
}

@Preview
@Composable
private fun TextFieldEmptyPreview() {
    SuacTheme {
        TextField(value = "", onValueChange = {}, modifier = Modifier.padding(16.dp))
    }
}
