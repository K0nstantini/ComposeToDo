package com.grommade.composetodo.util

import android.content.res.Resources
import com.grommade.composetodo.db.entity.Settings
import com.grommade.composetodo.db.entity.Task
import com.grommade.composetodo.enums.DialogDaysOfWeek
import java.time.LocalTime
import java.util.*

typealias singleSet = Settings.SettingsSingleTask

fun String.toListInt(): List<Int> = when (this) {
    "" -> emptyList()
    else -> split(",").map { it.toInt() }
}

fun String.toDaysOfWeek(resources: Resources): String =
    split(",").joinToString(",") { resources.getString(DialogDaysOfWeek.values()[it.toInt()].abbr) }

fun Int.toStrTime(): String {
    return (this / 60).toString().padStart(2, '0') + ':' +
            (this % 60).toString().padStart(2, '0')
}

fun Int.hoursToMilli(): Long = this.toLong() * MINUTES_IN_HOUR * SECONDS_IN_MINUTE * MILLI_IN_SECOND
fun Int.daysToMilli(): Long = this.toLong() * HOURS_IN_DAY * MINUTES_IN_HOUR * SECONDS_IN_MINUTE * MILLI_IN_SECOND

fun Int.minutesToLocalTime(): LocalTime = LocalTime.of(this / MINUTES_IN_HOUR, this % MINUTES_IN_HOUR)
fun LocalTime.toMinutes(): Int = hour * MINUTES_IN_HOUR + minute

//FIXME: Не чистая ф-я?
fun List<Task>.nestedTasks(
    task: Task,
    list: MutableList<Task> = mutableListOf()
): List<Task> {
    list.add(task)
    this.filter { it.parent == task.id }.forEach { this.nestedTasks(it, list) }
    return list
}

fun List<Task>.groupIsEmpty(task: Task): Boolean =
    task.group && nestedTasks(task).count() == 1

//FIXME: Не чистая ф-я?
fun List<Task>.delEmptyGroups(): List<Task> {
    val emptyGroups = mutableListOf<Task>()
    this.filter { it.group }.forEach { task ->
        if (this.nestedTasks(task).all { it.group }) {
            emptyGroups.add(task)
        }
    }
    val noEmptyGroups: MutableList<Task> = this.toMutableList()
    emptyGroups.forEach { task ->
        noEmptyGroups.remove(task)
    }
    return noEmptyGroups
}

fun Settings.change(body: (singleSet) -> singleSet): Settings =
    copy(singleTask = body(singleTask))
