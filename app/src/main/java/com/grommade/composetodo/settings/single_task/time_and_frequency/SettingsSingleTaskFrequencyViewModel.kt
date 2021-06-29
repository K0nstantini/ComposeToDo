package com.grommade.composetodo.settings.single_task.time_and_frequency

import androidx.lifecycle.viewModelScope
import com.grommade.composetodo.Repository
import com.grommade.composetodo.add_classes.BaseViewModel
import com.grommade.composetodo.db.entity.Settings
import com.grommade.composetodo.enums.ModeGenerationSingleTasks
import com.grommade.composetodo.use_cases.UpdateSettings
import com.grommade.composetodo.util.change
import com.grommade.composetodo.util.singleSet
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsSingleTaskFrequencyViewModel @Inject constructor(
    private val repo: Repository,
    private val updateSettings: UpdateSettings
) : BaseViewModel() {

    val settings = repo.settingsFlow.asState(Settings())

    class SavesCallbacks(
        val saveMode: (Int) -> Unit = {},
        val savePeriodFrom: (Int) -> Unit = {},
        val savePeriodTo: (Int) -> Unit = {},
        val savePeriodNoRestrictions: () -> Unit = {},
        val saveEveryFewDays: (String) -> Unit = {},
        val saveDaysOfWeek: (List<Int>) -> Unit = {},
        val saveDaysNoRestrictions: () -> Unit = {},
        val saveCountTasks: (String) -> Unit = {},
        val saveFrequency: (String, String) -> Unit = { _, _ -> },
    )

    val savesCallbacks = SavesCallbacks(
        saveMode = ::saveMode,
        savePeriodFrom = ::savePeriodFrom,
        savePeriodTo = ::savePeriodTo,
        savePeriodNoRestrictions = ::savePeriodNoRestrictions,
        saveEveryFewDays = ::saveEveryFewDays,
        saveDaysOfWeek = ::saveDaysOfWeek,
        saveDaysNoRestrictions = ::saveDaysNoRestrictions,
        saveCountTasks = ::saveCountTasks,
        saveFrequency = ::saveFrequency,
    )

    private fun saveMode(index: Int) {
        val mode = ModeGenerationSingleTasks.values()[index]
        changeSettings { set: singleSet -> set.copy(modeGeneration = mode) }
    }

    private fun savePeriodFrom(time: Int) {
        changeSettings { set: singleSet -> set.copy(periodFrom = time) }
    }

    private fun savePeriodTo(time: Int) {
        changeSettings { set: singleSet -> set.copy(periodTo = time) }
    }

    private fun savePeriodNoRestrictions() {
        changeSettings { set: singleSet -> set.copy(periodFrom = 0, periodTo = 0) }
    }

    private fun saveEveryFewDays(value: String) {
        when (val days = value.toIntOrNull()) {
            0, 1 -> saveDaysNoRestrictions()
            is Int -> changeSettings { set: singleSet -> set.copy(everyFewDays = days, daysOfWeek = "") }
        }
    }

    private fun saveDaysOfWeek(list: List<Int>) {
        val daysOfWeek = list.joinToString(",")
        when (list.count()) {
            0, 7 -> saveDaysNoRestrictions()
            else -> changeSettings { set: singleSet -> set.copy(everyFewDays = 1, daysOfWeek = daysOfWeek) }
        }
    }

    private fun saveDaysNoRestrictions() =
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

    private fun changeSettings(body: (singleSet) -> singleSet) = viewModelScope.launch {
        updateSettings(settings.value.change(body))
    }

}