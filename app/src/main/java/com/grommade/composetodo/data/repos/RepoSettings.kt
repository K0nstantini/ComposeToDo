package com.grommade.composetodo.data.repos

import com.grommade.composetodo.data.dao.SettingsDao
import com.grommade.composetodo.data.entity.Settings
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RepoSettings @Inject constructor(
    private val settingsDao: SettingsDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    val settingsFlow: Flow<Settings> = settingsDao.observeSettings()

    suspend fun updateSettings(set: Settings) = withContext(ioDispatcher) {
        settingsDao.update(set)
    }

    suspend fun getSettings(): Settings = withContext(ioDispatcher) {
        when (val set = settingsDao.getSettings()) {
            null -> throw Exception("Settings isn't initialised")
            else -> set
        }
    }

    suspend fun getCountSettings() = withContext(ioDispatcher) {
        settingsDao.getCountSettings()
    }
}