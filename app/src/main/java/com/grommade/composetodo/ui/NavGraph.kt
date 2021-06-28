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
import com.grommade.composetodo.MainRoute
import com.grommade.composetodo.SettingsRoute
import com.grommade.composetodo.SettingsSingleTaskRoute
import com.grommade.composetodo.TasksRoute
import com.grommade.composetodo.home.HomeScreen
import com.grommade.composetodo.home.HomeViewModel
import com.grommade.composetodo.settings.SettingsScreen
import com.grommade.composetodo.settings.single_task.SettingsSingleTaskScreen
import com.grommade.composetodo.settings.single_task.time_and_frequency.SettingsSingleTaskFrequencyScreen
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
        startDestination = MainRoute.HomeChildRoute.route
    ) {
        addRoutMainScreen(drawerGesturesEnabled, openDrawer)
        addRoutStatistics(navController, drawerGesturesEnabled)

        addRoutSettings(navController, drawerGesturesEnabled)
        addRoutSettingsSingleTask(navController)
        addRoutSettingsSingleTaskFrequency(navController)

        addRoutTaskList(navController, drawerGesturesEnabled)
        addRoutRegularTask(navController)
        addRoutSingleTask(navController)
    }
}

/** Main */

private fun NavGraphBuilder.addRoutMainScreen(
    drawerGesturesEnabled: (Boolean) -> Unit,
    openDrawer: () -> Unit
) = composable(
    route = MainRoute.HomeChildRoute.route
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
    route = MainRoute.TaskListChildRoute.route,
    arguments = MainRoute.TaskListChildRoute.addArguments()
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
    route = MainRoute.StatisticsChildRoute.route
) {
    drawerGesturesEnabled(false)
    StatisticsScreen(
        viewModel = hiltViewModel(),
        navController
    )
}

private fun NavGraphBuilder.addRoutSettings(
    navController: NavHostController,
    drawerGesturesEnabled: (Boolean) -> Unit
) = composable(
    route = MainRoute.SettingsChildRoute.route
) {
    drawerGesturesEnabled(false)
    SettingsScreen(
        viewModel = hiltViewModel(),
        navController
    )
}

/** Types Tasks */

private fun NavGraphBuilder.addRoutRegularTask(
    navController: NavHostController
) = composable(
    route = TasksRoute.RegularTaskChildRoute.route,
    arguments = TasksRoute.RegularTaskChildRoute.addArguments()
) {
    // TODO
}

@ExperimentalMaterialApi
@ExperimentalComposeUiApi
private fun NavGraphBuilder.addRoutSingleTask(
    navController: NavHostController
) = composable(
    route = TasksRoute.SingleTaskChildRoute.route,
    arguments = TasksRoute.SingleTaskChildRoute.addArguments()
) {
    SingleTaskScreen(
        viewModel = hiltViewModel(),
        navController,
    )
}

/** Types Settings */

@ExperimentalMaterialApi
private fun NavGraphBuilder.addRoutSettingsSingleTask(
    navController: NavHostController
) = composable(
    route = SettingsRoute.SettingsSingleTaskChildRoute.route
) {
    SettingsSingleTaskScreen(
        viewModel = hiltViewModel(),
        navController,
    )
}

/** Single Task Settings */

@ExperimentalMaterialApi
private fun NavGraphBuilder.addRoutSettingsSingleTaskFrequency(
    navController: NavHostController
) = composable(
    route = SettingsSingleTaskRoute.SettingsSingleTaskFrequencyChildRoute.route
) {
    SettingsSingleTaskFrequencyScreen(
        viewModel = hiltViewModel(),
        navController,
    )
}