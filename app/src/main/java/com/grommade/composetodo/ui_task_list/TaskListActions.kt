package com.grommade.composetodo.ui_task_list

import com.grommade.composetodo.data.entity.Task

sealed class TaskListActions {
    data class OpenCloseGroup(val task: Task) : TaskListActions()
    data class OpenTask(val id: Long): TaskListActions()
    object CloseActionMode: TaskListActions()
    object PopulateDBWithTasks: TaskListActions()
    data class BackWithID(val id: Long): TaskListActions()
    object Back: TaskListActions()
}