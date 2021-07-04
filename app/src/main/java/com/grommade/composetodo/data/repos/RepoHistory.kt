package com.grommade.composetodo.data.repos

import com.grommade.composetodo.data.dao.HistoryDao
import com.grommade.composetodo.data.entity.History
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RepoHistory @Inject constructor(
    private val historyDao: HistoryDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    val allHistory: Flow<List<History>> = historyDao.getHistoryFlow()

    suspend fun saveHistory(history: History) = withContext(ioDispatcher) {
        when (history.isNew) {
            true -> historyDao.insert(history)
            false -> historyDao.update(history)
        }
    }

    suspend fun deleteAllHistory() = withContext(ioDispatcher) {
        historyDao.deleteAll()
    }
}