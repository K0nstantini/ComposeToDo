package com.grommade.composetodo.ui_task_list

import com.grommade.composetodo.data.entity.RandomTask
import com.grommade.composetodo.enums.TypeTask

sealed class TaskListActions {
    data class OpenCloseGroup(val task: RandomTask) : TaskListActions()
    data class OpenTask(val type: TypeTask, val id: Long) : TaskListActions()
    data class NewTask(val type: TypeTask) : TaskListActions()
    data class PerformTask(val task: RandomTask) : TaskListActions()
    data class DeleteTasks(val tasks: List<RandomTask>) : TaskListActions()
    object PopulateDBWithTasks : TaskListActions()
    object Back : TaskListActions()
}