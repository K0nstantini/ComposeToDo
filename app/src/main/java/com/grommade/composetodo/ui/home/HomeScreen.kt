package com.grommade.composetodo.ui.home

import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.grommade.composetodo.R

@Composable
fun HomeScreen(
    openDrawer: () -> Unit,
    drawerGesturesEnabled: MutableState<Boolean> = remember { mutableStateOf(true) },
) {
    drawerGesturesEnabled.value = true

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                navigationIcon = {
                    IconButton(onClick = openDrawer) {
                        Icon(Icons.Filled.Menu, contentDescription = null)
                    }
                }
            )
        }
    ) {
        Text(text = "Home screen")
    }
}


@Preview
@Composable
fun DefaultPreview() {
    HomeScreen(openDrawer = {})
}