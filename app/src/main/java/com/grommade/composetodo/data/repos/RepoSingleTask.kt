package com.grommade.composetodo.data.repos

import com.grommade.composetodo.data.dao.SingleTaskDao
import com.grommade.composetodo.data.entity.Task
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RepoSingleTask @Inject constructor(
    private val singleTaskDao: SingleTaskDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    val allTasks: Flow<List<Task>> = singleTaskDao.getTasksFlow()
    val activeTasks: Flow<List<Task>> = singleTaskDao.getActiveTasks()

    suspend fun saveTask(task: Task) = withContext(ioDispatcher) {
        when (task.isNew) {
            true -> singleTaskDao.insert(task)
            false -> singleTaskDao.update(task)
        }
    }

    suspend fun deleteTask(task: Task) = withContext(ioDispatcher) {
        singleTaskDao.delete(task)
    }

    suspend fun getAllTasks() = withContext(Dispatchers.IO) {
        singleTaskDao.getAllTasks()
    }

    suspend fun getTask(id: Long) = withContext(Dispatchers.IO) {
        singleTaskDao.getTask(id)
    }

    suspend fun getNoActivateTasks() = withContext(Dispatchers.IO) {
        singleTaskDao.getNoActiveTasks()
    }
}