package com.grommade.composetodo.ui_task_list

import androidx.lifecycle.viewModelScope
import com.grommade.composetodo.add_classes.BaseViewModel
import com.grommade.composetodo.data.entity.Task
import com.grommade.composetodo.data.repos.RepoTask
import com.grommade.composetodo.use_cases.DeleteTask
import com.grommade.composetodo.use_cases.PerformSingleTask
import com.grommade.composetodo.use_cases.PopulateDBWithTasks
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskListViewModel @Inject constructor(
    private val repoSingleTask: RepoTask,
    private val performSingleTask: PerformSingleTask,
    private val populateDBWithTasks: PopulateDBWithTasks,
    private val deleteTask: DeleteTask,
) : BaseViewModel() {

    private val pendingActions = MutableSharedFlow<TaskListActions>()

    private val shownTasks = repoSingleTask.allTasks
        .map { it.hierarchicalView() }

    val state = shownTasks.map { tasks -> TaskListViewState(tasks = tasks) }

    init {
        viewModelScope.launch {
            pendingActions.collect { action ->
                when (action) {
                    TaskListActions.PopulateDBWithTasks -> populateDBWithTasks()
                    is TaskListActions.OpenCloseGroup -> openCloseGroup(action.task)
                    is TaskListActions.PerformTask -> performSingleTask(action.task)
                    is TaskListActions.DeleteTasks -> deleteTask(tasks = action.tasks)
                    else -> {
                    }
                }
            }
        }
    }

    fun submitAction(action: TaskListActions) {
        viewModelScope.launch { pendingActions.emit(action) }
    }

    // FIXME?
    private fun List<Task>.hierarchicalView(id: Long = 0, list: MutableList<Task> = mutableListOf()): List<Task> {
        filter { it.parent == id }
            .sortedWith(compareByDescending<Task> { it.group }.thenBy { it.name })
            .forEach {
                list.add(it)
                if (it.groupOpen) {
                    hierarchicalView(it.id, list)
                }
            }
        return list
    }

    private fun openCloseGroup(task: Task) {
        if (task.group) {
            task.copy(groupOpen = !task.groupOpen).save()
        }
    }

    private fun Task.save() = viewModelScope.launch(Dispatchers.IO) { repoSingleTask.saveTask(this@save) }
}