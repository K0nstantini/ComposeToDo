package com.grommade.composetodo.use_cases

import com.grommade.composetodo.Repository
import com.grommade.composetodo.data.entity.Task
import javax.inject.Inject

interface PerformSingleTask {
    suspend operator fun invoke(task: Task)
}

class PerformSingleTaskImpl @Inject constructor(
    private val repo: Repository,
    private val getSettings: GetSettings
) : PerformSingleTask {

    override suspend fun invoke(task: Task) {
        getSettings().addSinglePointsTaskDone(task).also { repo.updateSettings(it) }
        repo.deleteTask(task)
    }
}