package com.grommade.composetodo.ui

import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navArgument
import androidx.navigation.compose.rememberNavController
import com.grommade.composetodo.MainScreen
import com.grommade.composetodo.TasksScreen
import com.grommade.composetodo.single_task.SingleTaskScreen
import com.grommade.composetodo.ui.home.HomeScreen
import com.grommade.composetodo.ui.task_list.TaskListScreen
import com.grommade.composetodo.util.Keys
import kotlinx.coroutines.launch

@Composable
fun ToDoNavGraph(
    navController: NavHostController = rememberNavController(),
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    drawerGesturesEnabled: (Boolean) -> Unit,
) {

    val scope = rememberCoroutineScope()
    val openDrawer: () -> Unit = {
        scope.launch { scaffoldState.drawerState.open() }
    }

    NavHost(
        navController = navController,
        startDestination = MainScreen.Home.route
    ) {
        composable(
            route = MainScreen.Home.route
        ) {
            drawerGesturesEnabled(true)
            HomeScreen(
                openDrawer = openDrawer
            )
        }
        composable(
            route = MainScreen.TaskList.route,
            arguments = listOf(navArgument(Keys.TASK_TYPE_KEY) {
                type = NavType.StringType
            })
        ) {
            drawerGesturesEnabled(false)
            TaskListScreen(
                viewModel = hiltViewModel(),
                navController,
                onBack = navController::navigateUp, // FIXME: Del?
            )
        }
        composable(
            route = TasksScreen.RegularTask.route,
            arguments = listOf(navArgument(Keys.TASK_ID) {
                defaultValue = -1
            })
        ) {
//            // TODO: RegularTaskScreen()
        }
        composable(
            route = TasksScreen.SingleTask.route,
            arguments = listOf(navArgument(Keys.TASK_ID) {
                defaultValue = -1L
            })
        ) {
            SingleTaskScreen(
                viewModel = hiltViewModel(),
                navController,
            )
        }
    }
}
