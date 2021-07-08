package com.grommade.composetodo.ui_statistics

import androidx.compose.runtime.Immutable

@Immutable
data class StatisticsViewState(
    val singlePoints: Int = 0
) {
    companion object {
        val Empty = StatisticsViewState()
    }
}