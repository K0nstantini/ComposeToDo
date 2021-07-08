package com.grommade.composetodo.ui_settings.single_task.time_and_frequency

sealed class SetSTaskFreqActions {
    data class Mode(val value: Int) : SetSTaskFreqActions()
    data class PeriodFrom(val value: Int) : SetSTaskFreqActions()
    data class PeriodTo(val value: Int) : SetSTaskFreqActions()
    data class EveryFewDays(val value: String) : SetSTaskFreqActions()
    data class DaysOfWeek(val value: List<Int>) : SetSTaskFreqActions()
    data class CountTasks(val value: String) : SetSTaskFreqActions()
    data class Frequency(val from: String, val to: String) : SetSTaskFreqActions()
    object PeriodNoRestrictions : SetSTaskFreqActions()
    object DaysNoRestriction : SetSTaskFreqActions()
    object Back : SetSTaskFreqActions()
}