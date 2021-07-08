package com.grommade.composetodo.ui_single_task

import androidx.compose.runtime.Immutable

@Immutable
data class SingleTaskViewState(
    val title: String? = null,
    val name: String = "",
    val group: Boolean = false,
    val parentStr: String? = null,
    val parentId: Long = -1,
    val dateStart: String = "",
    val deadline: Int = 0,
) {
    companion object {
        val Empty = SingleTaskViewState()
    }
}