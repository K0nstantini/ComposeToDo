package com.grommade.composetodo.data.repos

import com.grommade.composetodo.data.dao.SingleTaskDao
import com.grommade.composetodo.data.entity.RandomTask
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RepoTask @Inject constructor(
    private val singleTaskDao: SingleTaskDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    val allTasks: Flow<List<RandomTask>> = singleTaskDao.getTasksFlow()
    val activeTasks: Flow<List<RandomTask>> = singleTaskDao.getActiveTasks()
    val groups: Flow<List<RandomTask>> = singleTaskDao.getGroups()

    suspend fun saveTask(task: RandomTask): Long = withContext(ioDispatcher) {
        singleTaskDao.insertOrUpdate(task)
    }

    suspend fun deleteTask(task: RandomTask) = withContext(ioDispatcher) {
        singleTaskDao.delete(task)
    }

    suspend fun deleteAllTasks() = withContext(ioDispatcher) {
        singleTaskDao.deleteAll()
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