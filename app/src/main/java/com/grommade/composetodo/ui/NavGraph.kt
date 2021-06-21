package com.grommade.composetodo.ui

import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navArgument
import androidx.navigation.compose.rememberNavController
import com.grommade.composetodo.enums.ModeTaskList
import com.grommade.composetodo.enums.TypeTask
import com.grommade.composetodo.ui.home.HomeScreen
import com.grommade.composetodo.ui.task_list.TaskListScreen
import com.grommade.composetodo.ui.task_list.TaskListViewModel
import com.grommade.composetodo.util.Keys
import com.grommade.composetodo.util.MainDestinations
import com.grommade.composetodo.util.addArgument
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
        startDestination = MainDestinations.HOME_SCREEN
    ) {
        composable(MainDestinations.HOME_SCREEN) {
            drawerGesturesEnabled(true)
            HomeScreen(
                openDrawer = openDrawer
            )
        }
        composable(
            route = MainDestinations.TASK_LIST.addArgument(Keys.TASK_TYPE_KEY),
            arguments = listOf(
                navArgument("type") {
                    type = NavType.StringType
                }
            )
        ) {
            drawerGesturesEnabled(false)
            TaskListScreen(
                onBack = navController::navigateUp,
                viewModel = hiltViewModel()
            )
        }
    }
}
