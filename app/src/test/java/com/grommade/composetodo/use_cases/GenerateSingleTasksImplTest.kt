package com.grommade.composetodo.use_cases

import com.grommade.composetodo.add_classes.MyCalendar
import com.grommade.composetodo.data.entity.Settings
import com.grommade.composetodo.data.entity.Task
import com.grommade.composetodo.util.delEmptyGroups
import com.grommade.composetodo.util.hoursToMilli
import com.grommade.composetodo.util.timeToMinutes
import com.grommade.composetodo.util.toStrTime
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.*

class GenerateSingleTasksImplTest {

    /** ===================================== generateRandomTasks ================================================== */

    @Test
    fun generateRandomTasks_isCorrect() {
        var result = true
        val dateNow = MyCalendar.now()
        for (i in 1..100) {
            println("--------------------------------------------------------------------------------------")
            val startGeneration = getRandomDate(-20, -1)
            val lastGeneration = startGeneration.addDays((0..5).random()).addHours((0..23).random())
            val tasks = getTasks()
            val period = getRandomTimePeriod()
            val settings = Settings(
                singleTask = Settings.SettingsSingleTask(
                    daysOfWeek = getRandomDaysOfWeek(),
                    periodFrom = period.first,
                    periodTo = period.last,
                    startGeneration = startGeneration,
                    lastGeneration = lastGeneration
                )
            )

            generateRandomTasks(
                tasks = tasks,
                settings = settings,
                lastGeneration = lastGeneration,
                dateNow = dateNow
            )

            with(settings.singleTask) {
                tasks.filter { it.singleIsActivated }.forEach { task ->
                    if (!task.single.dateActivation.dateInWorkDays(daysOfWeek) ||
                        task.single.dateActivation.getMinutesOfDay() !in periodFrom..periodTo
                    ) {
                        result = false
                        return@forEach
                    }
                }
            }
            if (!result) {
                break
            }
        }

        assertEquals(true, result)
    }

    /** Testing */
    private fun generateRandomTasks(
        tasks: List<Task>,
        settings: Settings,
        lastGeneration: MyCalendar,
        dateNow: MyCalendar
    ) {
        val frequency = with(settings.singleTask) { frequencyFrom..frequencyTo }
        val period = with(settings.singleTask) { periodFrom..periodTo }
        val tasksToActivate = tasks.filter { it.group || it.singleReadyToActivate }.delEmptyGroups()

        if (lastGeneration.isNoEmpty()) {
            val workDate = lastGeneration.nextSuitableDate(period, settings.singleTask.daysOfWeek, dateNow)
            generateTask(tasksToActivate)
                ?.apply { single.copy(dateActivation = workDate) }
                ?.save()
            println("Generation date: $lastGeneration, Task date: $workDate")
        }

        val lastDate = with(settings.singleTask) { if (lastGeneration.isEmpty()) startGeneration else lastGeneration }
        val newDate = generateDate(frequency, lastDate)
//        println("New date: $newDate")
        if (newDate < dateNow) {
            generateRandomTasks(tasksToActivate, settings, newDate, dateNow)
        } else {
            settings.copy(singleTask = settings.singleTask.copy(lastGeneration = newDate)).update()
        }
    }

    private fun Settings.update() {
//        println("Settings, last generation: ${singleTask.lastGeneration}")
    }

    private fun Task.save() {
//        println("Activated: $name, date: ${single.dateActivation}")
    }

    /** ===================================== nextSuitableDate ===================================================== */

    @Test
    fun nextSuitableDate_isCorrect() {
        var result = true
        val dateNow = MyCalendar.now()
        for (i in 1..10000) {
            val daysOfWeek = getRandomDaysOfWeek()
            val currentDate = MyCalendar.now().addHours(1)
            val timePeriod = getRandomTimePeriod()
            val newDate = currentDate.nextSuitableDate(timePeriod, daysOfWeek, dateNow)

            if (!newDate.dateInWorkDays(daysOfWeek) || newDate.getMinutesOfDay() !in timePeriod) {
                result = false
                println("$currentDate (${timePeriod.toTimeStr()}) -  $daysOfWeek - $newDate")
                break
            }
            println("$currentDate (${timePeriod.toTimeStr()}) -  $daysOfWeek - $newDate")
        }

        assertEquals(true, result)
    }

    private fun IntRange.toTimeStr() = first.toStrTime() + "-" + last.toStrTime()

    /** Testing */
    private fun MyCalendar.nextSuitableDate(
        period: IntRange,
        daysOfWeek: String,
        dateNow: MyCalendar
    ): MyCalendar {
        if (!dateInWorkDays(daysOfWeek)) {
            val date = nearestRandomWorkDay(daysOfWeek).addMinutes(period.random())
//            println("Nearest Random: $this (${period.toTimeStr()}) -  $daysOfWeek - $date")
            return date
        }

        val currentDateMinutes = getMinutesOfDay()
//        if (currentDateMinutes in period) {
//            println("SAME: $this (${period.toTimeStr()}) -  $daysOfWeek - $this")
//            return this
//        }

        if (currentDateMinutes < period.first) {
            val date = startDay().addMinutes(period.random())
//            println("This day before work time: $this (${period.toTimeStr()}) -  $daysOfWeek - $date")
            return date
        }
        if (currentDateMinutes > period.last) {
            val dateNowMinutes = dateNow.getMinutesOfDay()
            if (dateNow.startDay().isEqual(startDay()) && dateNowMinutes < period.last) {
                val periodFromNow = (if (dateNowMinutes < period.first) period.first else dateNowMinutes)..period.last
                val date = startDay().addMinutes(periodFromNow.random())
//                println("This day after work time, but before now: $this (${period.toTimeStr()}) -  $daysOfWeek - $date")
                return date
            }
            val date = nextNearestWorkDay(daysOfWeek).addMinutes(period.random())
//            println("Nearest next day: $this (${period.toTimeStr()}) -  $daysOfWeek - $date")
            return date
        }
//        println("SAME: $this (${period.toTimeStr()}) -  $daysOfWeek - $this")
        return this
    }

    /** ============================== getWorkTime [DEPRECATED} ================================================ */

    @Test
    fun getWorkTime_isCorrect() {
        var result = true
        for (i in 1..100) {
            val workTime = getRandomTimePeriod()
            val currentDate = getRandomDate()
            val workDate = currentDate.getWorkTime(workTime)

            val workTimeStr = workTime.first.toStrTime() + "-" + workTime.last.toStrTime()

            if (workDate != null &&
                (workDate.getMinutesOfDay() !in workTime ||
                        (currentDate.getMinutesOfDay() in workTime && currentDate != workDate) ||
                        currentDate.toString(false) != workDate.toString(false)) ||
                workDate == null && currentDate.getMinutesOfDay() < workTime.first
            ) {
                result = false
                println("($workTimeStr) $currentDate - $workDate")
                break
            }
            println("($workTimeStr) $currentDate - $workDate")
        }

        assertEquals(true, result)
    }


    private fun getRandomTimePeriod(): IntRange {
        val time1 = ("00:00".timeToMinutes().."23:59".timeToMinutes()).random()
        val time2 = (time1.."23:59".timeToMinutes()).random()
        return time1..time2
    }

    /** Testing */
    private fun MyCalendar.getWorkTime(period: IntRange): MyCalendar? =
        when {
            this.getMinutesOfDay() > period.last -> null
            getMinutesOfDay() in period -> this
            else -> startDay().addMinutes(period.random())
        }

    /** ===================================== nearestRandomWorkDay ================================================= */

    @Test
    fun nearestRandomWorkDay_isCorrect() {
        var result = true
        for (i in 1..100) {
            val daysOfWeek = getRandomDaysOfWeek()
            val currentDate = getRandomDate()
            val newDate = currentDate.nearestRandomWorkDay(daysOfWeek)

            if (!newDate.dateInWorkDays(daysOfWeek)) {
                println("$currentDate - $daysOfWeek - $newDate")
                result = false
                break
            }
            println("$currentDate - $daysOfWeek - $newDate")
        }

        assertEquals(true, result)
    }

    /** Testing */
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

    /** ===================================== nextNearestWorkDay =================================================== */

    @Test
    fun nextNearestWorkDay_isCorrect() {
        var result = true
        label@ for (i in 1..100) {
            val daysOfWeek = getRandomDaysOfWeek()
            val currentDate = getRandomDate()
            val newDate = currentDate.nextNearestWorkDay(daysOfWeek)

            for (j in 1..10) {
                val date = currentDate.addDays(j).startDay()
                if (date.isEqual(newDate)) {
                    break
                }
                if (date.dateInWorkDays(daysOfWeek)) {
                    result = false
                    break@label
                }
            }
            println("$currentDate - $daysOfWeek - $newDate")
        }
        assertEquals(true, result)
    }

    private fun getRandomDaysOfWeek(): String {
        val days = mutableListOf(0, 1, 2, 3, 4, 5, 6)
        val daysOfWeek = mutableListOf<Int>()
        val countDays = (0..7).random()
        repeat(countDays) {
            val day = days.random()
            daysOfWeek.add(day)
            days.remove(day)
        }
        return daysOfWeek.sortedBy { it }.joinToString(",")
    }

    /** Testing */
    private fun MyCalendar.nextNearestWorkDay(daysOfWeek: String): MyCalendar {
        val nextDay = { i: Int ->
            addDays(i).dateInWorkDays(daysOfWeek)
        }
        val countDays = generateSequence(1) { if (nextDay(it)) null else it + 1 }.count()

        return startDay().addDays(countDays)
    }

    /** ===================================== dateInWorkDays ======================================================= */

    @Test
    fun dateInWorkDays_isCorrect() {
        var result = true
        repeat(100) {
            val randomDate = getRandomDate()
            val daysOfWeek = getRandomDaysOfWeek()

            val daysOfWeekNormal = when (daysOfWeek) {
                "" -> daysOfWeek
                else -> daysOfWeek.split(",").joinToString(",") { it.toInt().toDayOfWeek() }
            }
            val dayOfWeekToTest = randomDate.getNumberDayOfWeek().toDayOfWeek()
            val dateNormal = randomDate.toString(false) + " ($dayOfWeekToTest)"
            val dateInWorkDays = randomDate.dateInWorkDays(daysOfWeek)

            val res = {
                result = false
                println("$dateNormal - $daysOfWeekNormal - $dateInWorkDays")
            }
            if (daysOfWeekNormal.contains(dayOfWeekToTest)) {
                if (!dateInWorkDays) {
                    res()
                    return@repeat
                }
            } else {
                if (daysOfWeekNormal.isNotEmpty() && dateInWorkDays) {
                    res()
                    return@repeat
                }
            }
        }

        assertEquals(true, result)
    }

    private fun Int.toDayOfWeek() = when (this) {
        0 -> "Пн"
        1 -> "Вт"
        2 -> "Ср"
        3 -> "Чт"
        4 -> "Пт"
        5 -> "Сб"
        else -> "Вс"
    }

    private fun getRandomDate(from: Int = 0, to: Int = 30): MyCalendar {
        val range = MyCalendar.now().addDays(from).milli..MyCalendar.now().addDays(to).milli
        return MyCalendar(range.random())
    }

    /** Testing */
    private fun MyCalendar.dateInWorkDays(daysOfWeek: String): Boolean =
        daysOfWeek.isEmpty() || daysOfWeek.contains(getNumberDayOfWeek().toString())

    /** ===================================== generateTask ======================================================= */

    @Test
    fun generateTask_isCorrect() {

        val tasks = getTasks().toMutableList()

        while (true) {
            val task = generateTask(tasks.delEmptyGroups())
            if (task == null) {
                break
            } else {
                println("task = " + task.name)
                tasks.remove(task)
            }
        }
        assertEquals(true, true)
    }

    private fun getTasks(): List<Task> {
        val tasks = mutableListOf<Task>()
        for (i in 1L..10L) {
            tasks.add(Task(id = i, name = "Group-1L", group = true))
            for (j in 1L..10L) {
                tasks.add(Task(id = i * 10 + j, name = "Group-2L", parent = i, group = true))
                for (k in 1L..10L) {
                    tasks.add(Task(id = i * 100 + j * 10 + k, name = "$i - $j ($k)", parent = j))
                }
            }
        }
        return tasks
    }

    /** Testing */
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

    /** ===================================== GenerateDate ======================================================= */


    @Test
    fun generateDate_isCorrect() {
        var result = true
        val dates = mutableListOf<Pair<IntRange, MyCalendar>>()
        repeat(100) {
            val frequency = getRandomFrequency()
            val date = MyCalendar.random(MyCalendar.now()..MyCalendar.today().addDays(1))
            dates.add(frequency to generateDate(frequency, date))
            dates.forEach {
                val date1 = it.second.addHours(it.first.first)
                val date2 = it.second.addHours(it.first.last)
                val range = date1..date2
                if (date1 > date2 || date1 !in range || date2 !in range) {
                    result = false
                    return@repeat
                }
            }
        }
        assertEquals(true, result)
    }

    private fun getRandomFrequency(): IntRange {
        val time1 = (0..24).random()
        val time2 = (time1..48).random()
        return time1..time2
    }

    /** Testing */
    private fun generateDate(frequency: IntRange, date: MyCalendar) = MyCalendar(
        date.milli + (frequency.first.hoursToMilli()..frequency.last.hoursToMilli()).random()
    )
}