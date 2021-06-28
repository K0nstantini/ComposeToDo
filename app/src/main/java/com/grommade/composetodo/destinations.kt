package com.grommade.composetodo

import androidx.navigation.NavType
import androidx.navigation.compose.navArgument
import com.grommade.composetodo.enums.ModeTaskList
import com.grommade.composetodo.enums.TypeTask
import com.grommade.composetodo.util.Keys


sealed class MainRoute(val route: String) {
    object HomeChildRoute : MainRoute("main")

    object TaskListChildRoute : MainRoute("taskList/{taskTypeKey}?taskListModeKey={taskListModeKey}&taskID={taskID}") {
        fun createRoute(mode: ModeTaskList, type: TypeTask, id: Long = -1) =
            "taskList/${type.name}?taskListModeKey=${mode.name}&taskID=$id"

        fun addArguments() = listOf(
            navArgument(Keys.TASK_TYPE_KEY) {
                type = NavType.StringType
            },
            navArgument(Keys.TASK_LIST_MODE_KEY) {
                defaultValue = ModeTaskList.DEFAULT.name
            },
            navArgument(Keys.TASK_ID) {
                defaultValue = -1L
            }
        )

    }

    object StatisticsChildRoute : MainRoute("statistics")
    // TODO: Change name
    object SettingsChildRoute : MainRoute("settings")
}

sealed class TasksRoute(val route: String) {
    object RegularTaskChildRoute : TasksRoute("taskList/REGULAR_TASK/addEditTask?taskID={taskID}") {
        fun createRoute(id: Long) = "taskList/REGULAR_TASK/addEditTask?taskID=$id"
        fun addArguments() = addArgumentTaskID()
    }

    object SingleTaskChildRoute : TasksRoute("taskList/SINGLE_TASK/addEditTask?taskID={taskID}") {
        fun createRoute(id: Long) = "taskList/SINGLE_TASK/addEditTask?taskID=$id"
        fun addArguments() = addArgumentTaskID()
    }

    fun addArgumentTaskID() = listOf(navArgument(Keys.TASK_ID) {
        defaultValue = -1L
    })
}

sealed class SettingsRoute(val route: String) {
    object SettingsGeneralChildRoute : SettingsRoute("settings/general")
    object SettingsRegularTaskChildRoute : SettingsRoute("settings/REGULAR_TASK")
    object SettingsSingleTaskChildRoute : SettingsRoute("settings/SINGLE_TASK")
}

sealed class SettingsSingleTaskRoute(val route: String) {
    object SettingsSingleTaskFrequencyChildRoute : SettingsSingleTaskRoute("settings/SINGLE_TASK/frequency")
}