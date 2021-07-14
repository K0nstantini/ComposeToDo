package com.grommade.composetodo.data.entity

/**
 * @param byDefault запись используется для задания параметров по умолчанию
 * */
abstract class Task(
) {
    abstract val id: Long
    abstract val name: String
    abstract val byDefault: Boolean
}

abstract class SingleTask(
) : Task() {
}

abstract class RegularTask(
) : Task() {
}

