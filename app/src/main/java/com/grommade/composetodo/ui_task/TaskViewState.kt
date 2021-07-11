package com.grommade.composetodo.ui_task

import androidx.compose.runtime.Immutable
import com.grommade.composetodo.data.entity.Task

@Immutable
data class TaskViewState(
    val task: Task = Task(),
    val parent: String? = ""
) {
    companion object {
        val Empty = TaskViewState()
    }
}