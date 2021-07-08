package com.grommade.composetodo.ui_settings.single_task.time_and_frequency

import androidx.lifecycle.viewModelScope
import com.grommade.composetodo.add_classes.BaseViewModel
import com.grommade.composetodo.data.entity.Settings
import com.grommade.composetodo.data.repos.RepoSettings
import com.grommade.composetodo.enums.ModeGenerationSingleTasks
import com.grommade.composetodo.util.extensions.change
import com.grommade.composetodo.util.extensions.singleSet
import com.grommade.composetodo.util.extensions.timeToMinutes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsSingleTaskFrequencyViewModel @Inject constructor(
    private val repoSettings: RepoSettings,
) : BaseViewModel() {

    private val pendingActions = MutableSharedFlow<SetSTaskFreqActions>()

    private val settings = repoSettings.settingsFlow.asState(Settings())

    val settingsState = settings.map { set ->
        set.singleTask
    }

    init {
        viewModelScope.launch {
            pendingActions.collect { action ->
                when (action) {
                    is SetSTaskFreqActions.Mode -> saveMode(action.value)
                    is SetSTaskFreqActions.PeriodFrom -> savePeriodFrom(action.value)
                    is SetSTaskFreqActions.PeriodTo -> savePeriodTo(action.value)
                    is SetSTaskFreqActions.EveryFewDays -> saveEveryFewDays(action.value)
                    is SetSTaskFreqActions.DaysOfWeek -> saveDaysOfWeek(action.value)
                    is SetSTaskFreqActions.CountTasks -> saveCountTasks(action.value)
                    is SetSTaskFreqActions.Frequency -> saveFrequency(action.from, action.to)
                    SetSTaskFreqActions.PeriodNoRestrictions -> savePeriodNoRestrictions()
                    SetSTaskFreqActions.DaysNoRestriction -> saveDaysNoRestriction()
                    else -> {
                    }
                }
            }
        }
    }

    private fun saveMode(index: Int) {
        val mode = ModeGenerationSingleTasks.values()[index]
        changeSettings { set: singleSet -> set.copy(modeGeneration = mode) }
        if (mode == ModeGenerationSingleTasks.RANDOM && settings.value.singleTask.everyFewDays > 1) {
            saveDaysNoRestriction()
        }
    }

    private fun savePeriodFrom(time: Int) {
        changeSettings { set: singleSet -> set.copy(periodFrom = time) }
    }

    private fun savePeriodTo(time: Int) {
        changeSettings { set: singleSet -> set.copy(periodTo = time) }
    }

    private fun savePeriodNoRestrictions() {
        val from = "00:00".timeToMinutes()
        val to = "23:59".timeToMinutes()
        changeSettings { set: singleSet -> set.copy(periodFrom = from, periodTo = to) }
    }

    private fun saveEveryFewDays(value: String) {
        when (val days = value.toIntOrNull()) {
            0, 1 -> saveDaysNoRestriction()
            is Int -> changeSettings { set: singleSet -> set.copy(everyFewDays = days, daysOfWeek = "") }
        }
    }

    private fun saveDaysOfWeek(list: List<Int>) {
        val daysOfWeek = list.joinToString(",")
        when (list.count()) {
            0, 7 -> saveDaysNoRestriction()
            else -> changeSettings { set: singleSet -> set.copy(everyFewDays = 1, daysOfWeek = daysOfWeek) }
        }
    }

    private fun saveDaysNoRestriction() =
        changeSettings { set: singleSet -> set.copy(everyFewDays = 1, daysOfWeek = "") }

    private fun saveCountTasks(value: String) {
        when (val count = value.toIntOrNull()) {
            0 -> changeSettings { set: singleSet -> set.copy(countGeneratedTasksAtATime = 1) }
            is Int -> changeSettings { set: singleSet -> set.copy(countGeneratedTasksAtATime = count) }
        }
    }

    private fun saveFrequency(valueFrom: String, valueTo: String) {
        val frequencyFrom = valueFrom.toIntOrNull() ?: 0
        val frequencyTo = valueTo.toIntOrNull() ?: 0
        if (frequencyFrom < frequencyTo) {
            changeSettings { set: singleSet -> set.copy(frequencyFrom = frequencyFrom, frequencyTo = frequencyTo) }
        }
    }

    fun submitAction(action: SetSTaskFreqActions) {
        viewModelScope.launch {
            pendingActions.emit(action)
        }
    }

    private fun changeSettings(body: (singleSet) -> singleSet) = viewModelScope.launch {
        repoSettings.updateSettings(settings.value.change(body))
    }

}