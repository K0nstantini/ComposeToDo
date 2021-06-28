package com.grommade.composetodo.db

import androidx.room.TypeConverter
import com.grommade.composetodo.add_classes.MyCalendar
import com.grommade.composetodo.enums.ModeGenerationSingleTasks
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
}