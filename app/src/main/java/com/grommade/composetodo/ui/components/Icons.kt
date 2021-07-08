package com.grommade.composetodo.ui.components

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable


@Composable
fun NavigationBackIcon(
    callback: () -> Unit
) {
    IconButton(onClick = callback) {
        Icon(Icons.Filled.ArrowBack, "")
    }
}

@Composable
fun NavigationCloseIcon(
    callback: () -> Unit
) {
    IconButton(onClick = callback) {
        Icon(Icons.Filled.Close, "")
    }
}

@Composable
fun SaveIcon(
    callback: () -> Unit
) {
    IconButton(onClick = callback) {
        Icon(Icons.Filled.Save, "")
    }
}

@Composable
fun DeleteIcon(
    callback: () -> Unit
) {
    IconButton(onClick = callback) {
        Icon(Icons.Filled.Delete, "")
    }
}

@Composable
fun DoneIcon(
    callback: () -> Unit
) {
    IconButton(onClick = callback) {
        Icon(Icons.Filled.Done, "")
    }
}

@Composable
fun MoreVertIcon(
    callback: () -> Unit
) {
    IconButton(onClick = callback) {
        Icon(Icons.Default.MoreVert, "")
    }
}