package com.grommade.composetodo.ui_task

sealed class TaskActions {
    data class ChangeName(val value: String): TaskActions()
    object ChangeType: TaskActions()
    object Save: TaskActions()
    object Close: TaskActions()
}