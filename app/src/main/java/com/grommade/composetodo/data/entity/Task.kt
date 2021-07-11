package com.grommade.composetodo.data.entity

import androidx.compose.runtime.Immutable
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.grommade.composetodo.add_classes.MyCalendar
import com.grommade.composetodo.enums.TypeTask

const val DEFAULT_DEADLINE_SINGLE_TASK = 24

@Entity(tableName = "single_task_table")
@Immutable
data class Task(

    /** General */
    @PrimaryKey(autoGenerate = true) override val id: Long = 0L,
    val name: String = "",
    val group: Boolean = false,
    val groupOpen: Boolean = false,
    val parent: Long = 0L,
    val type: TypeTask = TypeTask.IMPORTANT,
    val active: Boolean = false,
    val archived: Boolean = false,

    @Embedded
    val schedule: ScheduleTask = ScheduleTask(),

    @Embedded
    val dates: DatesTask = DatesTask(),

    /**
     * Single:
     *  - Exact Time
     *  - Unimportant
     *
     *  Regular:
     *  - Long
     *  - Short
     *  - Container
     *
     * */

    @Embedded
    val single: SingleTask = SingleTask(),

    ) : AppEntity {


    /** Single task */
    @Immutable
    data class SingleTask(
        val deadlineDays: Int = DEFAULT_DEADLINE_SINGLE_TASK,
        val toDoAfterTask: Long = 0L,                            // Задача будет сегенрирована только после выполнения другой задачи
        val rolls: Int = 0,                                      // количество замен задачи
    )

    val deadlineDate: MyCalendar
        get() = dates.dateActivation.addHours(single.deadlineDays)

    val isNew: Boolean
        get() = (id == 0L)

    val singleReadyToActivate: Boolean
        get() = !group && dates.dateActivation.isEmpty() && dates.dateStart < MyCalendar.now()

    val singleIsActivated: Boolean
        get() = dates.dateActivation.isNoEmpty()

    fun canRoll(settings: Settings) = single.rolls < settings.singleTask.numberPossibleRolls

    /** General */
    fun getLevel(tasks: List<Task>) = generateSequence(this) { task ->
        tasks.find { it.id == task.parent }
    }.count() - 1

    fun hierarchicalSort(tasks: List<Task>): String {
        return generateSequence(this) { task ->
            tasks.find { it.id == task.parent }
        }.toList().reversed().joinToString { it.name + it.id }
    }

}