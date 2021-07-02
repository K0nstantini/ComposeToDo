package com.grommade.composetodo.ui.components

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable

// FIXME: DEl
@Composable
fun NavigationBackIcon(
    callback: () -> Unit
) {
    IconButton(onClick = callback) {
        Icon(Icons.Filled.ArrowBack, "")
    }
}