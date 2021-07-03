package com.grommade.composetodo.history

import androidx.lifecycle.viewModelScope
import com.grommade.composetodo.Repository
import com.grommade.composetodo.add_classes.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(private val repo: Repository) : BaseViewModel() {

    val histories = repo.historyFlow

    val countRecords = histories.map { it.count() }.asState(0)

    fun onClickDeleteAll() {
        deleteAllHistory()
    }

    private fun deleteAllHistory() = viewModelScope.launch {
        repo.deleteAllHistory()
    }

}