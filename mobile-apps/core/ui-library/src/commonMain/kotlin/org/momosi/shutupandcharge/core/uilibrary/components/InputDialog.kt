package org.momosi.shutupandcharge.core.uilibrary.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.momosi.shutupandcharge.core.uilibrary.theme.AppTheme
import org.momosi.shutupandcharge.core.uilibrary.theme.SuacTheme

enum class InputKind { Text, Decimal }

@Composable
fun InputDialog(
    title: String,
    value: String,
    label: String,
    confirmText: String,
    dismissText: String,
    onValueChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    message: String? = null,
    inputKind: InputKind = InputKind.Text,
    confirmEnabled: Boolean = true,
    isInProgress: Boolean = false,
    extraContent: @Composable ColumnScope.() -> Unit = {},
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = title, style = AppTheme.typography.titleMedium, color = AppTheme.colors.onSurface) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                if (message != null) {
                    Text(text = message, style = AppTheme.typography.bodyMedium, color = AppTheme.colors.onSurfaceVariant)
                }
                OutlinedTextField(
                    value = value,
                    onValueChange = onValueChange,
                    label = { Text(text = label) },
                    singleLine = true,
                    enabled = !isInProgress,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = when (inputKind) {
                            InputKind.Text -> KeyboardType.Text
                            InputKind.Decimal -> KeyboardType.Decimal
                        },
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AppTheme.colors.primary,
                        focusedLabelColor = AppTheme.colors.primary,
                        cursorColor = AppTheme.colors.onSurface,
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("input_dialog_field"),
                )
                extraContent()
            }
        },
        confirmButton = {
            Button(
                text = confirmText,
                onClick = onConfirm,
                enabled = confirmEnabled,
                loading = isInProgress,
                modifier = Modifier.testTag("input_dialog_confirm_button"),
            )
        },
        dismissButton = {
            TextButton(text = dismissText, onClick = onDismiss, modifier = Modifier.testTag("input_dialog_dismiss_button"))
        },
        containerColor = AppTheme.colors.surfaceContainerLowest,
        modifier = modifier,
    )
}

@Preview
@Composable
private fun InputDialogPreview() {
    SuacTheme {
        InputDialog(
            title = "Top up credit",
            message = "Martin Styk",
            value = "20",
            label = "Amount (€)",
            confirmText = "Top up",
            dismissText = "Cancel",
            inputKind = InputKind.Decimal,
            onValueChange = {},
            onConfirm = {},
            onDismiss = {},
        )
    }
}
