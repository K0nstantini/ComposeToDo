package com.grommade.composetodo.ui_statistics

import com.grommade.composetodo.add_classes.BaseViewModel
import com.grommade.composetodo.data.repos.RepoSettings
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    repoSettings: RepoSettings
) : BaseViewModel() {

    private val settings = repoSettings.settingsFlow

    val state = settings.map {set ->
        StatisticsViewState(singlePoints = set.singleTask.points)
    }

}