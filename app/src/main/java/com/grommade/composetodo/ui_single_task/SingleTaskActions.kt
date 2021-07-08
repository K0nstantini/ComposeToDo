package com.grommade.composetodo.ui_single_task

import com.grommade.composetodo.add_classes.MyCalendar

sealed class SingleTaskActions {
    data class ChangeName(val text: String): SingleTaskActions()
    data class ChangeGroup(val group: Boolean): SingleTaskActions()
    data class ChangeDateStart(val date: MyCalendar): SingleTaskActions()
    data class ChangeDeadline(val deadline: Int): SingleTaskActions()
    object SelectParent: SingleTaskActions()
    object ClearParent: SingleTaskActions()
    object Close: SingleTaskActions()
    object Save: SingleTaskActions()
}