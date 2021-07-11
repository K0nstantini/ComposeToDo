package com.grommade.composetodo.ui_task.ui_type_task

import com.grommade.composetodo.enums.TypeTask

sealed class TypeTaskActions {
    data class ChangeType(val value: TypeTask) : TypeTaskActions()
    object Confirm : TypeTaskActions()
    object Close : TypeTaskActions()
}