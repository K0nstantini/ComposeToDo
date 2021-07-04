package com.grommade.composetodo.data.entity

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.grommade.composetodo.add_classes.MyCalendar
import com.grommade.composetodo.enums.TypeTask
import kotlinx.parcelize.Parcelize

const val DEFAULT_DEADLINE_SINGLE_TASK = 24

@Parcelize
@Entity(tableName = "task_table")
data class Task(

    /** General */
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val name: String = "",
    val group: Boolean = false,
    val groupOpen: Boolean = false,
    val parent: Long = 0L,
    val type: TypeTask = TypeTask.SINGLE_TASK,
    val dateCreation: MyCalendar = MyCalendar.now(),

    @Embedded
    val regular: RegularTask = RegularTask(),
    @Embedded
    val single: SingleTask = SingleTask(),

    ) : Parcelable {

    /** Regular task */
    @Parcelize
    data class RegularTask(
        val frequencyFrom: Int = 0,
        val frequencyTo: Int = 0,
        val timeFrom: Int = 0,                                   // Время, когда задача может стартовать
        val timeTo: Int = 0,
        val periodGeneration: Int = 0,                           // Период генерации задач в днях, например 0-3 задач за 2 дня
        val workingTime: String = "",                            // Дни, когда будет активироваться задача (например только в будни или 2 через 2 дня)
        val chooseFromGroup: Boolean = false,                    // Задачи будут рандомно выбираться из всей группы
        val dateActivated: Long = 0L,                            // Дата активации задачи
        val finishDate: Long = 0L,                               // Задача работает до этой даты
    ) : Parcelable

    /** Single task */
    @Parcelize
    data class SingleTask(
        val dateActivation: MyCalendar = MyCalendar(),           // Дата активации задачи
        val dateStart: MyCalendar = MyCalendar.today(),          // Дата, начиная с которой, задача становиться активной
        val dateUntilToDo: MyCalendar = MyCalendar(),            // Задача должна быть сгенерирована до этой даты
        val deadline: Int = DEFAULT_DEADLINE_SINGLE_TASK,
        val toDoAfterTask: Long = 0L,                            // Задача будет сегенрирована только после выполнения другой задачи
        val rolls: Int = 0,                                      // количество замен задачи
    ) : Parcelable

    val isNew: Boolean
        get() = (id == 0L)

    val singleReadyToActivate: Boolean
        get() = !group && single.dateActivation.isEmpty() && single.dateStart < MyCalendar.now()

    val singleIsActivated: Boolean
        get() = single.dateActivation.isNoEmpty()

    fun canRoll(settings: Settings) = single.rolls < settings.singleTask.numberPossibleRolls

    /** General */
    fun getLevel(tasks: List<Task>) = generateSequence(this) { task ->
        tasks.find { it.id == task.parent }
    }.count() - 1

    fun getDifferent(other: Task): StringBuilder {
        val changes = StringBuilder()
        Task::class.java.declaredFields.forEach { field ->
            val oldValue = field.get(this)
            val newValue = field.get(other)
            if (oldValue != newValue)
                changes.append("Task '$name' changed '${field.name}' from '$oldValue' to '$newValue'")
        }
        return changes
    }

}