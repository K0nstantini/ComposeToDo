package com.grommade.composetodo

import com.grommade.composetodo.add_classes.MyCalendar
import com.grommade.composetodo.data.dao.HistoryDao
import com.grommade.composetodo.data.dao.SettingsDao
import com.grommade.composetodo.data.dao.TaskDao
import com.grommade.composetodo.data.entity.History
import com.grommade.composetodo.data.entity.Settings
import com.grommade.composetodo.data.entity.Task
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
    private val historyDao: HistoryDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    /** ============================== Settings ==============================================================*/

    val settingsFlow: Flow<Settings> = settingsDao.getSettingsFlow()

    suspend fun updateSettings(set: Settings) = withContext(ioDispatcher) {
        settingsDao.update(set)
    }

    suspend fun getSettings(): List<Settings> = withContext(ioDispatcher) {
        settingsDao.getSettings()
    }

    /** ============================== Tasks ==============================================================*/


    suspend fun saveTask(task: Task)= withContext(ioDispatcher) {
        val dateNow = MyCalendar.now()
        if (task.isNew) {
            taskDao.insert(task)
//            val history = History(date = dateNow, value = "Inserted new task: '${task.name}'")
//            historyDao.insert(history)
        } else {
            taskDao.update(task)
//            val history = History(date = dateNow, value = "Updated task: '${task.name}'")
//            historyDao.insert(history)
        }
    }

    suspend fun deleteTask(task: Task) = withContext(ioDispatcher) {
        taskDao.deleteTasks(getTasks().nestedTasks(task))
    }

    suspend fun deleteTasks(tasks: List<Task>) = withContext(ioDispatcher) {
        tasks.forEach { task ->
            taskDao.deleteTasks(getTasks().nestedTasks(task))
        }
    }

    fun getTasksFlow(type: TypeTask): Flow<List<Task>> = when (type) {
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

    val readyToActivateSingleTasks = taskDao.getNoActiveTasksFlow(TypeTask.SINGLE_TASK.name)

    private suspend fun getTasks() = withContext(Dispatchers.IO) { taskDao.getTasks() }

    suspend fun getTask(id: Long) = withContext(Dispatchers.IO) { taskDao.getTask(id) }

    /** ============================== History ==============================================================*/

    val historyFlow: Flow<List<History>> = historyDao.getHistoryFlow()

    suspend fun saveHistory(history: History) = withContext(ioDispatcher) {
        if (history.isNew) historyDao.insert(history) else historyDao.update(history)
    }

    suspend fun deleteHistory(history: History) = withContext(ioDispatcher) {
        historyDao.delete(history)
    }

    suspend fun deleteAllHistory() = withContext(ioDispatcher) {
        historyDao.deleteAll()
    }
}