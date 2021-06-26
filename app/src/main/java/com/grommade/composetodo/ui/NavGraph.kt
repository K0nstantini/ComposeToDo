package com.grommade.composetodo.ui

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.grommade.composetodo.MainScreen
import com.grommade.composetodo.TasksScreen
import com.grommade.composetodo.home.HomeScreen
import com.grommade.composetodo.home.HomeViewModel
import com.grommade.composetodo.single_task.SingleTaskScreen
import com.grommade.composetodo.statistics.StatisticsScreen
import com.grommade.composetodo.task_list.TaskListScreen
import kotlinx.coroutines.launch

@ExperimentalMaterialApi
@ExperimentalComposeUiApi
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
        addRoutMainScreen(drawerGesturesEnabled, openDrawer)
        addRoutTaskList(navController, drawerGesturesEnabled)
        addRoutStatistics(navController, drawerGesturesEnabled)
        addRoutRegularTask(navController)
        addRoutSingleTask(navController)
    }
}

private fun NavGraphBuilder.addRoutMainScreen(
    drawerGesturesEnabled: (Boolean) -> Unit,
    openDrawer: () -> Unit
) = composable(
    route = MainScreen.Home.route
) {
    drawerGesturesEnabled(true)
    HomeScreen(
        viewModel = hiltViewModel<HomeViewModel>().also { it.refreshTasks() },
        openDrawer = openDrawer
    )
}

private fun NavGraphBuilder.addRoutTaskList(
    navController: NavHostController,
    drawerGesturesEnabled: (Boolean) -> Unit
) = composable(
    route = MainScreen.TaskList.route,
    arguments = MainScreen.TaskList.addArguments()
) {
    drawerGesturesEnabled(false)
    TaskListScreen(
        viewModel = hiltViewModel(),
        navController
    )
}

private fun NavGraphBuilder.addRoutStatistics(
    navController: NavHostController,
    drawerGesturesEnabled: (Boolean) -> Unit
) = composable(
    route = MainScreen.Statistics.route
) {
    drawerGesturesEnabled(false)
    StatisticsScreen(
        viewModel = hiltViewModel(),
        navController
    )
}

private fun NavGraphBuilder.addRoutRegularTask(
    navController: NavHostController
) = composable(
    route = TasksScreen.RegularTask.route,
    arguments = TasksScreen.RegularTask.addArguments()
) {
    // TODO
}

@ExperimentalMaterialApi
@ExperimentalComposeUiApi
private fun NavGraphBuilder.addRoutSingleTask(
    navController: NavHostController
) = composable(
    route = TasksScreen.SingleTask.route,
    arguments = TasksScreen.SingleTask.addArguments()
) {
    SingleTaskScreen(
        viewModel = hiltViewModel(),
        navController,
    )
}
