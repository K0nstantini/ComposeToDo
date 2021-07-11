package com.grommade.composetodo.ui_task_list

import com.grommade.composetodo.data.entity.Task
import com.grommade.composetodo.enums.TypeTask

sealed class TaskListActions {
    data class OpenCloseGroup(val task: Task) : TaskListActions()
    data class OpenTask(val type: TypeTask, val id: Long) : TaskListActions()
    data class NewTask(val type: TypeTask) : TaskListActions()
    data class PerformTask(val task: Task) : TaskListActions()
    data class DeleteTasks(val tasks: List<Task>) : TaskListActions()
    object PopulateDBWithTasks : TaskListActions()
    object Back : TaskListActions()
}