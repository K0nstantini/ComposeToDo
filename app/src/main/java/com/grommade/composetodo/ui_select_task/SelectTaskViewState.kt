package com.grommade.composetodo.ui_select_task

import androidx.compose.runtime.Immutable
import com.grommade.composetodo.data.entity.Task

@Immutable
data class SelectTaskViewState(
    val tasks: List<Task> = emptyList()
) {
    companion object {
        val Empty = SelectTaskViewState()
    }
}