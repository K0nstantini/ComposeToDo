package com.grommade.composetodo.ui_home

import com.grommade.composetodo.data.entity.RandomTask

sealed class HomeActions {
    data class MarkTaskDone(val task: RandomTask) : HomeActions()
    object Refresh : HomeActions()
    object ClearStateTasks : HomeActions()
    object OpenDrawer : HomeActions()
}