package com.grommade.composetodo.settings.single_task

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
class SettingsSingleTaskViewModel @Inject constructor(
    private val repo: Repository,
    private val updateSettings: UpdateSettings
) : BaseViewModel() {
    
    val settings = repo.settingsFlow.asState(Settings())

    fun onClickActive(active: Boolean) {
        viewModelScope.launch {
            updateSettings(
                settings.value.change { set: singleSet -> set.copy(active = active) }
            )
        }
    }

}