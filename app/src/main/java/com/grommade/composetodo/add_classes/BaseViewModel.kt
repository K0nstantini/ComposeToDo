package com.grommade.composetodo.add_classes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grommade.composetodo.db.entity.History
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

abstract class BaseViewModel : ViewModel() {

    fun <T> Flow<T>.asState(default: T) =
        stateIn(viewModelScope, SharingStarted.Lazily, default)
}