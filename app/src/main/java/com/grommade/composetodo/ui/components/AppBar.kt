package com.grommade.composetodo.ui.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextOverflow

@Composable
fun TopBarActionMode(
    title: String,
    actions: @Composable RowScope.() -> Unit,
    closeActionMode: () -> Unit,
) {
    TopAppBar(
        title = { Text(title, maxLines = 1, overflow = TextOverflow.Ellipsis) },
        navigationIcon = {
            IconButton(onClick = closeActionMode) {
                Icon(Icons.Filled.Close, "")
            }
        },
        backgroundColor = MaterialTheme.colors.onSecondary,
        contentColor = MaterialTheme.colors.onPrimary,
        actions = actions
    )
}