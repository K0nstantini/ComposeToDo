package com.grommade.composetodo.single_task

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grommade.composetodo.MainScreen
import com.grommade.composetodo.Repository
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
) : ViewModel() {

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
        get() = MainScreen.TaskList.createRoute(
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
        currentTask.apply {
            if (text.length < 100) {
                value = value.copy(name = text)
            }
        }
    }

    fun onGroupClicked(group: Boolean) {
        currentTask.apply {
            value = value.copy(group = group)
        }
    }

    fun onParentClearClicked() {
        currentTask.apply {
            value = value.copy(parent = 0L)
        }
    }

    fun saveDateStart(date: MyCalendar) {
        currentTask.apply {
            value = value.copy(single = value.single.copy(dateStart = date))
        }
    }

    fun saveDeadline(text: String) {
        text.toIntOrNull()?.let { deadline ->
            currentTask.apply {
                value = value.copy(single = value.single.copy(deadline = deadline))
            }
        }
    }

    fun saveTask() {
        if (readyToSafe.value) {
            when (currentTask.value.isNew) {
                true -> currentTask.value.insert()
                else -> currentTask.value.update()
            }
        }
    }

    fun setParentsID(id: Long) {
        currentTask.apply {
            value = value.copy(parent = id)
        }
    }

    private fun Task.update() = viewModelScope.launch { repo.updateTask(this@update) }
    private fun Task.insert() = viewModelScope.launch { repo.insertTask(this@insert) }

    private fun <T> Flow<T>.asState(default: T) =
        stateIn(viewModelScope, SharingStarted.Lazily, default)
}
