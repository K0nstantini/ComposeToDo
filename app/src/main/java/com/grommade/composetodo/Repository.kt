package com.grommade.composetodo

import androidx.annotation.WorkerThread
import com.grommade.composetodo.db.dao.SettingsDao
import com.grommade.composetodo.db.dao.TaskDao
import com.grommade.composetodo.db.entity.Settings
import com.grommade.composetodo.db.entity.Task
import com.grommade.composetodo.enums.TypeTask
import com.grommade.composetodo.util.nestedTasks
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class Repository @Inject constructor(
    private val settingsDao: SettingsDao,
    private val taskDao: TaskDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    /** Settings */

    val settingsFlow: Flow<Settings> = settingsDao.getSettingsFlow()

    suspend fun insertSettings() = settingsDao.insert(Settings())

    suspend fun updateSettings(set: Settings) = settingsDao.update(set)

    suspend fun getSettings() = withContext(ioDispatcher) {settingsDao.getSettings()}

    /** ======================================================================================= */

    /** Tasks */

    suspend fun insertTask(task: Task) = taskDao.insert(task)

    suspend fun updateTask(task: Task) = taskDao.update(task)

    suspend fun saveTask(task: Task) {
        if (task.isNew) taskDao.insert(task) else taskDao.update(task)
    }

    suspend fun updateTasks(tasks: List<Task>) = taskDao.updateTasks(tasks)

    suspend fun deleteTask(task: Task) = taskDao.deleteTasks(getTasks().nestedTasks(task))

    suspend fun deleteTasks(tasks: List<Task>) = tasks.forEach { task ->
        taskDao.deleteTasks(getTasks().nestedTasks(task))
    }

    fun getTasksFlow(type: TypeTask) = when (type) {
        TypeTask.REGULAR_TASK -> taskDao.getTasksFlow(type.name)
        TypeTask.SINGLE_TASK -> taskDao.getTasksFlow(type.name)
    }

    val activatedSingleTasks = taskDao.getActiveTasks()

    suspend fun getAllSingleTasks() = withContext(Dispatchers.IO) {
        taskDao.getSingleTasks(TypeTask.SINGLE_TASK.name)
    }

    suspend fun getReadyToActivateSingleTasks() = withContext(Dispatchers.IO) {
        taskDao.getNoActiveTasks(TypeTask.SINGLE_TASK.name)
    }

    private suspend fun getTasks() = withContext(Dispatchers.IO) { taskDao.getTasks() }

    suspend fun getTask(id: Long) = withContext(Dispatchers.IO) { taskDao.getTask(id) }

    /** ======================================================================================= */


}