package com.grommade.composetodo.ui_task.ui_type_task

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.grommade.composetodo.enums.TypeTask
import com.grommade.composetodo.util.Keys
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class TypeTaskViewModel @Inject constructor(
    handle: SavedStateHandle
) : ViewModel() {

    private val typeName: String = handle.get<String>(Keys.TASK_TYPE_KEY) ?: TypeTask.EXACT_TIME.name

    val type = MutableStateFlow(TypeTask.valueOf(typeName))

    fun changeType(value: TypeTask) {
        type.value = value
    }
}