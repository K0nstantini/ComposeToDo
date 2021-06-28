package com.grommade.composetodo.single_task

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.grommade.composetodo.MainRoute
import com.grommade.composetodo.Repository
import com.grommade.composetodo.add_classes.BaseViewModel
import com.grommade.composetodo.add_classes.MyCalendar
import com.grommade.composetodo.db.entity.Task
import com.grommade.composetodo.enums.ModeTaskList
import com.grommade.composetodo.enums.TypeTask
import com.grommade.composetodo.util.Keys
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SingleTaskViewModel @Inject constructor(
    private val repo: Repository,
    handle: SavedStateHandle
) : BaseViewModel() {

    data class SingleTaskItem(
        val name: String = "",
        val group: Boolean = false,
        val parent: String? = null,
        val dateStart: String = "",
        val deadline: Int = 0
    )

    private val currentTaskID: Long = handle.get<Long>(Keys.TASK_ID) ?: -1L
    private val currentTask = MutableStateFlow(Task(type = TypeTask.SINGLE_TASK))

    val title = currentTask
        .map { if (it.isNew) null else it.name }
        .asState(null)

    val taskItem = currentTask
        .map { task ->
            SingleTaskItem(
                name = task.name,
                group = task.group,
                parent = repo.getTask(task.parent)?.name,
                dateStart = task.single.dateStart.toString(false),
                deadline = task.single.deadline
            )
        }.asState(SingleTaskItem())

    val readyToSafe = currentTask
        .map { task -> task.name.isNotEmpty() && task.single.deadline > 0 }
        .asState(false)

    val navigateToSelectParent: String
        get() = MainRoute.TaskListChildRoute.createRoute(
            mode = ModeTaskList.SELECT_CATALOG,
            type = TypeTask.SINGLE_TASK,
            id = currentTask.value.parent
        )

    /** =========================================== INIT ========================================================= */

    init {
        viewModelScope.launch {
            repo.getTask(currentTaskID)?.let { task ->
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
        currentTask.setValue { copy(single = single.copy(dateStart = date))}
    }

    fun saveDeadline(text: String) {
        text.toIntOrNull()?.let { deadline ->
            currentTask.setValue { copy(single = single.copy(deadline = deadline))}
        }
    }

    fun saveTask() {
        if (readyToSafe.value) {
            currentTask.value.save()
        }
    }

    fun setParentsID(id: Long) {
        currentTask.setValue { copy(parent = id) }
    }

    private fun Task.save() = viewModelScope.launch { repo.saveTask(this@save) }

    private fun MutableStateFlow<Task>.setValue(block: Task.() -> Task) =
        apply { value = block(value) }

}
