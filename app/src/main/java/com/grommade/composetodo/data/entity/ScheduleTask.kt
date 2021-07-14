package com.grommade.composetodo.data.entity

import androidx.compose.runtime.Immutable
import androidx.room.ColumnInfo
import com.grommade.composetodo.add_classes.MyCalendar
import com.grommade.composetodo.enums.ScheduleTask

@Immutable
data class ScheduleTask(
    val countTasksFrom: Int = 1,
    val countTasksTo: Int = 1,
    @ColumnInfo(name = "schedule_type") val type: ScheduleTask = ScheduleTask.NO_RESTRICTIONS,
    val weekdays: String = "",
    val workDays: Int = WORK_DAYS,
    val daysOff: Int = DAYS_OFF,
    val flexibility: Boolean = false,
    val timeFrom: MyCalendar = MyCalendar(),
    val timeTo: MyCalendar = MyCalendar(),
) {

    companion object {
        const val WORK_DAYS = 6
        const val DAYS_OFF = 1
    }
}