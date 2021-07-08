package com.grommade.composetodo.ui_settings

import androidx.lifecycle.ViewModel
import com.grommade.composetodo.add_classes.BaseViewModel
import com.grommade.composetodo.data.repos.RepoSettings
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repoSettings: RepoSettings
) : ViewModel() {
}