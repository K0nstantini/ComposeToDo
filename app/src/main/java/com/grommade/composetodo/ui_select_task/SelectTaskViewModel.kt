package com.grommade.composetodo.ui_select_task

import androidx.lifecycle.ViewModel
import com.grommade.composetodo.data.repos.RepoSingleTask
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class SelectTaskViewModel @Inject constructor(
    repoSingleTask: RepoSingleTask,
) : ViewModel() {

    private val groups = repoSingleTask.groups

    val state = groups.map { groups ->
        SelectTaskViewState(
            tasks = groups.sortedBy { it.hierarchicalSort(groups) }
        )
    }

}