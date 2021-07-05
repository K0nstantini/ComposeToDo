package com.grommade.composetodo.data.dao

import androidx.room.*
import com.grommade.composetodo.data.entity.History
import kotlinx.coroutines.flow.Flow

@Dao
abstract class HistoryDao : EntityDao<History>() {

    @Query("DELETE FROM history_table")
    abstract suspend fun deleteAll()

    @Query("SELECT * FROM history_table ORDER BY date DESC")
    abstract fun getHistoryFlow(): Flow<List<History>>
}