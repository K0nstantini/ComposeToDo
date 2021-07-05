package com.grommade.composetodo.data.dao

import androidx.room.Dao
import androidx.room.Query
import com.grommade.composetodo.data.entity.Settings
import kotlinx.coroutines.flow.Flow

@Dao
abstract class SettingsDao : EntityDao<Settings>() {

    @Query("SELECT * FROM settings_table WHERE id == 1")
    abstract fun observeSettings(): Flow<Settings>

    @Query("SELECT * FROM settings_table WHERE id == 1")
    abstract suspend fun getSettings(): Settings?

    @Query("SELECT COUNT(*) FROM settings_table")
    abstract suspend fun getCountSettings(): Int

}