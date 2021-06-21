package com.grommade.composetodo.di

import android.content.Context
import com.grommade.composetodo.Repository
import com.grommade.composetodo.alarm.AlarmService
import com.grommade.composetodo.db.AppDatabase
import com.grommade.composetodo.db.dao.SettingsDao
import com.grommade.composetodo.db.dao.TaskDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object Modules {

    @Provides
    @Singleton
    fun provideSettingsDao(@ApplicationContext appContext: Context): SettingsDao {
        return AppDatabase.getDatabase(appContext, CoroutineScope(SupervisorJob())).SettingsDao()
    }

    @Provides
    @Singleton
    fun provideTaskDao(@ApplicationContext appContext: Context): TaskDao {
        return AppDatabase.getDatabase(appContext, CoroutineScope(SupervisorJob())).TaskDao()
    }

    @Provides
    @Singleton
    fun provideRepository(settingsDao: SettingsDao, taskDao: TaskDao) = Repository(settingsDao, taskDao)

    @Provides
    @Singleton
    fun provideAlarmService(@ApplicationContext appContext: Context) = AlarmService(appContext)

}