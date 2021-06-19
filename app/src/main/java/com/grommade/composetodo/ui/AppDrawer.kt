package com.grommade.composetodo.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.grommade.composetodo.R
import com.grommade.composetodo.ui.theme.ComposeToDoTheme

@Composable
fun AppDrawer(
    navigateToRegularTasks: () -> Unit,
    navigateToSingleTasks: () -> Unit,
    closeDrawer: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        DrawerButton(
            icon = Icons.Filled.List,
            label = stringResource(id = R.string.nav_regular_task_list),
            action = {
                navigateToRegularTasks()
                closeDrawer()
            }
        )
        DrawerButton(
            icon = Icons.Filled.List,
            label = stringResource(id = R.string.nav_single_task_list),
            action = {
                navigateToSingleTasks()
                closeDrawer()
            }
        )
    }
}

@Composable
private fun DrawerButton(
    icon: ImageVector,
    label: String,
    action: () -> Unit,
    modifier: Modifier = Modifier
) {
    val surfaceModifier = modifier
        .padding(start = 8.dp, top = 8.dp, end = 8.dp)
        .fillMaxWidth()
    Surface(
        modifier = surfaceModifier,
    ) {
        TextButton(
            onClick = action,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    imageVector = icon,
                    contentDescription = null, // decorative
                )
                Spacer(Modifier.width(16.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.body2,
                    color = MaterialTheme.colors.onSurface
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewAppDrawer() {
    ComposeToDoTheme {
        Surface {
            AppDrawer({}, {}, {})
        }
    }
}