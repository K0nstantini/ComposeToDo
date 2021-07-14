package com.grommade.composetodo.data.dao

import androidx.room.Dao
import androidx.room.Query
import com.grommade.composetodo.data.entity.RandomTask
import kotlinx.coroutines.flow.Flow

@Dao
abstract class SingleTaskDao : EntityDao<RandomTask>() {

    @Query("DELETE FROM random_task_table")
    abstract suspend fun deleteAll()

    @Query("SELECT * FROM random_task_table WHERE id = :id")
    abstract suspend fun getTask(id: Long): RandomTask?

    @Query("SELECT * FROM random_task_table ORDER BY name ASC")
    abstract suspend fun getAllTasks(): List<RandomTask>

    @Query("SELECT * FROM random_task_table WHERE dateActivation = 0")
    abstract suspend fun getNoActiveTasks(): List<RandomTask>

    @Query("SELECT * FROM random_task_table WHERE `group` = 1 ORDER BY name ASC")
    abstract fun getGroups(): Flow<List<RandomTask>>

    @Query("SELECT * FROM random_task_table ORDER BY name ASC")
    abstract fun getTasksFlow(): Flow<List<RandomTask>>

    @Query("SELECT * FROM random_task_table WHERE dateActivation > 0")
    abstract fun getActiveTasks(): Flow<List<RandomTask>>



}