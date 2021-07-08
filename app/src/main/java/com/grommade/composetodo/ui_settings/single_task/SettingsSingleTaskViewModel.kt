package com.grommade.composetodo.ui_settings.single_task

import androidx.lifecycle.viewModelScope
import com.grommade.composetodo.add_classes.BaseViewModel
import com.grommade.composetodo.add_classes.MyCalendar
import com.grommade.composetodo.data.entity.Settings
import com.grommade.composetodo.data.repos.RepoSettings
import com.grommade.composetodo.util.extensions.change
import com.grommade.composetodo.util.extensions.singleSet
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsSingleTaskViewModel @Inject constructor(
    private val repoSettings: RepoSettings,
) : BaseViewModel() {

    private val settings = repoSettings.settingsFlow.asState(Settings())

    val settingsState = settings.map { set ->
        set.singleTask
    }


    fun changeStartGeneration(date: MyCalendar) {
        val state = date.isNoEmpty()
        changeSettings { set: singleSet ->
            set.copy(active = state, startGeneration = date, lastGeneration = MyCalendar())
        }
    }

    private fun changeSettings(body: (singleSet) -> singleSet) = viewModelScope.launch {
        repoSettings.updateSettings(settings.value.change(body))
    }

}