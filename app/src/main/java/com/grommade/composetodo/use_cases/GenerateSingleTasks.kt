package com.grommade.composetodo.use_cases

import com.grommade.composetodo.Repository
import com.grommade.composetodo.add_classes.MyCalendar
import com.grommade.composetodo.db.entity.Settings
import com.grommade.composetodo.db.entity.Task
import com.grommade.composetodo.enums.ModeGenerationSingleTasks
import com.grommade.composetodo.util.delEmptyGroups
import com.grommade.composetodo.util.hoursToMilli
import kotlinx.coroutines.*
import java.util.*
import javax.inject.Inject

interface GenerateSingleTasks {
    suspend operator fun invoke()
}

class GenerateSingleTasksImpl @Inject constructor(
    private val repo: Repository,
    private val getSettings: GetSettings
) : GenerateSingleTasks {

    override suspend fun invoke() {
        val settings = getSettings()
        val singleSet = settings.singleTask
        val dateNow = MyCalendar.now()

        if (!settings.singleTask.active || singleSet.lastGeneration > dateNow || singleSet.startGeneration > dateNow) {
            return
        }

        val tasks = repo.getReadyToActivateSingleTasks()

        generateRandomTasks(
            tasks = tasks,
            settings = settings,
            lastGeneration = singleSet.lastGeneration,
            dateNow = dateNow,
            frequency = singleSet.frequencyFrom..singleSet.frequencyTo,
        )

        /** ====================================================================================================== */
        /**
        val dates = getDatesToActivateTasks(tasks, singleSet, dateNow)

        val lastDate = dates.last()
        settings.change { set: singleSet -> set.copy(lastGeneration = lastDate) }.update()

        getTasksToUpdateDatesActivation(tasks, dates).update()
         */
    }

    private suspend fun generateRandomTasks(
        tasks: List<Task>,
        settings: Settings,
        lastGeneration: MyCalendar,
        dateNow: MyCalendar,
        frequency: IntRange,
    ) {
        val tasksToActivate = tasks.filter { it.group || it.singleReadyToActivate }.delEmptyGroups()


        if (lastGeneration.isNoEmpty() && lastGeneration.dateInWorkDays(settings.singleTask.daysOfWeek)) {
            val workDate = with(settings.singleTask) { lastGeneration.getWorkTime(periodFrom..periodTo) }
            generateTask(tasksToActivate)
                ?.apply { single.dateActivation = workDate }
                ?.save()
        }

        val newDate = generateDate(frequency, lastGeneration)

        if (newDate < dateNow) {
            generateRandomTasks(tasksToActivate, settings, newDate, dateNow, frequency)
        } else {
            settings.copy(singleTask = settings.singleTask.copy(lastGeneration = newDate)).update()
        }
    }

    private fun generateDate(frequency: IntRange, date: MyCalendar) = MyCalendar(
        date.milli + (frequency.first.hoursToMilli()..frequency.last.hoursToMilli()).random()
    )

    private fun generateTask(tasks: List<Task>, parent: Long = 0L): Task? {
        val task = tasks.filter { it.parent == parent }
            .shuffled()
            .randomOrNull()
        return when {
            task == null -> null
            task.group -> generateTask(tasks, task.id)
            else -> task
        }
    }

    private fun MyCalendar.dateInWorkDays(daysOfWeek: String): Boolean =
        daysOfWeek == "" || daysOfWeek.contains(getNumberDayOfWeek().toString())

    private fun MyCalendar.getWorkTime(period: IntRange): MyCalendar {
        val workTime = { time: Int -> MyCalendar.today().addMinutes(time) }
        val workRange = workTime(period.first)..workTime(period.last)
        return when (this) {
            in workRange -> this
            else -> MyCalendar.random(workRange)
        }
    }

    private suspend fun Task.save() = repo.saveTask(this)
    private suspend fun Settings.update() = repo.updateSettings(this@update)

}

    /**

    private fun getDatesToActivateTasks(
        tasks: List<Task>,
        singleSet: Settings.SettingsSingleTask,
        dateNow: MyCalendar
    ): List<MyCalendar> {
        val startDate = if (singleSet.lastGeneration.isEmpty()) singleSet.startGeneration else singleSet.lastGeneration
        return when (singleSet.modeGeneration) {
            ModeGenerationSingleTasks.RANDOM ->
                getDatesToActivateTasksRandomMode(
                    tasks = tasks,
                    frequency = singleSet.frequencyFrom..singleSet.frequencyTo,
                    lastDateActivation = singleSet.lastGeneration,
                    startDate = startDate,
                    dateNow = dateNow
                )
            ModeGenerationSingleTasks.FIXED -> getDatesToActivateTasksFixedMode(startDate)
        }
    }


    fun getDatesToActivateTasksRandomMode(
        tasks: List<Task>,
        frequency: IntRange,
        lastDateActivation: MyCalendar,
        startDate: MyCalendar,
        dateNow: MyCalendar
    ): List<MyCalendar> {
        val dates = generateDatesRandomMode(frequency, startDate, dateNow)
        return when {
            noTaskLastDateActivation(tasks, lastDateActivation) -> listOf(lastDateActivation) + dates
            else -> dates
        }
    }

    private fun generateDatesRandomMode(
        frequency: IntRange,
        dateFrom: MyCalendar,
        dateTo: MyCalendar
    ): List<MyCalendar> {
        val dateNext = generateDate(frequency, dateFrom)
        return when {
            dateNext < dateTo -> listOf(dateNext) + generateDatesRandomMode(frequency, dateNext, dateTo)
            else -> listOf(dateNext)
        }
    }


    private fun noTaskLastDateActivation(tasks: List<Task>, date: MyCalendar) =
        date.isNoEmpty() && !tasks.any { it.single.dateActivation == date }

    private fun getTasksToUpdateDatesActivation(
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

    */





