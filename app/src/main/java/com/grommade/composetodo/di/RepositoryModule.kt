package com.grommade.composetodo.di

import com.grommade.composetodo.data.dao.HistoryDao
import com.grommade.composetodo.data.dao.SettingsDao
import com.grommade.composetodo.data.dao.SingleTaskDao
import com.grommade.composetodo.data.repos.RepoHistory
import com.grommade.composetodo.data.repos.RepoSettings
import com.grommade.composetodo.data.repos.RepoTask
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

    @Provides
    @Singleton
    fun provideRepositorySettings(settingsDao: SettingsDao) = RepoSettings(settingsDao)

    @Provides
    @Singleton
    fun provideRepositorySingleTask(singleTaskDao: SingleTaskDao) = RepoTask(singleTaskDao)

    @Provides
    @Singleton
    fun provideRepositoryHistory(historyDao: HistoryDao) = RepoHistory(historyDao)
}