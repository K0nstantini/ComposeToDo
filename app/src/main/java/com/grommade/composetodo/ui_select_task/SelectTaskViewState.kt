package com.grommade.composetodo.ui_select_task

import androidx.compose.runtime.Immutable
import com.grommade.composetodo.data.entity.RandomTask

@Immutable
data class SelectTaskViewState(
    val tasks: List<RandomTask> = emptyList()
) {
    companion object {
        val Empty = SelectTaskViewState()
    }
}