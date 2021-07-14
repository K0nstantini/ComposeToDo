package com.grommade.composetodo

import androidx.navigation.NavType
import androidx.navigation.compose.navArgument
import com.grommade.composetodo.enums.TypeTask
import com.grommade.composetodo.util.Keys


sealed class MainRoute(val route: String) {
    object HomeChildRoute : MainRoute("main")

    object TaskListChildRoute : MainRoute("taskList/{taskTypeKey}?taskID={taskID}") {
        fun createRoute(type: TypeTask, id: Long = -1) =
            "taskList/${type.name}?taskID=$id"

        fun addArguments() = listOf(
            navArgument(Keys.TASK_TYPE_KEY) {
                type = NavType.StringType
            },
            navArgument(Keys.TASK_ID) {
                defaultValue = -1L
            }
        )

    }

    object StatisticsChildRoute : MainRoute("statistics")
    object SettingsChildRoute : MainRoute("settings")
    object HistoryChildRoute : MainRoute("history")
}

sealed class TasksRoute(val route: String) {
    object RegularTaskChildRoute : TasksRoute("taskList/REGULAR_TASK/addEditTask?taskID={taskID}") {
        fun createRoute(id: Long) = "taskList/REGULAR_TASK/addEditTask?taskID=$id"
        fun addArguments() = addArgumentTaskID()
    }

    object TaskChildRoute : TasksRoute("taskList/task/addEditTask?taskTypeKey={taskTypeKey}&taskID={taskID}") {
        fun createRoute(type: TypeTask, id: Long = -1) = "taskList/task/addEditTask?taskTypeKey=${type.name}&taskID=$id"
        fun addArguments() = listOf(
            navArgument(Keys.TASK_TYPE_KEY) {
                defaultValue = TypeTask.EXACT_TIME.name
            },
            navArgument(Keys.TASK_ID) {
                defaultValue = -1L
            }
        )
    }
}

sealed class TypeTaskRoute(val route: String) {
    object TypeTaskChildRoute : TasksRoute("taskList/SINGLE_TASK/addEditTask/type?type={type}") {
        fun createRoute(type: String) = "taskList/SINGLE_TASK/addEditTask/type?type=$type"
        fun addArguments() = listOf(
            navArgument(Keys.TASK_TYPE_KEY) {
                type = NavType.StringType
            }
        )
    }

}

sealed class SelectTaskRoute(val route: String) {
    object SingleTaskSelectRoute : SelectTaskRoute("taskList/SINGLE_TASK/addEditTask/selectTask?taskID={taskID}") {
        fun createRoute(id: Long) = "taskList/SINGLE_TASK/addEditTask/selectTask?taskID=$id"
        fun addArguments() = addArgumentTaskID()
    }
}

sealed class SettingsRoute(val route: String) {
    object SettingsGeneralChildRoute : SettingsRoute("settings/general")
    object SettingsRegularTaskChildRoute : SettingsRoute("settings/REGULAR_TASK")
    object SettingsSingleTaskChildRoute : SettingsRoute("settings/SINGLE_TASK")
}

sealed class SettingsSingleTaskRoute(val route: String) {
    object SettingsSingleTaskFrequencyChildRoute : SettingsSingleTaskRoute("settings/SINGLE_TASK/frequency")
}

private fun addArgumentTaskID() = listOf(navArgument(Keys.TASK_ID) {
    defaultValue = -1L
})