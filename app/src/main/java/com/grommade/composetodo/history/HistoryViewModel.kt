package com.grommade.composetodo.history

import com.grommade.composetodo.Repository
import com.grommade.composetodo.add_classes.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(repo: Repository) : BaseViewModel() {

    val histories = repo.historyFlow

}