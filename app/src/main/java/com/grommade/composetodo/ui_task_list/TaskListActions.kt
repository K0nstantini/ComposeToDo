package com.grommade.composetodo.ui_task_list

import com.grommade.composetodo.data.entity.Task

sealed class TaskListActions {
    data class OpenCloseGroup(val task: Task) : TaskListActions()
    data class OpenTask(val id: Long) : TaskListActions()
    object NewTask : TaskListActions()
    data class PerformTask(val task: Task) : TaskListActions()
    data class DeleteTasks(val tasks: List<Task>) : TaskListActions()
    object PopulateDBWithTasks : TaskListActions()
    object Back : TaskListActions()
}