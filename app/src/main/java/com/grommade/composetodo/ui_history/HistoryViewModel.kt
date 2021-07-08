package com.grommade.composetodo.ui_history

import androidx.lifecycle.viewModelScope
import com.grommade.composetodo.add_classes.BaseViewModel
import com.grommade.composetodo.data.repos.RepoHistory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(private val repoHistory: RepoHistory) : BaseViewModel() {

    val histories = repoHistory.allHistory

    fun deleteAllHistory() = viewModelScope.launch {
        repoHistory.deleteAllHistory()
    }

}