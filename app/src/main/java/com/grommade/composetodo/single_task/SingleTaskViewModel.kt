package com.grommade.composetodo.single_task

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.grommade.composetodo.MainRoute
import com.grommade.composetodo.add_classes.BaseViewModel
import com.grommade.composetodo.add_classes.MyCalendar
import com.grommade.composetodo.data.entity.Task
import com.grommade.composetodo.data.repos.RepoSingleTask
import com.grommade.composetodo.enums.ModeTaskList
import com.grommade.composetodo.enums.TypeTask
import com.grommade.composetodo.util.Keys
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SingleTaskViewModel @Inject constructor(
    private val repoSingleTask: RepoSingleTask,
    handle: SavedStateHandle
) : BaseViewModel() {

    data class SingleTaskItem(
        val title: String? = null,
        val name: String = "",
        val group: Boolean = false,
        val parent: String? = null,
        val dateStart: String = "",
        val deadline: Int = 0,
        val readyToSave: Boolean = false
    )

    private val currentTaskID: Long = handle.get<Long>(Keys.TASK_ID) ?: -1L
    private val currentTask = MutableStateFlow(Task(type = TypeTask.SINGLE_TASK))

    val taskItem = currentTask
        .map { task ->
            SingleTaskItem(
                title = if (task.isNew) null else task.name,
                name = task.name,
                group = task.group,
                parent = repoSingleTask.getTask(task.parent)?.name,
                dateStart = task.single.dateStart.toString(false),
                deadline = task.single.deadline,
                readyToSave = task.name.isNotEmpty() && task.single.deadline > 0,
            )
        }.asState(SingleTaskItem())

    val navigateToSelectParentRout: String
        get() = MainRoute.TaskListChildRoute.createRoute(
            mode = ModeTaskList.SELECT_CATALOG,
            type = TypeTask.SINGLE_TASK,
            id = currentTask.value.parent
        )

    val navigateToBack: MutableStateFlow<Boolean?> = MutableStateFlow(null)

    /** =========================================== INIT ========================================================= */

    init {
        viewModelScope.launch {
            repoSingleTask.getTask(currentTaskID)?.let { task ->
                currentTask.value = task
            }
        }
    }

    /** =========================================== FUNCTIONS ==================================================== */


    fun onTaskNameChange(text: String) {
        if (text.length < 100) {
            currentTask.setValue { copy(name = text) }
        }
    }

    fun onGroupClicked(group: Boolean) {
        currentTask.setValue { copy(group = group) }
    }

    fun onParentClearClicked() {
        currentTask.setValue { copy(parent = 0L) }
    }

    fun saveDateStart(date: MyCalendar) {
        currentTask.setValue { copy(single = single.copy(dateStart = date)) }
    }

    fun saveDeadline(text: String) {
        text.toIntOrNull()?.let { deadline ->
            currentTask.setValue { copy(single = single.copy(deadline = deadline)) }
        }
    }

    fun saveTask() {
        if (taskItem.value.readyToSave) {
            viewModelScope.launch {
                repoSingleTask.saveTask(currentTask.value)
                navigateToBack.value = true
            }
        }
    }

    fun setParentsID(id: Long) {
        currentTask.setValue { copy(parent = id) }
    }

    private fun MutableStateFlow<Task>.setValue(block: Task.() -> Task) =
        apply { value = block(value) }

}
