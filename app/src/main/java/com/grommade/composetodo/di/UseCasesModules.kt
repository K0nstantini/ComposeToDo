package com.grommade.composetodo.di

import android.content.Context
import com.grommade.composetodo.data.repos.RepoHistory
import com.grommade.composetodo.data.repos.RepoSettings
import com.grommade.composetodo.data.repos.RepoSingleTask
import com.grommade.composetodo.use_cases.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object UseCasesModules {

    @Provides
    @Singleton
    fun providePerformSingleTask(
        repoSettings: RepoSettings,
        repoSingleTask: RepoSingleTask
    ): PerformSingleTask = PerformSingleTaskImpl(repoSettings, repoSingleTask)


    @Provides
    @Singleton
    fun provideDeleteTask(
        @ApplicationContext appContext: Context,
        repoSettings: RepoSettings,
        repoSingleTask: RepoSingleTask,
    ): DeleteTask = DeleteTaskImpl(appContext, repoSettings, repoSingleTask)


    @Provides
    @Singleton
    fun provideGenerateSingleTasks(
        repoSettings: RepoSettings,
        repoSingleTask: RepoSingleTask,
        repoHistory: RepoHistory,
    ): GenerateSingleTasks = GenerateSingleTasksImpl(repoSettings, repoSingleTask, repoHistory)

}