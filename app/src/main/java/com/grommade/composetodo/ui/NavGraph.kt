package com.grommade.composetodo.ui

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.grommade.composetodo.*
import com.grommade.composetodo.ui_history.HistoryUi
import com.grommade.composetodo.ui_home.HomeUi
import com.grommade.composetodo.ui_select_task.SelectTaskUi
import com.grommade.composetodo.ui_settings.SettingsUi
import com.grommade.composetodo.ui_settings.general.SettingsGeneralTaskScreen
import com.grommade.composetodo.ui_settings.regular_task.SettingsRegularTaskScreen
import com.grommade.composetodo.ui_settings.single_task.SettingsSingleTaskUi
import com.grommade.composetodo.ui_settings.single_task.time_and_frequency.SettingsSingleTaskFrequencyUi
import com.grommade.composetodo.ui_statistics.StatisticsUi
import com.grommade.composetodo.ui_task.TaskUi
import com.grommade.composetodo.ui_task.ui_type_task.TypeTaskUi
import com.grommade.composetodo.ui_task_list.TaskListUi
import com.grommade.composetodo.util.Keys
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
        addRoutTask(navController)
        addRoutTypeTask(navController)

        addRoutSelectTask(navController)
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
    StatisticsUi(navController)
}

private fun NavGraphBuilder.addRoutSettings(
    navController: NavHostController,
    drawerGesturesEnabled: (Boolean) -> Unit
) = composable(
    route = MainRoute.SettingsChildRoute.route
) {
    drawerGesturesEnabled(false)
    SettingsUi(navController)
}

private fun NavGraphBuilder.addRoutHistory(
    navController: NavHostController,
    drawerGesturesEnabled: (Boolean) -> Unit
) = composable(
    route = MainRoute.HistoryChildRoute.route
) {
    drawerGesturesEnabled(false)
    HistoryUi(navController)
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
private fun NavGraphBuilder.addRoutTask(
    navController: NavHostController
) = composable(
    route = TasksRoute.TaskChildRoute.route,
    arguments = TasksRoute.TaskChildRoute.addArguments()
) {
//    SingleTaskUi(navController)
    TaskUi(navController)
}

/** Add Tasks */

private fun NavGraphBuilder.addRoutTypeTask(
    navController: NavHostController
) = composable(
    route = TypeTaskRoute.TypeTaskChildRoute.route,
    arguments = TypeTaskRoute.TypeTaskChildRoute.addArguments()
) {
    TypeTaskUi(navController)
}

/** Select Tasks */

private fun NavGraphBuilder.addRoutSelectTask(
    navController: NavHostController
) = composable(
    route = SelectTaskRoute.SingleTaskSelectRoute.route,
    arguments = SelectTaskRoute.SingleTaskSelectRoute.addArguments()
) { backStackEntry ->
    SelectTaskUi(
        id = backStackEntry.arguments?.getLong(Keys.TASK_ID) ?: -1L,
        navController
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
    SettingsSingleTaskUi(navController)
}

/** Single Task Settings */

@ExperimentalComposeUiApi
@ExperimentalMaterialApi
private fun NavGraphBuilder.addRoutSettingsSingleTaskFrequency(
    navController: NavHostController
) = composable(
    route = SettingsSingleTaskRoute.SettingsSingleTaskFrequencyChildRoute.route
) {
    SettingsSingleTaskFrequencyUi(navController)
}