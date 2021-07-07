package com.grommade.composetodo.ui_task_list

import androidx.compose.runtime.Immutable
import com.grommade.composetodo.data.entity.Task

@Immutable
data class TaskListViewState(
    val tasks: List<Task> = emptyList(),
) {
    companion object {
        val Empty = TaskListViewState()
    }
}