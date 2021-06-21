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
) {

    Scaffold(
        topBar = {
            TopBar(openDrawer)
        }
    ) {
        Text(text = "Home screen")
    }
}

@Composable
private fun TopBar(openDrawer: () -> Unit) {
    TopAppBar(
        title = { Text(stringResource(R.string.app_name)) },
        navigationIcon = {
            IconButton(onClick = openDrawer) {
                Icon(Icons.Filled.Menu, contentDescription = null)
            }
        }
    )
}


@Preview
@Composable
fun HomeScreenPreview() {
    HomeScreen(openDrawer = {})
}