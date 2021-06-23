package com.grommade.composetodo.ui.task_list

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.outlined.Task
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grommade.composetodo.Repository
import com.grommade.composetodo.TasksScreen
import com.grommade.composetodo.add_classes.TaskItem
import com.grommade.composetodo.db.entity.Task
import com.grommade.composetodo.enums.ModeTaskList
import com.grommade.composetodo.enums.TypeTask
import com.grommade.composetodo.util.Keys
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskListViewModel @Inject constructor(
    private val repo: Repository,
    handle: SavedStateHandle
) : ViewModel() {

    /** Variables static */

    private val taskType: TypeTask =
        handle.get<String>(Keys.TASK_TYPE_KEY)?.let { TypeTask.valueOf(it) } ?: TypeTask.REGULAR_TASK

    private val mode: ModeTaskList =
        handle.get<String>(Keys.TASK_LIST_MODE_KEY)?.let { ModeTaskList.valueOf(it) } ?: ModeTaskList.DEFAULT

    val title: Int = when (taskType) {
        TypeTask.REGULAR_TASK -> mode.titleRegularTask
        TypeTask.SINGLE_TASK -> mode.titleSingleTask
    }

    /** Variables flow */

    private val allTasks: StateFlow<List<Task>> = repo.getTasksFlow(taskType).asState(emptyList())

    private var selectedTasks = MutableStateFlow(listOf<Long>())

    // FIXME
    val shownTasks: StateFlow<List<TaskItem>> = allTasks.combine(selectedTasks) { task, taskItem -> task to taskItem }
        .map { getTasksToShow(if (mode == ModeTaskList.SELECT_CATALOG) getOpenGroups() else it.first) }
        .asState(emptyList())

    private val currentTask: StateFlow<Task?> = selectedTasks
        .map { if (it.count() == 1) getTask(it.first()) else null }
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    private val currentIDTask: Long
        get() = currentTask.value?.id ?: -1

    val navigateToAddEditTask: String
        get() = when (taskType) {
            TypeTask.REGULAR_TASK -> TasksScreen.RegularTask.createRoute(currentIDTask)
            TypeTask.SINGLE_TASK -> TasksScreen.SingleTask.createRoute(currentIDTask)
        }

    val titleActionMode: StateFlow<String?> = selectedTasks
        .map { currentTask.value?.name ?: if (it.isEmpty()) null else "" }
        .asState(null)

    private val _actionMode: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val actionMode: StateFlow<Boolean> = _actionMode.asStateFlow()

    /** Variables Visibility & Enabling */

    val showAddButton: StateFlow<Boolean> = actionMode
        .map { !it && mode.showAddBtn }
        .asState(mode.showAddBtn)

    val showDoneActionMenu: StateFlow<Boolean> = selectedTasks
        .map { mapToShowDoneActionMenu(it) }
        .asState(false)

    val showEditActionMenu: StateFlow<Boolean> = selectedTasks
        .map { !actionMode.value || it.count() == 1 }
        .asState(false)


    /** =========================================== FUNCTIONS ==================================================== */

    private fun mapToShowDoneActionMenu(selected: List<Long>): Boolean {
        val noMultiSelect = selected.count() == 1
        val noGroup = !(currentTask.value?.group ?: false)
        return !actionMode.value || (taskType == TypeTask.SINGLE_TASK && noGroup && noMultiSelect)
    }

//    private fun mapToEnabledConfirmMenu() = when (val task = currentTask.value) {
//        is Task -> isMarkTaskForSelection(task)
//        else -> false
//    }

    /** Clicks */

    fun onTaskClicked(id: Long) {
        val task = requireNotNull(getTask(id))
        when {
            actionMode.value -> selectItemActionMode(id)
//          isMarkTaskForSelection(task) -> selectTaskForSelectionMode(task)
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

    fun onAddEditClicked() {
//        setEvent(Event.NavigateToAddEdit(currentTask.value))
        closeActionMode()
    }

    fun onDeleteClicked() {
        selectedTasks.value
            .map { getTask(it) ?: Task() }
            .delete()
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

    private fun setGroupOpenClose(task: Task) = task
        .apply { groupOpen = !groupOpen }
        .update()

    private fun getOpenGroups(): List<Task> =
        allTasks.value.filter { it.group }.map { it.copy(groupOpen = true) }

    private fun List<Task>.toTaskItem(): List<TaskItem> = map { task ->
        val level = task.getLevel(allTasks.value)
        TaskItem(
            id = task.id,
            name = task.name,
            isSelected = selectedTasks.value.contains(task.id),
            padding = 16 + level * 4,
            icon = when {
                task.groupOpen -> Icons.Filled.FolderOpen
                task.group -> Icons.Filled.Folder
                else -> Icons.Outlined.Task
            },
            fontSize = (16 - level * 2).coerceIn(8..16),
            fontWeight = if (task.group) FontWeight.Bold else FontWeight.Normal
        )
    }

    private fun <T> Flow<T>.asState(default: T) =
        stateIn(viewModelScope, SharingStarted.Lazily, default)

    private fun getTask(id: Long) = allTasks.value.find { it.id == id }
    private fun getTaskItem(id: Long) = shownTasks.value.find { it.id == id }

    private fun Task.update() = viewModelScope.launch { repo.updateTask(this@update) }
    private fun List<Task>.delete() = viewModelScope.launch { repo.deleteTasks(this@delete) }

    /**


    val enabledConfirmMenu: StateFlow<Boolean> = _selectedItems
    .map { mapToEnabledConfirmMenu() }
    .asState(false)



    private fun isMarkTaskForSelection(task: Task): Boolean =
    (mode == TaskListMode.SELECT_CATALOG) || (mode == TaskListMode.SELECT_TASK && !task.group)

    private fun selectTaskForSelectionMode(task: Task) =
    _selectedItems.apply { value = listOf(task.position()) }


    private fun getTask(index: Int): Task? = shownTasks.value.getOrNull(index)

    private fun Task.position() = shownTasks.value.indexOf(this)
    private fun Task.level(): Int = generateSequence(this) { getTask(it.parent) }.count() - 1


     */

}