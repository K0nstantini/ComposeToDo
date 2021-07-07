package com.grommade.composetodo.ui_select_task

sealed class SelectTaskActions {
    object Close : SelectTaskActions()
    data class Confirmation(val id: Long) : SelectTaskActions()
}