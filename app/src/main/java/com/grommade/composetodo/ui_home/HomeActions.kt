package com.grommade.composetodo.ui_home

import com.grommade.composetodo.data.entity.Task

sealed class HomeActions {
    data class MarkTaskDone(val task: Task) : HomeActions()
    object Refresh : HomeActions()
    object ClearStateTasks : HomeActions()
    object OpenDrawer : HomeActions()
}