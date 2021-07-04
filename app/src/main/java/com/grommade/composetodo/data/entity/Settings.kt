package com.grommade.composetodo.data.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.grommade.composetodo.add_classes.MyCalendar
import com.grommade.composetodo.enums.ModeGenerationSingleTasks
import com.grommade.composetodo.util.timeToMinutes

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
        @ColumnInfo(name = "regular_points") val points: Int = 0,                       // баллы за выполнение/невыполнение задач
    ) {

    }

    /**
     * @param active активация режима разовых задач
     * @param startGeneration дата начала генерации задач
     * @param lastGeneration время, когда задача запланирована
     * @param showDateNextTask показывать время следующей запланированной задачи
     * @param modeGeneration режим генерации задач (фиксированный - в определенное время или внутри определенного интервала,
     * рандомный - генерацйия задач, основываясь на частоте генерации)
     * @param periodFrom интервал (от) времени в котором будут генерироваться задачи
     * @param periodTo интервал (до) времени в котором будут генерироваться задачи
     * @param daysOfWeek дни недели, в которые будут генерироваться задачи
     * @param everyFewDays генерация задач раз в 1/2/3 и т. д. дня
     * @param countGeneratedTasksAtATime количество генерируемых задач за раз (только для фиксированного режима генерации)
     * @param frequencyFrom частота генерации задач в часах от
     * @param frequencyTo частота генерации задач в часах до
     *
     *
     * */
    data class SettingsSingleTask(
        @ColumnInfo(name = "single_active") val active: Boolean = false,
        val startGeneration: MyCalendar = MyCalendar(),
        val lastGeneration: MyCalendar = MyCalendar(),
        val showDateNextTask: Boolean = false,

        /** Frequency */
        val modeGeneration: ModeGenerationSingleTasks = ModeGenerationSingleTasks.RANDOM,
        val periodFrom: Int = "00:00".timeToMinutes(),
        val periodTo: Int = "23:59".timeToMinutes(),
        val daysOfWeek: String = "",
        val everyFewDays: Int = 1,
        val countGeneratedTasksAtATime: Int = 1,
        val frequencyFrom: Int = FREQUENCY_GENERATE_FROM,
        val frequencyTo: Int = FREQUENCY_GENERATE_TO,

        /** Restrictions */

        val restrictionOnChange: Boolean = false,                                                                       // ограничение на изменение настроек при активации режима разовых задач
        val ActivityPeriod: MyCalendar = MyCalendar.today()
            .addHours(ACTIVITY_PERIOD),                                  // период активности режима, до этой даты нельзя отключать режим и менять настройки
        val timeAfterActivationToChangeGeneralSettings: Int = TIME_AFTER_ACTIVATION_TO_CHANGE_GENERAL_SETTINGS,         // период времени после активации режима, когда еще можно изменить общие настройки
        val timeAfterAddingTaskToEditOrDel: Int = TIME_AFTER_ADDING_TASK_TO_EDIT_OR_DEL,                                // период времени после добавления задачи, когда еще можно изменить или удалить ее

        /** Rewards */

        val rewards: Boolean = true,                                                                                    // включение системы наказаний/вознаграждений
        @ColumnInfo(name = "single_points") val points: Int = 0,                                                        // баллы за выполнение/невыполнение задач
        val pointsForTask: Int = POINTS_FOR_TASK,                                                                       // баллы за выполнение запланированной задачи
        val pointsForOutOfOrderTask: Int = POINTS_FOR_OUT_OF_ORDER_TASK,                                                // баллы за внеочередное выполнение задачи
        val daysToConsiderTaskOld: Int = DAYS_TO_CONSIDER_TASK_OLD,                                                     // количество дней жизни задачи чтобы считать ее пригодной для внеочередного выполнения (заработать баллы)
        val pointsForRoll: Int = POINTS_FOR_ROLL,                                                                       // баллы за рандомную замену текущей задачи
        val numberPossibleRolls: Int = NUMBER_POSSIBLE_ROLLS,                                                           // сколько раз можно заменять одну задачу
        val currentTaskTakePartInRoll: Boolean = true,                                                                  // текущая задача учавствует при генерации новой задачи (т. е. замена может не сработать)
        val postponeCurrentTaskForOnePoint: Int = POSTPONE_CURRENT_TASK_FOR_ONE_POINT,                                  // количество часов, на которые можно отложить текущую задачу за 1 балл
        val postponeNextTaskForOnePoint: Int = POSTPONE_NEXT_TASK_FOR_ONE_POINT,                                        // количество часов, на которые можно отложить следующую задачу за 1 балл
    ) {

        val canRoll: Boolean
            get() = points >= pointsForRoll

        val restrictionIsActive: Boolean
            get() = active && restrictionOnChange

        val periodNoRestriction: Boolean
            get() = (periodTo - periodFrom) == "23:59".timeToMinutes()

        val daysOfWeekNoRestriction: Boolean
            get() = daysOfWeek.isEmpty()

        companion object {
            const val FREQUENCY_GENERATE_FROM = 1
            const val FREQUENCY_GENERATE_TO = 96
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