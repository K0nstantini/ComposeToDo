package com.grommade.composetodo.data.dao

import androidx.room.*
import com.grommade.composetodo.data.entity.Settings
import kotlinx.coroutines.flow.Flow

@Dao
interface SettingsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(set: Settings)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(set: Settings)

    @Delete
    suspend fun delete(set: Settings)

    @Query("DELETE FROM settings_table")
    suspend fun deleteAll()

    @Query("SELECT * FROM settings_table LIMIT 1")
    fun getSettingsFlow(): Flow<Settings>

    @Query("SELECT * FROM settings_table")
    suspend fun getSettings(): List<Settings>

    @Query("SELECT COUNT(*) FROM settings_table")
    suspend fun getCountSettings(): Int

}