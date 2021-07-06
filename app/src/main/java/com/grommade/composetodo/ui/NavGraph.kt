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
import com.grommade.composetodo.ui_history.HistoryScreen
import com.grommade.composetodo.ui_home.HomeUi
import com.grommade.composetodo.ui_settings.SettingsScreen
import com.grommade.composetodo.ui_settings.general.SettingsGeneralTaskScreen
import com.grommade.composetodo.ui_settings.regular_task.SettingsRegularTaskScreen
import com.grommade.composetodo.ui_settings.single_task.SettingsSingleTaskScreen
import com.grommade.composetodo.ui_settings.single_task.time_and_frequency.SettingsSingleTaskFrequencyScreen
import com.grommade.composetodo.ui_single_task.SingleTaskScreen
import com.grommade.composetodo.ui_statistics.StatisticsScreen
import com.grommade.composetodo.ui_task_list.TaskListUi
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
        addRoutHistory(navController, drawerGesturesEnabled)

        addRoutSettings(navController, drawerGesturesEnabled)
        addRoutSettingsGeneralTask(navController)
        addRoutSettingsRegularTask(navController)
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
    HomeUi(
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
    TaskListUi(navController)
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

private fun NavGraphBuilder.addRoutHistory(
    navController: NavHostController,
    drawerGesturesEnabled: (Boolean) -> Unit
) = composable(
    route = MainRoute.HistoryChildRoute.route
) {
    drawerGesturesEnabled(false)
    HistoryScreen(
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

private fun NavGraphBuilder.addRoutSettingsGeneralTask(
    navController: NavHostController
) = composable(
    route = SettingsRoute.SettingsGeneralChildRoute.route
) {
    SettingsGeneralTaskScreen()
}

private fun NavGraphBuilder.addRoutSettingsRegularTask(
    navController: NavHostController
) = composable(
    route = SettingsRoute.SettingsRegularTaskChildRoute.route
) {
    SettingsRegularTaskScreen()
}

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

@ExperimentalComposeUiApi
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