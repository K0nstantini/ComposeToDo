package com.grommade.composetodo.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.grommade.composetodo.data.dao.HistoryDao
import com.grommade.composetodo.data.dao.SettingsDao
import com.grommade.composetodo.data.dao.TaskDao
import com.grommade.composetodo.data.entity.History
import com.grommade.composetodo.data.entity.Settings
import com.grommade.composetodo.data.entity.Task

@Database(
    entities = [Settings::class, Task::class, History::class],
    version = 1,
    exportSchema = false,
)
@TypeConverters(Converters::class)
abstract class AppDataBase : RoomDatabase() {
    abstract fun SettingsDao(): SettingsDao
    abstract fun TaskDao(): TaskDao
    abstract fun HistoryDao(): HistoryDao
}