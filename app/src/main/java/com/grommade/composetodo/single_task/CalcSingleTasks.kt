package com.grommade.composetodo.single_task

import com.grommade.composetodo.Repository
import com.grommade.composetodo.add_classes.MyCalendar
import com.grommade.composetodo.db.entity.Settings
import com.grommade.composetodo.db.entity.Task
import com.grommade.composetodo.util.delEmptyGroups
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class CalcSingleTasks(private val repo: Repository) {

    fun refresh(scope: CoroutineScope) = scope.launch {
        val settings = repo.getSettings() ?: return@launch
        val lastDateActivation = settings.singleTask.dateActivation
        val tasks = repo.getReadyToActivateSingleTasks()

        if (needToActivateSingleTasks(tasks, lastDateActivation)) {
            val dates = getDatesToActivateSingleTasks(
                tasks,
                settings.singleTask.frequency,
                lastDateActivation
            )

            val lastDate = dates.last()
            settings.apply { singleTask.dateActivation = lastDate }.update()

            getTasksToUpdateDatesActivation(tasks, dates).update()
        }
    }

    private fun needToActivateSingleTasks(tasks: List<Task>, date: MyCalendar) =
        date < MyCalendar.now() && tasks.any { it.singleReadyToActivate }

    private fun getDatesToActivateSingleTasks(
        tasks: List<Task>,
        frequency: Int,
        lastDateActivation: MyCalendar
    ): List<MyCalendar> {
        val dates = generateDates(frequency, lastDateActivation, MyCalendar.now())
        return when {
            noTaskLastDateActivation(tasks, lastDateActivation) -> listOf(lastDateActivation) + dates
            else -> dates
        }
    }

    private fun generateDates(
        frequency: Int,
        dateFrom: MyCalendar,
        dateTo: MyCalendar
    ): List<MyCalendar> {
        val dateNext = generateDate(frequency, dateFrom)
        return when {
            dateNext < dateTo -> listOf(dateNext) + generateDates(frequency, dateNext, dateTo)
            else -> listOf(dateNext)
        }
    }

    private fun generateDate(frequency: Int, date: MyCalendar) = MyCalendar(
        (if (date.isEmpty()) MyCalendar.now() else date).milli + 60_000L
    )

    private fun noTaskLastDateActivation(tasks: List<Task>, date: MyCalendar) =
        date.isNoEmpty() && !tasks.any { it.single.dateActivation == date }

    fun getTasksToUpdateDatesActivation(
        tasks: List<Task>,
        dates: List<MyCalendar>
    ): List<Task> {
        dates.dropLast(1).forEach { date ->
            val tasksToActivate = tasks.filter { it.group || it.singleReadyToActivate }.delEmptyGroups()
            when (val task = generateTask(tasksToActivate)) {
                null -> return@forEach
                else -> task.single.dateActivation = date
            }
        }
        return tasks.filter { dates.contains(it.single.dateActivation) }
    }


    private fun generateTask(tasks: List<Task>, parent: Long = 0L): Task? {
        val task = tasks.filter { it.parent == parent && it.single.dateActivation.isEmpty() }
            .shuffled()
            .randomOrNull()
        return when {
            task == null -> null
            task.group -> generateTask(tasks, task.id)
            else -> task
        }
    }

    private suspend fun List<Task>.update() = repo.updateTasks(this@update)
    private suspend fun Settings.update() = repo.updateSettings(this@update)

}





