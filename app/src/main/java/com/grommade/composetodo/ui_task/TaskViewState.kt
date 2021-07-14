package com.grommade.composetodo.ui_task

import androidx.compose.runtime.Immutable
import com.grommade.composetodo.data.entity.RandomTask

@Immutable
data class TaskViewState(
    val task: RandomTask = RandomTask(),
    val parent: String? = ""
) {
    companion object {
        val Empty = TaskViewState()
    }
}