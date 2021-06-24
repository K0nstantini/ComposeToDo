package com.grommade.composetodo.single_task

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grommade.composetodo.MainScreen
import com.grommade.composetodo.R
import com.grommade.composetodo.Repository
import com.grommade.composetodo.db.entity.Task
import com.grommade.composetodo.enums.ModeTaskList
import com.grommade.composetodo.enums.TypeTask
import com.grommade.composetodo.settings.SettingItem
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

    private val currentTaskID: Long = handle.get<Long>(Keys.TASK_ID) ?: -1L
    private val currentTask = MutableStateFlow(Task(type = TypeTask.SINGLE_TASK))

    val title = currentTask
        .map { if (it.isNew) null else it.name }
        .asState(null)

    val taskName = currentTask
        .map { it.name }
        .asState("")

    val settings: StateFlow<List<SettingItem>> = currentTask
        .map {
            val setDeadline = it.single.deadline > 0
            listOf(
                SettingItem(R.string.settings_add_task_title_group)
                    .setSwitch(::onGroupSwitchClicked, it.group)
                    .setAction(::onGroupClicked),
                SettingItem(R.string.settings_add_task_title_parent)
                    .setClear(::onParentClearClicked, it.parent != 0L)
                    .setAction(::onParentClicked)
                    .setValue(repo.getTask(it.parent)?.name, R.string.settings_main_catalog_text),
                SettingItem(R.string.settings_add_single_task_title_date_start)
                    .setAction(::onsDateStartClicked)
                    .setValue(it.single.dateStart.toString(false)),
                SettingItem(R.string.settings_add_single_task_title_deadline)
                    .setAction(::onDeadlineClicked)
                    .setValue(
                        it.single.deadline.toString() + if (setDeadline) "*R.string*" else "",
                        if (setDeadline) {
                            R.string.settings_add_single_task_deadline_time_hours_text
                        } else {
                            R.string.settings_add_single_task_deadline_zero_text
                        }
                    )
            )
        }
        .asState(emptyList())

    private var _navigateToSelectParent = MutableSharedFlow<String>()
    val navigateToSelectParent = _navigateToSelectParent.asSharedFlow()

    /** =========================================== INIT ========================================================= */

    init {
        viewModelScope.launch {
            repo.getTask(currentTaskID)?.let { task ->
                currentTask.value = task
            }
        }
    }

    /** =========================================== FUNCTIONS ==================================================== */


    /** Effects */

    fun onTaskNameChange(text: String) {
        currentTask.apply {
            if (text.length < 100) {
                value = value.copy(name = text)
            }
        }
    }

    private fun onGroupClicked() {
        onGroupSwitchClicked(!currentTask.value.group)
    }

    private fun onGroupSwitchClicked(group: Boolean) {
        currentTask.apply {
            value = value.copy(group = group)
        }
    }

    private fun onParentClicked() {
        viewModelScope.launch {
            _navigateToSelectParent.emit(
                MainScreen.TaskList.createRoute(
                    mode = ModeTaskList.SELECT_CATALOG,
                    type = TypeTask.SINGLE_TASK,
                    id = currentTask.value.parent
                )
            )
        }
    }

    private fun onParentClearClicked() {
        currentTask.apply {
            value = value.copy(parent = 0L)
        }
    }

    private fun onsDateStartClicked() {
        // TODO
    }

    private fun onDeadlineClicked() {
        // TODO
//        val timeDeadline = when (_deadline.value ?: 0) {
//            0 -> DEFAULT_DEADLINE_SINGLE_TASK
//            else -> _deadline.value
//        }.toString()
    }

    fun setParentsID(id: Long) {
        currentTask.apply {
            value = value.copy(parent = id)
        }
    }

    private fun <T> Flow<T>.asState(default: T) =
        stateIn(viewModelScope, SharingStarted.Lazily, default)
}

/**

sealed class Event1 {
object NavigateToParent : Event1()
object NavigateToBack : Event1()
}

{

// TODO
//    val taskName = MutableLiveData(currentTask.name)

val taskName = MutableStateFlow(currentTask.name)



private val _group = MutableStateFlow(currentTask.group)
val group: StateFlow<Pair<SettingItem, Boolean>> = _group
.map {
setEnabledSettings()
setGroup to it
}
.asState(setGroup to currentTask.group)

private val _parent = MutableStateFlow(currentTask.parent)
val parent: StateFlow<Pair<SettingItem, Task?>> = _parent
.map { setParent to repo.getTask(it) }
.asState(setParent to null)

private val _dateStart = MutableStateFlow(currentTask.single.dateStart)
val dateStart: StateFlow<Pair<SettingItem, MyCalendar>> = _dateStart
.map { setDateStart to it }
.asState(setDateStart to currentTask.single.dateStart)

private val _deadline = MutableStateFlow(currentTask.single.deadline)
val deadline: StateFlow<Pair<SettingItem, Int>> = _deadline
.map { setDeadline to it }
.asState(setDeadline to currentTask.single.deadline)



fun setParent(id: Long) = _parent.apply { value = id }



val dialog = MyInputDialog(::saveDeadline, timeDeadline)
.setTitle(R.string.alert_title_add_single_task_deadline)
.setLength(2)
setInputDialog(dialog)
}

fun onSaveClicked(): Boolean {
// TODO: Проверить заполнение
//        saveTask()
setEvent(Event1.NavigateToBack)
return true
}

//    private fun saveTask() {
//        currentTask.setDataSingleTask(taskName, _group, _parent, _dateStart, _deadline)
//        when (currentTask.id) {
//            0L -> currentTask.insert()
//            else -> currentTask.update()
//        }
//    }

private fun saveDeadline(value: String) {
_deadline.value = value.toIntOrNull() ?: 0
}

private fun setEnabledSettings() {
setDateStart.setEnabled(!_group.value)
setDeadline.setEnabled(!_group.value)
}

private fun Task.update() = viewModelScope.launch { repo.updateTask(this@update) }
private fun Task.insert() = viewModelScope.launch { repo.insertTask(this@insert) }

private fun <T> Flow<T>.asState(default: T) =
stateIn(viewModelScope, SharingStarted.Lazily, default)

}

 */

/**
var dateUntilToDo: MyCalendar = MyCalendar(),           // Задача должна быть сгенерирована до этой даты
var toDoAfterTask: String = ""                          // Задача будет сегенрирована только после выполнения другой задачи
 **/