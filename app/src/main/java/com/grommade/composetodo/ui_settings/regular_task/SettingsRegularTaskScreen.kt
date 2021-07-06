package com.grommade.composetodo.ui_settings.regular_task

import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun SettingsRegularTaskScreen() {
    Text(
        text = "Coming soon...",
        modifier = Modifier.padding(8.dp),
        style = MaterialTheme.typography.caption.copy(fontStyle = FontStyle.Italic),
    )
}

@Preview
@Composable
fun SettingsRegularTaskScreenPreview() {
    SettingsRegularTaskScreen()
}