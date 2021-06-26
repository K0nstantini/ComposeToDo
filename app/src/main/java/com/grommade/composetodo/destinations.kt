package com.grommade.composetodo

import androidx.navigation.NavType
import androidx.navigation.compose.navArgument
import com.grommade.composetodo.enums.ModeTaskList
import com.grommade.composetodo.enums.TypeTask
import com.grommade.composetodo.util.Keys


sealed class MainScreen(val route: String) {
    object Home : MainScreen("main")

    object TaskList : MainScreen("taskList/{taskTypeKey}?taskListModeKey={taskListModeKey}&taskID={taskID}") {
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

    object Statistics : MainScreen("statistics")
}

sealed class TasksScreen(val route: String) {
    object RegularTask : TasksScreen("taskList/REGULAR_TASK/addEditTask?taskID={taskID}") {
        fun createRoute(id: Long) = "taskList/REGULAR_TASK/addEditTask?taskID=$id"
        fun addArguments() = addArgumentTaskID()
    }

    object SingleTask : TasksScreen("taskList/SINGLE_TASK/addEditTask?taskID={taskID}") {
        fun createRoute(id: Long) = "taskList/SINGLE_TASK/addEditTask?taskID=$id"
        fun addArguments() = addArgumentTaskID()
    }

    fun addArgumentTaskID() = listOf(navArgument(Keys.TASK_ID) {
        defaultValue = -1L
    })
}