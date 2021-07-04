package com.grommade.composetodo.di

import android.content.Context
import com.grommade.composetodo.Repository
import com.grommade.composetodo.alarm.AlarmService
import com.grommade.composetodo.data.dao.HistoryDao
import com.grommade.composetodo.data.dao.SettingsDao
import com.grommade.composetodo.data.dao.TaskDao
import com.grommade.composetodo.use_cases.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object Modules {

    @Provides
    @Singleton
    fun provideRepository(settingsDao: SettingsDao, taskDao: TaskDao, historyDao: HistoryDao) =
        Repository(settingsDao, taskDao, historyDao)

    /** Use cases */

    @Provides
    @Singleton
    fun providePerformSingleTask(repo: Repository, settings: GetSettings): PerformSingleTask =
        PerformSingleTaskImpl(repo, settings)

    @Provides
    @Singleton
    fun provideGetSettings(repo: Repository): GetSettings = GetSettingsImpl(repo)

    @Provides
    @Singleton
    fun provideDeleteTask(
        @ApplicationContext appContext: Context,
        repo: Repository,
        settings: GetSettings
    ): DeleteTask = DeleteTaskImpl(appContext, repo, settings)

    @Provides
    @Singleton
    fun provideUpdateSettings(repo: Repository): UpdateSettings = UpdateSettingsImpl(repo)

    @Provides
    @Singleton
    fun provideGenerateSingleTasks(
        repo: Repository,
        settings: GetSettings
    ): GenerateSingleTasks = GenerateSingleTasksImpl(repo, settings)

}