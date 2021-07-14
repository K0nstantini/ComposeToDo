package com.grommade.composetodo.ui_home

import androidx.compose.runtime.Immutable
import com.grommade.composetodo.data.entity.RandomTask

@Immutable
data class HomeViewState(
    val tasks: List<RandomTask> = emptyList(),
) {
    companion object {
        val Empty = HomeViewState()
    }
}