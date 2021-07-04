package com.grommade.composetodo.statistics

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

    val regularPoints = settings
        .map { set-> set.regularTask.points }
        .asState(0)

    val singlePoints = settings
        .map { set-> set.singleTask.points }
        .asState(0)

}