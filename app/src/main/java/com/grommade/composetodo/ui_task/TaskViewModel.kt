package com.grommade.composetodo.ui_task

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.grommade.composetodo.add_classes.BaseViewModel
import com.grommade.composetodo.data.entity.RandomTask
import com.grommade.composetodo.data.repos.RepoTask
import com.grommade.composetodo.enums.TypeTask
import com.grommade.composetodo.util.Keys
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val repoTask: RepoTask,
    handle: SavedStateHandle
) : BaseViewModel() {

    private val pendingActions = MutableSharedFlow<TaskActions>()

    private val taskId: Long = handle.get<Long>(Keys.TASK_ID) ?: -1L
    private val taskTypeStr: String = handle.get<String>(Keys.TASK_TYPE_KEY) ?: TypeTask.EXACT_TIME.name
    private val taskType: TypeTask = TypeTask.valueOf(taskTypeStr)

    private val currentTask = MutableStateFlow(RandomTask(type = taskType))

    val navigateToBack = MutableStateFlow<Boolean?>(null)

    val state = currentTask.map { task ->
        TaskViewState(
            task = currentTask.value,
            parent = repoTask.getTask(task.parent)?.name,
        )
    }

    init {
        viewModelScope.launch {
            repoTask.getTask(taskId)?.let { task ->
                currentTask.value = task
            }
            pendingActions.collect { action ->
                when (action) {
                    TaskActions.Save -> saveTask()
                    is TaskActions.ChangeName -> changeName(action.value)
                    else -> {
                    }
                }

            }
        }
    }

    private fun changeName(text: String) {
        if (text.length < 50) {
            currentTask.setValue { copy(name = text) }
        }
    }

    private fun saveTask() {
        viewModelScope.launch {
            repoTask.saveTask(currentTask.value)
            navigateToBack.value = true
        }
    }

    private fun MutableStateFlow<RandomTask>.setValue(block: RandomTask.() -> RandomTask) =
        apply { value = block(value) }

    fun submitAction(action: TaskActions) {
        viewModelScope.launch {
            pendingActions.emit(action)
        }
    }

}