package com.grommade.composetodo.util

import android.app.Application
import com.grommade.composetodo.db.entity.Task

fun String.addArgument(argument: String) = "$this/{$argument}"

fun Int.toStrTime(): String {
    return (this / 60).toString().padStart(2, '0') + ':' +
            (this % 60).toString().padStart(2, '0')
}

fun Int.toArray(app: Application): Array<String> = app.resources.getStringArray(this)

fun Int.hoursToMilli(): Long = this.toLong() * MINUTES_IN_HOUR * SECONDS_IN_MINUTE * MILLI_IN_SECOND

//FIXME: Не чистая ф-я?
fun List<Task>.nestedTasks(
    task: Task,
    list: MutableList<Task> = mutableListOf()
): List<Task> {
    list.add(task)
    this.filter { it.parent == task.id }.forEach { this.nestedTasks(it, list) }
    return list
}

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
