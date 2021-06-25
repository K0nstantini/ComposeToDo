package com.grommade.composetodo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.grommade.composetodo.enums.ModeTaskList
import com.grommade.composetodo.enums.TypeTask
import com.grommade.composetodo.ui.AppDrawer
import com.grommade.composetodo.ui.ToDoNavGraph
import com.grommade.composetodo.ui.theme.ComposeToDoTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @ExperimentalMaterialApi
    @ExperimentalComposeUiApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CompositionLocalProvider(LocalBackPressedDispatcher provides this.onBackPressedDispatcher) {
                ToDoApp()
            }
        }
    }
}

@ExperimentalMaterialApi
@ExperimentalComposeUiApi
@Composable
private fun ToDoApp() {
    ComposeToDoTheme {
        val navController = rememberNavController()
        val scope = rememberCoroutineScope()
        val scaffoldState = rememberScaffoldState()
        val (drawerGesturesEnabled, setDrawerGesturesEnabled) = remember { mutableStateOf(true) }
        val closeDrawer: () -> Unit = {
            scope.launch { scaffoldState.drawerState.close() }
        }

        if (scaffoldState.drawerState.isOpen) {
            BackPressHandler { closeDrawer() }
        }


        Scaffold(
            scaffoldState = scaffoldState,
            drawerContent = {
                AppDrawer(
                    navigateToRegularTasks = {
                        navController.navigate(
                            MainScreen.TaskList.createRoute(
                                ModeTaskList.DEFAULT,
                                TypeTask.REGULAR_TASK
                            )
                        )
                    },
                    navigateToSingleTasks = {
                        navController.navigate(
                            MainScreen.TaskList.createRoute(
                                ModeTaskList.DEFAULT,
                                TypeTask.SINGLE_TASK
                            )
                        )
                    },
                    closeDrawer = closeDrawer
                )
            },
            drawerGesturesEnabled = drawerGesturesEnabled
        ) {
            ToDoNavGraph(
                navController = navController,
                scaffoldState = scaffoldState,
                drawerGesturesEnabled = setDrawerGesturesEnabled
            )
        }
    }
}

@ExperimentalMaterialApi
@ExperimentalComposeUiApi
@Preview
@Composable
fun DefaultPreview() {
    ToDoApp()
}
