package com.grommade.composetodo.ui_single_task

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.grommade.composetodo.add_classes.BaseViewModel
import com.grommade.composetodo.add_classes.MyCalendar
import com.grommade.composetodo.data.entity.Task
import com.grommade.composetodo.data.repos.RepoSingleTask
import com.grommade.composetodo.util.Keys
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SingleTaskViewModel @Inject constructor(
    private val repoSingleTask: RepoSingleTask,
    handle: SavedStateHandle
) : BaseViewModel() {

    private val pendingActions = MutableSharedFlow<SingleTaskActions>()

    private val currentTaskID: Long = handle.get<Long>(Keys.TASK_ID) ?: -1L
    private val currentTask = MutableStateFlow(Task())

    val state = currentTask.map { task ->
        SingleTaskViewState(
            title = if (task.isNew) null else task.name,
            name = task.name,
            group = task.group,
            parentStr = repoSingleTask.getTask(task.parent)?.name,
            parentId = task.parent,
            dateStart = task.single.dateStart.toString(false),
            deadline = task.single.deadlineDays,
        )
    }

    val navigateToBack = MutableStateFlow<Boolean?>(null)

    init {
        viewModelScope.launch {
            repoSingleTask.getTask(currentTaskID)?.let { task ->
                currentTask.value = task
            }
            pendingActions.collect { action ->
                when (action) {
                    is SingleTaskActions.ChangeName -> changeName(action.text)
                    is SingleTaskActions.ChangeGroup -> changeGroup(action.group)
                    is SingleTaskActions.ChangeDateStart -> changeDateStart(action.date)
                    is SingleTaskActions.ChangeDeadline -> changeDeadline(action.deadline)
                    SingleTaskActions.ClearParent -> clearParent()
                    SingleTaskActions.Save -> saveTask()
                    else -> {
                    }
                }

            }
        }
    }

    fun submitAction(action: SingleTaskActions) {
        viewModelScope.launch {
            pendingActions.emit(action)
        }
    }

    private fun changeName(text: String) {
        if (text.length < 50) {
            currentTask.setValue { copy(name = text) }
        }
    }

    private fun changeGroup(group: Boolean) {
        currentTask.setValue { copy(group = group) }
    }

    private fun clearParent() {
        currentTask.setValue { copy(parent = 0L) }
    }

    private fun changeDateStart(date: MyCalendar) {
        currentTask.setValue { copy(single = single.copy(dateStart = date)) }
    }

    private fun changeDeadline(value: Int) {
        if (value > 0) {
            currentTask.setValue { copy(single = single.copy(deadlineDays = value)) }
        }
    }

    private fun saveTask() {
        viewModelScope.launch {
            repoSingleTask.saveTask(currentTask.value)
            navigateToBack.value = true
        }
    }

    fun setParentsID(id: Long) {
        currentTask.setValue { copy(parent = id) }
    }

    private fun MutableStateFlow<Task>.setValue(block: Task.() -> Task) =
        apply { value = block(value) }

}
