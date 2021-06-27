package com.grommade.composetodo.db.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.grommade.composetodo.add_classes.MyCalendar

@Entity(tableName = "settings_table")
data class Settings(
    @PrimaryKey val id: Int = 1,

    /** Regular tasks */
    @Embedded
    val regularTask: SettingsRegularTask = SettingsRegularTask(),

    /** Single tasks */
    @Embedded
    val singleTask: SettingsSingleTask = SettingsSingleTask()


) {

    data class SettingsRegularTask(
        @ColumnInfo(name = "regular_points") val points: Int = 0,                       // баллы завыполнение/невыполнение задач
    ) {

    }

    data class SettingsSingleTask(
        @ColumnInfo(name = "single_active") val active: Boolean = false,                                                // активация режима разовых задач
        var frequency: Int = FREQUENCY_GENERATE_S_TASKS,                                                                // частота генерации задач в часах (в среднем равная 1/2 от значения)
        var dateActivation: MyCalendar = MyCalendar(),                                                                  // время, когда задача запланирована

        /** Restrictions */

        val restrictionOnChange: Boolean = false,                                                                       // ограничение на изменение настроек при активации режима разовых задач
        val ActivityPeriod: MyCalendar = MyCalendar.today().addHours(ACTIVITY_PERIOD),                                  // период активности режима, до этой даты нельзя отключать режим и менять настройки
        val timeAfterActivationToChangeGeneralSettings: Int = TIME_AFTER_ACTIVATION_TO_CHANGE_GENERAL_SETTINGS,         // период времени после активации режима, когда еще можно изменить общие настройки
        val timeAfterAddingTaskToEditOrDel: Int = TIME_AFTER_ADDING_TASK_TO_EDIT_OR_DEL,                                // период времени после добавления задачи, когда еще можно изменить или удалить ее

        /** Rewards */

        var rewards: Boolean = true,                                                                                    // включение системы наказаний/вознаграждений
        @ColumnInfo(name = "single_points") val points: Int = 0,                                                        // баллы за выполнение/невыполнение задач
        var pointsForTask: Int = POINTS_FOR_TASK,                                                                       // баллы за выполнение запланированной задачи
        var pointsForOutOfOrderTask: Int = POINTS_FOR_OUT_OF_ORDER_TASK,                                                // баллы за внеочередное выполнение задачи
        var daysToConsiderTaskOld: Int = DAYS_TO_CONSIDER_TASK_OLD,                                                     // количество дней жизни задачи чтобы считать ее пригодной для внеочередного выполнения (заработать баллы)
        var pointsForRoll: Int = POINTS_FOR_ROLL,                                                                       // баллы за рандомную замену текущей задачи
        var numberPossibleRolls: Int = NUMBER_POSSIBLE_ROLLS,                                                           // сколько раз можно заменять одну задачу
        var currentTaskTakePartInRoll: Boolean = true,                                                                  // текущая задача учавствует при генерации новой задачи (т. е. замена может не сработать)
        var postponeCurrentTaskForOnePoint: Int = POSTPONE_CURRENT_TASK_FOR_ONE_POINT,                                  // количество часов, на которые можно отложить текущую задачу за 1 балл
        var postponeNextTaskForOnePoint: Int = POSTPONE_NEXT_TASK_FOR_ONE_POINT,                                        // количество часов, на которые можно отложить следующую задачу за 1 балл
    ) {

        val canRoll: Boolean
            get() = points >= pointsForRoll

        val restrictionIsActive: Boolean
            get() = active && restrictionOnChange

        companion object {
            private const val FREQUENCY_GENERATE_S_TASKS = 96
            private const val ACTIVITY_PERIOD = 30
            private const val TIME_AFTER_ACTIVATION_TO_CHANGE_GENERAL_SETTINGS = 24
            private const val TIME_AFTER_ADDING_TASK_TO_EDIT_OR_DEL = 24
            private const val POINTS_FOR_TASK = 1
            private const val POINTS_FOR_OUT_OF_ORDER_TASK = 3
            private const val DAYS_TO_CONSIDER_TASK_OLD = 7
            private const val POINTS_FOR_ROLL = 3
            private const val NUMBER_POSSIBLE_ROLLS = 1
            private const val POSTPONE_CURRENT_TASK_FOR_ONE_POINT = 2
            private const val POSTPONE_NEXT_TASK_FOR_ONE_POINT = 1
        }

    }

    fun addSinglePointsTaskDone(task: Task) = addSinglePoints(
        when (task.singleIsActivated) {
            true -> singleTask.pointsForTask
            else -> singleTask.pointsForOutOfOrderTask
        }
    )

    private fun addSinglePoints(count: Int) =
        copy(singleTask = singleTask.copy(points = singleTask.points + count))

}