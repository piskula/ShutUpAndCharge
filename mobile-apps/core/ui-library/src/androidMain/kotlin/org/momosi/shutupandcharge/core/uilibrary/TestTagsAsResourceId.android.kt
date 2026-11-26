package org.momosi.shutupandcharge.core.uilibrary

import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId

actual fun Modifier.markTestTagsAsResourceId(): Modifier = semantics { testTagsAsResourceId = true }
