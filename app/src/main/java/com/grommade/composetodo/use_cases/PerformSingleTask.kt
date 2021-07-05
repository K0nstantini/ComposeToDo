package com.grommade.composetodo.use_cases

import com.grommade.composetodo.data.entity.Task
import com.grommade.composetodo.data.repos.RepoSettings
import com.grommade.composetodo.data.repos.RepoSingleTask
import javax.inject.Inject

interface PerformSingleTask {
    suspend operator fun invoke(task: Task)
}

class PerformSingleTaskImpl @Inject constructor(
    private val repoSettings: RepoSettings,
    private val repoSingleTask: RepoSingleTask,
) : PerformSingleTask {

    override suspend fun invoke(task: Task) {
        repoSettings.getSettings()
            .addSinglePointsTaskDone(task)
            .also { repoSettings.updateSettings(it) }

        repoSingleTask.deleteTask(task)
    }
}