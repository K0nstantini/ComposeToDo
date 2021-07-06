package com.grommade.composetodo.ui_task_list

import androidx.compose.runtime.Immutable
import com.grommade.composetodo.data.entity.Task
import com.grommade.composetodo.enums.ModeTaskList

@Immutable
data class TaskListViewState(
    val tasks: List<Task> = emptyList(),
    val mode: ModeTaskList = ModeTaskList.DEFAULT,
    val actionMode: Boolean = false
) {
    companion object {
        val Empty = TaskListViewState()
    }
}