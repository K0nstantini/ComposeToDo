package com.grommade.composetodo.ui_home

import androidx.compose.runtime.Immutable
import com.grommade.composetodo.data.entity.Task

@Immutable
data class HomeViewState(
    val tasks: List<Task> = emptyList(),
) {
    companion object {
        val Empty = HomeViewState()
    }
}