package com.grommade.composetodo.data

import androidx.room.TypeConverter
import com.grommade.composetodo.add_classes.MyCalendar
import com.grommade.composetodo.enums.ModeGenerationSingleTasks
import com.grommade.composetodo.enums.ScheduleTask
import com.grommade.composetodo.enums.TypeTask

class Converters {

    @TypeConverter
    fun fromMyCalendar(date: MyCalendar) = date.milli

    @TypeConverter
    fun toMyCalendar(milli: Long) = MyCalendar(milli)

    @TypeConverter
    fun fromTypeTask(typeTask: TypeTask) = typeTask.name

    @TypeConverter
    fun toTypeTask(name: String) = TypeTask.valueOf(name)

    @TypeConverter
    fun fromModeGenerationSingleTasks(mode: ModeGenerationSingleTasks) = mode.name

    @TypeConverter
    fun toModeGenerationSingleTasks(name: String) = ModeGenerationSingleTasks.valueOf(name)

    @TypeConverter
    fun fromScheduleTask(schedule: ScheduleTask) = schedule.name

    @TypeConverter
    fun toScheduleTask(name: String) = ScheduleTask.valueOf(name)

    @TypeConverter
    fun fromListLong(list: List<Long>) = list.joinToString()

    @TypeConverter
    fun toListLong(str: String) = str.split(", ").map { it.toLong() }
}