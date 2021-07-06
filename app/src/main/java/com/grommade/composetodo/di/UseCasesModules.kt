package com.grommade.composetodo.di

import com.grommade.composetodo.use_cases.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
abstract class UseCasesModules {

    @Binds
    @Singleton
    abstract fun bindPerformSingleTask(performSingleTaskImpl: PerformSingleTaskImpl): PerformSingleTask

    @Binds
    @Singleton
    abstract fun bindDeleteTask(deleteTaskImpl: DeleteTaskImpl): DeleteTask

    @Binds
    @Singleton
    abstract fun bindGenerateSingleTasks(generateSingleTasksImpl: GenerateSingleTasksImpl): GenerateSingleTasks

    @Binds
    @Singleton
    abstract fun bindPopulateDBWithTasks(populateDBWithTasksImpl: PopulateDBWithTasksImpl): PopulateDBWithTasks

}