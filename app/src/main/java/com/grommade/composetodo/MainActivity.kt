package com.grommade.composetodo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.grommade.composetodo.ui.AppDrawer
import com.grommade.composetodo.ui.MainDestinations
import com.grommade.composetodo.ui.ToDoNavGraph
import com.grommade.composetodo.ui.theme.ComposeToDoTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CompositionLocalProvider(
                LocalBackPressedDispatcher provides this.onBackPressedDispatcher
            ) {
                ToDoApp()
            }
        }
    }
}

@Composable
private fun ToDoApp() {
    ComposeToDoTheme {
        val navController = rememberNavController()
        val scope = rememberCoroutineScope()
        val scaffoldState = rememberScaffoldState()
        val drawerGesturesEnabled = remember { mutableStateOf(true) }
        val closeDrawer: () -> Unit = { scope.launch { scaffoldState.drawerState.close() } }

        if (scaffoldState.drawerState.isOpen) {
            BackPressHandler { closeDrawer() }
        }


        Scaffold(
            scaffoldState = scaffoldState,
            drawerContent = {
                AppDrawer(
                    navigateToRegularTasks = { navController.navigate(MainDestinations.REGULAR_TASKS) },
                    navigateToSingleTasks = { navController.navigate(MainDestinations.SINGLE_TASKS) },
                    closeDrawer = closeDrawer
                )
            },
            drawerGesturesEnabled = drawerGesturesEnabled.value
        ) {
            ToDoNavGraph(
                navController = navController,
                scaffoldState = scaffoldState,
                drawerGesturesEnabled = drawerGesturesEnabled
            )
        }
    }
}



@Preview
@Composable
fun DefaultPreview() {
    ToDoApp()
}
