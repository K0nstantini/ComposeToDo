package com.grommade.composetodo.settings.single_task

import androidx.lifecycle.viewModelScope
import com.grommade.composetodo.Repository
import com.grommade.composetodo.add_classes.BaseViewModel
import com.grommade.composetodo.db.entity.Settings
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsSingleTaskViewModel @Inject constructor(
    private val repo: Repository
) : BaseViewModel() {

    val settings = repo.settingsFlow.asState(Settings())

    fun onClickActive(active: Boolean) {
        changeSettings(active = active).update()
    }

    private fun changeSettings(
        active: Boolean? = null
    ): Settings {
        val oldActive = settings.value.singleTask.active
        return settings.value.copy(singleTask = settings.value.singleTask.copy(active = active ?: oldActive))
    }

    // TODO: Проверять перед записью актуальные настройки ли загрузились
    private fun Settings.update() = viewModelScope.launch { repo.updateSettings(this@update) }
}