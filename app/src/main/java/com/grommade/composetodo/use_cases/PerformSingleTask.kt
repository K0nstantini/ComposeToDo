package com.grommade.composetodo.use_cases

import com.grommade.composetodo.data.entity.RandomTask
import com.grommade.composetodo.data.repos.RepoSettings
import com.grommade.composetodo.data.repos.RepoTask
import javax.inject.Inject

interface PerformSingleTask {
    suspend operator fun invoke(task: RandomTask)
}

class PerformSingleTaskImpl @Inject constructor(
    private val repoSettings: RepoSettings,
    private val repoSingleTask: RepoTask,
) : PerformSingleTask {

    override suspend fun invoke(task: RandomTask) {
        repoSettings.getSettings()
            .addSinglePointsTaskDone(task)
            .also { repoSettings.updateSettings(it) }

        repoSingleTask.deleteTask(task)
    }
}