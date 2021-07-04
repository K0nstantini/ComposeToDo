package com.grommade.composetodo.data.dao

import androidx.room.*
import com.grommade.composetodo.data.entity.History
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(set: History)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(set: History)

    @Delete
    suspend fun delete(set: History)

    @Query("DELETE FROM history_table")
    suspend fun deleteAll()

    @Query("SELECT * FROM history_table ORDER BY date DESC")
    fun getHistoryFlow(): Flow<List<History>>
}