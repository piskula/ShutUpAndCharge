package org.momosi.shutupandcharge.core.uilibrary

import androidx.compose.runtime.Composable

@Composable
expect fun BackHandler(enabled: Boolean, onBack: () -> Unit)
