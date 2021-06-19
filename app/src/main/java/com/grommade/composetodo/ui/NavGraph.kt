package com.grommade.composetodo.ui

import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.grommade.composetodo.ui.home.HomeScreen
import com.grommade.composetodo.ui.task_list.TaskListScreen
import com.homemade.anothertodo.enums.TypeTask
import kotlinx.coroutines.launch

object MainDestinations {
    const val HOME_SCREEN = "mainScreen"
    const val REGULAR_TASKS = "regularTasks"
    const val SINGLE_TASKS = "singleTasks"
    const val TASK_LIST = "taskList"
}

@Composable
fun ToDoNavGraph(
    navController: NavHostController = rememberNavController(),
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    drawerGesturesEnabled: MutableState<Boolean>,
) {

    val scope = rememberCoroutineScope()
    val openDrawer: () -> Unit = { scope.launch { scaffoldState.drawerState.open() } }

    NavHost(
        navController = navController,
        startDestination = MainDestinations.HOME_SCREEN
    ) {
        composable(MainDestinations.HOME_SCREEN) {
            HomeScreen(
                openDrawer = openDrawer,
                drawerGesturesEnabled = drawerGesturesEnabled
            )
        }
        composable(MainDestinations.REGULAR_TASKS) {
            TaskListScreen(
                typeTask = TypeTask.REGULAR_TASK,
                onBack = navController::navigateUp,
                drawerGesturesEnabled = drawerGesturesEnabled
            )
        }
        composable(MainDestinations.SINGLE_TASKS) {
            TaskListScreen(
                typeTask = TypeTask.SINGLE_TASK,
                onBack = navController::navigateUp,
                drawerGesturesEnabled = drawerGesturesEnabled
            )
        }
    }
}
