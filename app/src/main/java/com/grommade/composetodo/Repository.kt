package com.grommade.composetodo

import androidx.annotation.WorkerThread
import com.grommade.composetodo.db.dao.SettingsDao
import com.grommade.composetodo.db.dao.TaskDao
import com.grommade.composetodo.db.entity.Settings
import com.grommade.composetodo.db.entity.Task
import com.grommade.composetodo.enums.TypeTask
import com.grommade.composetodo.util.nestedTasks
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class Repository @Inject constructor(private val settingsDao: SettingsDao, private val taskDao: TaskDao) {

    /** Settings */

    val settingsFlow: Flow<Settings> = settingsDao.getSettingsFlow()

    @WorkerThread
    suspend fun insertSettings() = settingsDao.insert(Settings())

    @WorkerThread
    suspend fun updateSettings(set: Settings) = settingsDao.update(set)

    suspend fun getSettings() = withContext(Dispatchers.IO) { settingsDao.getSettings() }

    /** ======================================================================================= */

    /** Tasks */

    @WorkerThread
    suspend fun insertTask(task: Task) = taskDao.insert(task)

    @WorkerThread
    suspend fun updateTask(task: Task) = taskDao.update(task)

    @WorkerThread
    suspend fun updateTasks(tasks: List<Task>) = taskDao.updateTasks(tasks)

    @WorkerThread
    suspend fun deleteTask(task: Task) = taskDao.deleteTasks(getTasks().nestedTasks(task))

    @WorkerThread
    suspend fun deleteTasks(tasks: List<Task>) = tasks.forEach { task ->
        taskDao.deleteTasks(getTasks().nestedTasks(task))
    }

    fun getTasksFlow(type: TypeTask) = when (type) {
        TypeTask.REGULAR_TASK -> taskDao.getTasksFlow(type.name)
        TypeTask.SINGLE_TASK -> taskDao.getTasksFlow(type.name)
    }

    suspend fun getTasks() = withContext(Dispatchers.IO) { taskDao.getTasks() }

    suspend fun getTask(id: Long) = withContext(Dispatchers.IO) { taskDao.getTask(id) }

    /** ======================================================================================= */



}