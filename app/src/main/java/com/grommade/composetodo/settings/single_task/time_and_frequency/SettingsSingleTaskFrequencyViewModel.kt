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

    fun saveMode(index: Int) {
        val mode = ModeGenerationSingleTasks.values()[index]
        changeSettings { set: singleSet -> set.copy(modeGeneration = mode) }
    }

    fun savePeriodFrom(time: Int) {
        changeSettings { set: singleSet -> set.copy(periodFrom = time) }
    }

    fun savePeriodTo(time: Int) {
        changeSettings { set: singleSet -> set.copy(periodTo = time) }
    }

    fun resetPeriod() {
        changeSettings { set: singleSet -> set.copy(periodFrom = 0, periodTo = 0) }
    }

    private fun changeSettings(body: (singleSet) -> singleSet) = viewModelScope.launch {
        updateSettings(settings.value.change(body))
    }

}