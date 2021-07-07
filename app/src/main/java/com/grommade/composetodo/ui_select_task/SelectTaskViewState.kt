package com.grommade.composetodo.ui_select_task

import com.grommade.composetodo.data.entity.Task
import javax.annotation.concurrent.Immutable

@Immutable
data class SelectTaskViewState(
    val tasks: List<Task> = emptyList()
) {
    companion object {
        val Empty = SelectTaskViewState()
    }
}