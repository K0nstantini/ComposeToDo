package com.grommade.composetodo.use_cases

import android.content.Context
import com.grommade.composetodo.R
import com.grommade.composetodo.Repository
import com.grommade.composetodo.add_classes.MyCalendar
import com.grommade.composetodo.add_classes.ResultOf
import com.grommade.composetodo.add_classes.doIfFailure
import com.grommade.composetodo.add_classes.doIfSuccess
import com.grommade.composetodo.db.entity.Settings
import com.grommade.composetodo.db.entity.Task
import com.grommade.composetodo.util.groupIsEmpty
import javax.inject.Inject

interface DeleteTask {
    suspend operator fun invoke(task: Task? = null, tasks: List<Task> = emptyList()): ResultOf<Boolean>
}

class DeleteTaskImpl @Inject constructor(
    private val appContext: Context,
    private val repo: Repository,
    private val getSettings: GetSettings
) : DeleteTask {

    override suspend fun invoke(task: Task?, tasks: List<Task>): ResultOf<Boolean> {
        val settings = getSettings()

        val errorsMessage = StringBuilder()

        val tasksToDel = tasks.toMutableList()
        task?.let { tasksToDel.add(it) }

        tasks.filterNot { it.group }.forEach { _task ->
            val result = checkTask(settings, _task)
            result.doIfFailure { message, _ -> errorsMessage.append(message) }
            result.doIfSuccess {
                repo.deleteTask(_task)
                tasksToDel.remove(_task)
            }
        }

        tasksToDel.forEach {
            if (tasksToDel.groupIsEmpty(it)) {
                repo.deleteTask(it)
            }
        }

        return if (errorsMessage.isEmpty()) {
            ResultOf.Success(true)
        } else {
            ResultOf.Failure(errorsMessage.toString())
        }
    }

    private fun checkTask(settings: Settings, task: Task): ResultOf<Task> {
        return when {
            task.singleIsActivated -> ResultOf.Failure(R.string.failure_text_task_is_active.asString(task))
            settings.singleTask.restrictionIsActive &&
                    (MyCalendar.now() - task.dateCreation).hours > settings.singleTask.timeAfterAddingTaskToEditOrDel ->
                ResultOf.Failure(R.string.failure_text_delete_restrict_by_settings.asString(task))
            else -> ResultOf.Success(task)
        }
    }

    private fun Int.asString(task: Task) = appContext.getString(this, task.name)

}