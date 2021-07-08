package com.grommade.composetodo.use_cases

import com.grommade.composetodo.add_classes.MyCalendar
import com.grommade.composetodo.data.entity.History
import com.grommade.composetodo.data.entity.Settings
import com.grommade.composetodo.data.entity.Task
import com.grommade.composetodo.data.repos.RepoHistory
import com.grommade.composetodo.data.repos.RepoSettings
import com.grommade.composetodo.data.repos.RepoSingleTask
import com.grommade.composetodo.enums.ModeGenerationSingleTasks
import com.grommade.composetodo.util.extensions.delEmptyGroups
import com.grommade.composetodo.util.extensions.hoursToMilli
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.coroutineContext

interface GenerateSingleTasks {
    suspend operator fun invoke()
}

class GenerateSingleTasksImpl @Inject constructor(
    private val repoSettings: RepoSettings,
    private val repoSingleTask: RepoSingleTask,
    private val repoHistory: RepoHistory,
) : GenerateSingleTasks {

    override suspend fun invoke() {
        val settings = repoSettings.getSettings()
        val dateNow = MyCalendar.now()

        if (needToGenerateTask(settings.singleTask, dateNow) &&
            settings.singleTask.modeGeneration == ModeGenerationSingleTasks.RANDOM
        ) {
            val tasks = repoSingleTask.getNoActivateTasks()
            val scope = CoroutineScope(coroutineContext)

            generateRandomTasks(
                tasks = tasks,
                settings = settings,
                lastGeneration = settings.singleTask.lastGeneration,
                dateNow = dateNow,
                saveTask = { task -> scope.launch { task.save() } },
                updateSettings = { set -> scope.launch { set.save() } },
                saveHistory = { history -> scope.launch { history.save() } }
            )
        }
    }

    private fun needToGenerateTask(set: Settings.SettingsSingleTask, dateNow: MyCalendar): Boolean =
        set.active && set.lastGeneration < dateNow && set.startGeneration < dateNow

    private fun generateRandomTasks(
        tasks: List<Task>,
        settings: Settings,
        lastGeneration: MyCalendar,
        dateNow: MyCalendar,
        saveTask: (Task) -> Unit,
        updateSettings: (Settings) -> Unit,
        saveHistory: (History) -> Unit
    ) {
        val frequency = with(settings.singleTask) { frequencyFrom..frequencyTo }
        val period = with(settings.singleTask) { periodFrom..periodTo }
        val tasksToActivate = tasks.filter { it.group || it.singleReadyToActivate }.delEmptyGroups()

        if (lastGeneration.isNoEmpty()) {
            val workDate = lastGeneration.nextSuitableDate(period, settings.singleTask.daysOfWeek, dateNow)
            generateTask(tasksToActivate)?.let { task ->
                saveTask(
                    task.copy(single = task.single.copy(dateActivation = workDate))
                )
                saveHistory(
                    History(
                        date = MyCalendar.now(),
                        value = "'${task.name}' activated. Date activation: $workDate"
                    )
                )
            }
        }

        val lastDate = with(settings.singleTask) { if (lastGeneration.isEmpty()) startGeneration else lastGeneration }
        val newDate = generateDate(frequency, lastDate)

        saveHistory(
            History(
                date = MyCalendar.now(),
                value = "Generated new activated date: $newDate"
            )
        )

        if (newDate < dateNow) {
            generateRandomTasks(tasksToActivate, settings, newDate, dateNow, saveTask, updateSettings, saveHistory)
        } else {
            updateSettings(
                settings.copy(singleTask = settings.singleTask.copy(lastGeneration = newDate))
            )
        }
    }

    // FIXME:
//    private fun generateDate(frequency: IntRange, date: MyCalendar) = MyCalendar(
//        date.milli + (60_000..180_000).random()
//    )

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

    /** Ищем подходящую дату с условием разл. ограничений по времени и дням */
    private fun MyCalendar.nextSuitableDate(
        period: IntRange,
        daysOfWeek: String,
        dateNow: MyCalendar
    ): MyCalendar {
        if (!dateInWorkDays(daysOfWeek)) {
            return nearestRandomWorkDay(daysOfWeek).addMinutes(period.random())
        }

        val currentDateMinutes = getMinutesOfDay()
        if (currentDateMinutes < period.first) {
            return startDay().addMinutes(period.random())
        }
        if (currentDateMinutes > period.last) {
            val dateNowMinutes = dateNow.getMinutesOfDay()
            if (dateNow.startDay().isEqual(startDay()) && dateNowMinutes < period.last) {
                val periodFromNow = (if (dateNowMinutes < period.first) period.first else dateNowMinutes)..period.last
                return startDay().addMinutes(periodFromNow.random())
            }
            return nextNearestWorkDay(daysOfWeek).addMinutes(period.random())
        }
        return this
    }

    /** Ближайший рабочий день */
    private fun MyCalendar.nextNearestWorkDay(daysOfWeek: String): MyCalendar {
        val nextDay = { i: Int ->
            addDays(i).dateInWorkDays(daysOfWeek)
        }
        val countDays = generateSequence(1) { if (nextDay(it)) null else it + 1 }.count()

        return startDay().addDays(countDays)
    }

    /** Ищем ближайший диапазон рабочих дней и случайно выбираем оттуда день */
    private fun MyCalendar.nearestRandomWorkDay(daysOfWeek: String): MyCalendar {
        if (daysOfWeek.isEmpty() || daysOfWeek == "0,1,2,3,4,5,6") {
            return startDay()
        }
        val startDay = nextNearestWorkDay(daysOfWeek)
        val nextDay = { i: Int ->
            startDay.addDays(i).dateInWorkDays(daysOfWeek)
        }
        val countDays = generateSequence(0) { if (nextDay(it + 1)) it + 1 else null }.count() - 1

        val endDay = startDay.addDays(countDays).endDay()
        return MyCalendar.random(startDay..endDay).startDay()
    }

    /** Дата входит в рабочие дни */
    private fun MyCalendar.dateInWorkDays(daysOfWeek: String): Boolean =
        daysOfWeek.isEmpty() || daysOfWeek.contains(getNumberDayOfWeek().toString())

    private suspend fun Task.save() = repoSingleTask.saveTask(this)
    private suspend fun History.save() = repoHistory.saveHistory(this)
    private suspend fun Settings.save() = repoSettings.updateSettings(this)

}




