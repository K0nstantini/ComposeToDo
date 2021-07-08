package com.grommade.composetodo.ui_settings.single_task

import com.grommade.composetodo.add_classes.MyCalendar

sealed class SettingsSingleTaskActions {
    data class ChangeStartGeneration(val date: MyCalendar): SettingsSingleTaskActions()
    object ToTimeAndFrequency: SettingsSingleTaskActions()
    object Back: SettingsSingleTaskActions()
}