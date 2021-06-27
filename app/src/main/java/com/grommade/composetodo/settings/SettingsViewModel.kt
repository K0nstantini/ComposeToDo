package com.grommade.composetodo.settings

import com.grommade.composetodo.Repository
import com.grommade.composetodo.add_classes.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repo: Repository
) : BaseViewModel() {
}