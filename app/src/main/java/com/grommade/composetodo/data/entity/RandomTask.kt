package com.grommade.composetodo.data.entity

import androidx.compose.runtime.Immutable
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.grommade.composetodo.add_classes.MyCalendar
import com.grommade.composetodo.enums.TypeTask
import com.grommade.composetodo.util.extensions.timeToMinutes

const val DEFAULT_DEADLINE_SINGLE_TASK = 24

/**
 * @param active не активированная задача не будет показана в списках на выполнение
 * @param dateActivation дата активации RANDOM type tasks
 * @param dateStart дата, когда задача либо активируется, либо станет доступной для активации
 * @param dateEnd для случайных задач - до этой даты задача должна быть сгенерирована
 * @param notification напоминание (до 24ч)
 * @param duration период в течении которого задача желательно должна быть выполнена
 * @param deadlineInHours время в течении которго задча обязательно должна быть выполнена (в часах)
 * @param deadlineTime время в течении которго задча обязательно должна быть выполнена (до 24ч)
 * @param doAfter задача может быть активироваться на выполнение только после выполнения указанных задач
 * */
@Entity(tableName = "random_task_table")
@Immutable
data class RandomTask(

    /** General */
    @PrimaryKey(autoGenerate = true) override val id: Long = 0L,
    override val name: String = "",
    override val byDefault: Boolean = false,
    val group: Boolean = false,
    val groupOpen: Boolean = false,
    val parent: Long = 0L,
    val type: TypeTask = TypeTask.EXACT_TIME,
    val active: Boolean = false,
    val archived: Boolean = false,

    val dateCreation: MyCalendar = MyCalendar.now(),
    val dateActivation: MyCalendar = MyCalendar(),
    val dateStart: MyCalendar = MyCalendar.today(),
    val dateEnd: MyCalendar = MyCalendar(),

    val notification: Int = 0,
    val duration: Int = 10,
    val deadlineInHours: Int = 24,
    val deadlineTime: Int = "01:00".timeToMinutes(),

    val doAfter: List<Long> = emptyList(),

    @Embedded
    val schedule: ScheduleTask = ScheduleTask(),

//    @Embedded
//    val dates: DatesTask = DatesTask(),

    @Embedded
    val single: SingleTask = SingleTask(),

    ) : SingleTask(), AppEntity {


    /** Single task */
    @Immutable
    data class SingleTask(
        val deadlineDays: Int = DEFAULT_DEADLINE_SINGLE_TASK,
        val rolls: Int = 0,                                      // количество замен задачи
    )

    val deadlineDate: MyCalendar
        get() = dateActivation.addHours(single.deadlineDays)

    val isNew: Boolean
        get() = (id == 0L)

    val singleReadyToActivate: Boolean
        get() = !group && dateActivation.isEmpty() && dateStart < MyCalendar.now()

    val singleIsActivated: Boolean
        get() = dateActivation.isNoEmpty()

    fun canRoll(settings: Settings) = single.rolls < settings.singleTask.numberPossibleRolls

    /** General */
    fun getLevel(tasks: List<RandomTask>) = generateSequence(this) { task ->
        tasks.find { it.id == task.parent }
    }.count() - 1

    fun hierarchicalSort(tasks: List<RandomTask>): String {
        return generateSequence(this) { task ->
            tasks.find { it.id == task.parent }
        }.toList().reversed().joinToString { it.name + it.id }
    }

}