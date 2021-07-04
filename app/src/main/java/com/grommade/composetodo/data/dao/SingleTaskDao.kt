package com.grommade.composetodo.data.dao

import androidx.room.*
import com.grommade.composetodo.data.entity.Task
import kotlinx.coroutines.flow.Flow

@Dao
interface SingleTaskDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: Task): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(task: Task)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateTasks(tasks: List<Task>)

    @Delete
    suspend fun delete(task: Task)

    @Delete
    suspend fun deleteTasks(tasks: List<Task>)

    @Query("DELETE FROM single_task_table")
    suspend fun deleteAll()

    @Query("SELECT * FROM single_task_table WHERE id = :id")
    suspend fun getTask(id: Long): Task?

    @Query("SELECT * FROM single_task_table ORDER BY name ASC")
    fun getTasksFlow(): Flow<List<Task>>

    @Query("SELECT * FROM single_task_table ORDER BY name ASC")
    fun getAllTasks(): List<Task>

    @Query("SELECT COUNT(*) FROM single_task_table WHERE `group` = 0")
    fun getCountTasks(): Int

    @Query("SELECT * FROM single_task_table WHERE dateActivation > 0")
    fun getActiveTasks(): Flow<List<Task>>

    @Query("SELECT * FROM single_task_table WHERE dateActivation = 0")
    suspend fun getNoActiveTasks(): List<Task>

    @Query("SELECT * FROM single_task_table WHERE dateActivation = 0 AND type = :type")
    fun getNoActiveTasksFlow(type: String): Flow<List<Task>>

}