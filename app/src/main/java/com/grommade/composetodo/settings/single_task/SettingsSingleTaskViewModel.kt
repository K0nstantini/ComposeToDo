package com.grommade.composetodo.settings.single_task

import androidx.lifecycle.viewModelScope
import com.grommade.composetodo.Repository
import com.grommade.composetodo.add_classes.BaseViewModel
import com.grommade.composetodo.add_classes.MyCalendar
import com.grommade.composetodo.data.entity.Settings
import com.grommade.composetodo.use_cases.UpdateSettings
import com.grommade.composetodo.util.change
import com.grommade.composetodo.util.singleSet
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsSingleTaskViewModel @Inject constructor(
    repo: Repository,
    private val updateSettings: UpdateSettings
) : BaseViewModel() {

    val settings = repo.settingsFlow.asState(Settings())

    fun onClickActive(date: MyCalendar) {
        val state = date.isNoEmpty()
        changeSettings { set: singleSet ->
            set.copy(active = state, startGeneration = date, lastGeneration = MyCalendar())
        }
    }

    private fun changeSettings(body: (singleSet) -> singleSet) = viewModelScope.launch {
        updateSettings(settings.value.change(body))
    }

}