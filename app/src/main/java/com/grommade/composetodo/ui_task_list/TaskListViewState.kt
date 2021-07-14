package com.grommade.composetodo.ui_task_list

import androidx.compose.runtime.Immutable
import com.grommade.composetodo.data.entity.RandomTask

@Immutable
data class TaskListViewState(
    val tasks: List<RandomTask> = emptyList(),
) {
    companion object {
        val Empty = TaskListViewState()
    }
}