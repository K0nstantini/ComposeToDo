package com.grommade.composetodo.db.dao

import androidx.room.*
import com.grommade.composetodo.db.entity.Task
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

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

    @Query("DELETE FROM task_table")
    suspend fun deleteAll()

    @Query("SELECT * FROM task_table WHERE id = :id")
    fun getTask(id: Long): Task?

    @Query("SELECT * FROM task_table WHERE type = :type ORDER BY name ASC")
    fun getTasksFlow(type: String): Flow<List<Task>>

    @Query("SELECT * FROM task_table ORDER BY name ASC")
    fun getTasks(): List<Task>

    @Query("SELECT * FROM task_table  WHERE type = :type ORDER BY name ASC")
    fun getSingleTasks(type: String): List<Task>

    @Query("SELECT COUNT(*) FROM task_table WHERE `group` = 0")
    fun getCountTasks(): Int

    @Query("SELECT * FROM task_table WHERE dateActivation > 0")
    fun getActiveTasks(): Flow<List<Task>>

    @Query("SELECT * FROM task_table WHERE dateActivation = 0 AND type = :type")
    fun getNoActiveTasks(type: String): List<Task>

}