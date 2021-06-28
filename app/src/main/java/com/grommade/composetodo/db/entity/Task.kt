package com.grommade.composetodo.db.entity

import android.os.Parcelable
import androidx.lifecycle.LiveData
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
    var name: String = "",
    var group: Boolean = false,
    var groupOpen: Boolean = false,
    var parent: Long = 0L,
    var type: TypeTask = TypeTask.SINGLE_TASK,
    val dateCreation: MyCalendar = MyCalendar.now(),

    @Embedded
    val regular: RegularTask = RegularTask(),
    @Embedded
    val single: SingleTask = SingleTask(),

    ) : Parcelable {

    /** Regular task */
    @Parcelize
    data class RegularTask(
        var frequencyFrom: Int = 0,
        var frequencyTo: Int = 0,
        var timeFrom: Int = 0,                                   // Время, когда задача может стартовать
        var timeTo: Int = 0,
        var periodGeneration: Int = 0,                           // Период генерации задач в днях, например 0-3 задач за 2 дня
        var workingTime: String = "",                            // Дни, когда будет активироваться задача (например только в будни или 2 через 2 дня)
        var chooseFromGroup: Boolean = false,                    // Задачи будут рандомно выбираться из всей группы
        var dateActivated: Long = 0L,                            // Дата активации задачи
        var finishDate: Long = 0L,                               // Задача работает до этой даты
    ) : Parcelable

    /** Single task */
    @Parcelize
    data class SingleTask(
        var dateActivation: MyCalendar = MyCalendar(),           // Дата активации задачи
        var dateStart: MyCalendar = MyCalendar.today(),          // Дата, начиная с которой, задача становиться активной
        var dateUntilToDo: MyCalendar = MyCalendar(),            // Задача должна быть сгенерирована до этой даты
        var deadline: Int = DEFAULT_DEADLINE_SINGLE_TASK,
        var toDoAfterTask: Long = 0L,                            // Задача будет сегенрирована только после выполнения другой задачи
        var rolls: Int = 0,                                      // количество замен задачи
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

    fun setName(_name: LiveData<String>) = this.apply { _name.value?.let { name = it } }
    fun setGroup(_group: LiveData<Boolean>) = this.apply { _group.value?.let { group = it } }
    fun setParent(_parent: LiveData<Long>) = this.apply { _parent.value?.let { parent = it } }

    /** Regular task */
    fun setFromGroup(_choose: LiveData<Boolean>) = this.apply { _choose.value?.let { regular.chooseFromGroup = it } }

    /** Single task */
    private fun setDateStart(date: LiveData<MyCalendar>) = date.value?.let { single.dateStart = it }
    private fun setDeadline(_deadline: LiveData<Int>) = _deadline.value?.let { single.deadline = it }

    fun setDataSingleTask(
        _name: LiveData<String>,
        _group: LiveData<Boolean>,
        _parent: LiveData<Long>,
        _dateStart: LiveData<MyCalendar>,
        _deadline: LiveData<Int>
    ) {
        setName(_name)
        setGroup(_group)
        setParent(_parent)

        if (!group) {
            setDateStart(_dateStart)
            setDeadline(_deadline)
        }
    }

}