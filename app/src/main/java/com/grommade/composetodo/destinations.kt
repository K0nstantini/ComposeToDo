package com.grommade.composetodo

import com.grommade.composetodo.enums.TypeTask


sealed class MainScreen(val route: String) {
    object Home : MainScreen("main")
    object TaskList : MainScreen("taskList/{taskTypeKey}") {
        fun createRoute(type: TypeTask) = "taskList/${type.name}"
    }
}

sealed class TasksScreen(val route: String) {
    object RegularTask : TasksScreen("taskList/REGULAR_TASK?taskID={taskID}") {
        fun createRoute(id: Long) = "taskList/REGULAR_TASK?taskID=$id"
    }

    object SingleTask : TasksScreen("taskList/SINGLE_TASK?taskID={taskID}") {
        fun createRoute(id: Long) = "taskList/SINGLE_TASK?taskID=$id"
    }
}
