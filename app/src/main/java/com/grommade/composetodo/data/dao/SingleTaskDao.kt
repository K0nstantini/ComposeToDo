package com.grommade.composetodo.data.dao

import androidx.room.Dao
import androidx.room.Query
import com.grommade.composetodo.data.entity.Task
import kotlinx.coroutines.flow.Flow

@Dao
abstract class SingleTaskDao : EntityDao<Task>() {

    @Query("DELETE FROM single_task_table")
    abstract suspend fun deleteAll()

    @Query("SELECT * FROM single_task_table WHERE id = :id")
    abstract suspend fun getTask(id: Long): Task?

    @Query("SELECT * FROM single_task_table ORDER BY name ASC")
    abstract fun getTasksFlow(): Flow<List<Task>>

    @Query("SELECT * FROM single_task_table ORDER BY name ASC")
    abstract fun getAllTasks(): List<Task>

    @Query("SELECT * FROM single_task_table WHERE dateActivation > 0")
    abstract fun getActiveTasks(): Flow<List<Task>>

    @Query("SELECT * FROM single_task_table WHERE dateActivation = 0")
    abstract suspend fun getNoActiveTasks(): List<Task>

}