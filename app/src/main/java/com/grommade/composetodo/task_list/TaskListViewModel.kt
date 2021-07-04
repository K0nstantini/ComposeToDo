package com.grommade.composetodo.task_list

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.grommade.composetodo.Repository
import com.grommade.composetodo.TasksRoute
import com.grommade.composetodo.add_classes.BaseViewModel
import com.grommade.composetodo.data.entity.Task
import com.grommade.composetodo.enums.ModeTaskList
import com.grommade.composetodo.enums.TypeTask
import com.grommade.composetodo.use_cases.DeleteTask
import com.grommade.composetodo.use_cases.PerformSingleTask
import com.grommade.composetodo.util.Keys
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskListViewModel @Inject constructor(
    private val repo: Repository,
    private val performSingleTask: PerformSingleTask,
    private val deleteTask: DeleteTask,
    handle: SavedStateHandle,
) : BaseViewModel() {

    data class TaskItem(
        val task: Task,
        val level: Int,
        var isSelected: Boolean,
    )

    /** Variables static */

    private val taskType: TypeTask =
        handle.get<String>(Keys.TASK_TYPE_KEY)?.let { TypeTask.valueOf(it) } ?: TypeTask.REGULAR_TASK

    private val mode: ModeTaskList =
        handle.get<String>(Keys.TASK_LIST_MODE_KEY)?.let { ModeTaskList.valueOf(it) } ?: ModeTaskList.DEFAULT

    private val selectedTaskID: Long = handle.get<Long>(Keys.TASK_ID) ?: -1L

    val defaultTitle: Int = when (taskType) {
        TypeTask.REGULAR_TASK -> mode.titleRegularTask
        TypeTask.SINGLE_TASK -> mode.titleSingleTask
    }

    val routToAddEditTask: String
        get() = when (taskType) {
            TypeTask.REGULAR_TASK -> TasksRoute.RegularTaskChildRoute.createRoute(currentTaskID)
            TypeTask.SINGLE_TASK -> TasksRoute.SingleTaskChildRoute.createRoute(currentTaskID)
        }

    /** Variables flow */

    private val allTasks: StateFlow<List<Task>> = repo.getTasksFlow(taskType).asState(emptyList())

    private var selectedTasks: MutableStateFlow<List<Long>> = MutableStateFlow(
        if (selectedTaskID > 0L) listOf(selectedTaskID) else emptyList()
    )

    val shownTasks: StateFlow<List<TaskItem>> = allTasks.combine(selectedTasks) { tasks, _ -> tasks }
        .map { getTasksToShow(if (mode == ModeTaskList.SELECT_CATALOG) getOpenGroups() else it) }
        .asState(emptyList())

    private val currentTask: StateFlow<Task?> = selectedTasks
        .map { mapToCurrentTask(it) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val currentTaskID: Long
        get() = currentTask.value?.id ?: -1

    val title: StateFlow<String?> = selectedTasks.combine(currentTask) { selected, task -> selected to task }
        .map { it.second?.name ?: if (it.first.isEmpty()) null else "" }
        .asState(null)

    private val _actionMode: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val actionMode: StateFlow<Boolean> = _actionMode.asStateFlow()

    /** Variables Visibility & Availability */

    data class Availability(
        val showAddButton: Boolean = false,
        val showDoneActionMenu: Boolean = false,
        val showEditActionMenu: Boolean = false,
        val enabledDoneBtn: Boolean = false
    )

    val availability = actionMode.combine(selectedTasks) { actMode, selected ->
        Availability(
            showAddButton = !actMode && mode.showAddBtn,
            showDoneActionMenu = mapToShowDoneActionMenu(selected),
            showEditActionMenu = !actionMode.value || selected.count() == 1,
            enabledDoneBtn = selected.count() == 1
        )
    }.asState(Availability())

    /** =========================================== FUNCTIONS ==================================================== */

    private fun mapToShowDoneActionMenu(selected: List<Long>): Boolean {
        val noMultiSelect = selected.count() == 1
        val noGroup = !(currentTask.value?.group ?: false)
        val select = (mode == ModeTaskList.SELECT_CATALOG || mode == ModeTaskList.SELECT_TASK)
        return select || (actionMode.value && taskType == TypeTask.SINGLE_TASK && noGroup && noMultiSelect)
    }

    private suspend fun mapToCurrentTask(selected: List<Long>): Task? =
        // При выборе родителя нужно получить текущую задачу сразу и allTask еще не подгрузятся
        when (val id = if (selected.count() == 1) selected.first() else null) {
            is Long -> getTask(id) ?: repo.getTask(id)
            else -> null
        }

    /** Clicks */

    fun onTaskClicked(id: Long) {
        val task = requireNotNull(getTask(id))
        when {
            actionMode.value -> selectItemActionMode(id)
            isMarkTaskForSelection(task) -> selectTaskForSelectionMode(id)
            task.group -> setGroupOpenClose(task)
        }
    }

    fun onTaskLongClicked(id: Long) {
        if (mode.supportLongClick) {
            if (!_actionMode.value) {
                _actionMode.value = true
            }
            selectItemActionMode(id)
        }
    }

    fun onTaskDoneClicked() {
        viewModelScope.launch {
            currentTask.value?.let { performSingleTask(it) }
        }
        closeActionMode()
    }

    fun onDeleteClicked() {
        viewModelScope.launch {
            val tasks = selectedTasks.value
                .map { getTask(it) }
                .filterIsInstance<Task>()
            deleteTask(tasks = tasks)
        }
        closeActionMode()
    }


    fun closeActionMode() {
        _actionMode.value = false
        selectedTasks.value = emptyList()
    }

    private fun selectItemActionMode(id: Long) {
        val listAfter = { list: List<Long> -> if (list.contains(id)) list - id else list + id }
        selectedTasks.value = listAfter(selectedTasks.value)
        if (selectedTasks.value.isEmpty()) {
            closeActionMode()
        }
    }

    private fun selectTaskForSelectionMode(id: Long) =
        selectedTasks.apply { value = listOf(id) }

    // FIXME?
    private fun getTasksToShow(
        tasks: List<Task>,
        id: Long = 0,
        list: MutableList<Task> = mutableListOf()
    ): List<TaskItem> {
        tasks
            .filter { it.parent == id }
            .sortedWith(compareByDescending<Task> { it.group }.thenBy { it.name })
            .forEach {
                list.add(it)
                if (it.groupOpen) {
                    getTasksToShow(tasks, it.id, list)
                }
            }
        return list.toTaskItem()
    }

    private fun setGroupOpenClose(task: Task) =
        task.copy(groupOpen = !task.groupOpen).save()

    private fun getOpenGroups(): List<Task> =
        allTasks.value.filter { it.group }.map { it.copy(groupOpen = true) }

    private fun List<Task>.toTaskItem(): List<TaskItem> = map { task ->
        TaskItem(
            task = task,
            level = task.getLevel(allTasks.value),
            isSelected = selectedTasks.value.contains(task.id)
        )
    }

    private fun isMarkTaskForSelection(task: Task): Boolean =
        (mode == ModeTaskList.SELECT_CATALOG) || (mode == ModeTaskList.SELECT_TASK && !task.group)

    private fun getTask(id: Long): Task? = allTasks.value.find { it.id == id }

    private fun Task.save() = viewModelScope.launch (Dispatchers.IO) { repo.saveTask(this@save) }
}