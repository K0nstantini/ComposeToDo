package com.grommade.composetodo.home

import androidx.lifecycle.viewModelScope
import com.grommade.composetodo.Repository
import com.grommade.composetodo.add_classes.BaseViewModel
import com.grommade.composetodo.add_classes.MyCalendar
import com.grommade.composetodo.data.entity.Settings
import com.grommade.composetodo.data.entity.Task
import com.grommade.composetodo.use_cases.GenerateSingleTasks
import com.grommade.composetodo.use_cases.PerformSingleTask
import com.grommade.composetodo.use_cases.UpdateSettings
import com.grommade.composetodo.util.change
import com.grommade.composetodo.util.singleSet
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repo: Repository,
    private val performSingleTask: PerformSingleTask,
    private val updateSettings: UpdateSettings,
    private val generateSingleTasks: GenerateSingleTasks
) : BaseViewModel() {

    data class HomeTaskItem(
        val id: Long,
        val name: String,
        val deadline: MyCalendar,
        val selected: Boolean
    )

    private val settings: StateFlow<Settings> = repo.settingsFlow.asState(Settings())

    private val activatedSingleTasks = repo.activatedSingleTasks.asState(emptyList())

    private val currentTask = MutableStateFlow<Task?>(null)

    val actionMode = currentTask
        .map { task -> task != null }
        .asState(false)

    val actionTitle = currentTask
        .map { task -> task?.name ?: "" }
        .asState("")

    val tasksItems = activatedSingleTasks.combine(currentTask) { tasks, _ -> tasks }
        .map { tasks ->
            tasks.map { task ->
                HomeTaskItem(
                    id = task.id,
                    name = task.name,
                    deadline = task.single.dateActivation.addHours(task.single.deadline),
                    selected = task.id == currentTask.value?.id ?: 0
                )
            }
        }
        .asState(emptyList())


    /** =========================================== FUNCTIONS ==================================================== */

    fun refreshTasks() = viewModelScope.launch {
        if (repo.getSettings().isNotEmpty()) { // FIXME: WTF?
            generateSingleTasks()
        }
    }

    fun deactivateTasks() = viewModelScope.launch {
        updateSettings(
            settings.value.change { set: singleSet -> set.copy(lastGeneration = MyCalendar()) }
        )
        val tasks = repo.getAllSingleTasks()
        tasks.filter { it.single.dateActivation.isNoEmpty() }.forEach { task ->
            task.copy(single = task.single.copy(dateActivation = MyCalendar())).save()
        }
        tasks.filter { it.single.rolls > 0 }.forEach { task ->
            task.copy(single = task.single.copy(rolls = 0)).save()
        }
    }

    fun onTaskLongClicked(id: Long) {
        currentTask.value = when (currentTask.value?.id) {
            id -> null
            else -> activatedSingleTasks.value.find { it.id == id }
        }
    }

    fun onTaskDoneClicked() {
        viewModelScope.launch {
            currentTask.value?.let { performSingleTask(it) }
        }
        closeActionMode()
    }

    fun closeActionMode() {
        currentTask.value = null
    }

    private fun Task.save() = viewModelScope.launch { repo.saveTask(this@save) }

}

/**


val settingsLive = repo.settingsFlow.asLiveData()
private val settings get() = checkNotNull(settingsLive.value) { "Settings isn't initialised" }

private val sDateActivation get() = settings.singleTask.dateActivation
private val sPostponeCurrentTask get() = settings.singleTask.postponeCurrentTaskForOnePoint
private val sPostponeNextTask get() = settings.singleTask.postponeNextTaskForOnePoint

/** ======================================================================================= */

private val tasksLive: LiveData<List<Task>> = repo.getTasksFlow(TypeTask.SINGLE_TASK).asLiveData()
val shownSingleTasks = Transformations.map(tasksLive) { tasks ->
tasks.filter { it.single.dateActivation.isNoEmpty() }
.sortedBy { it.single.dateActivation.milli + it.single.deadline.hoursToMilli() }
}
private val tasks: List<Task> get() = tasksLive.value ?: emptyList()

private var isActionMode: Boolean = false

private val _showActionMode = MutableLiveData<Event<Boolean>>()
val showActionMode: LiveData<Event<Boolean>> get() = _showActionMode

private val _hideActionMode = MutableLiveData<Event<Boolean>>()
val hideActionMode: LiveData<Event<Boolean>> get() = _hideActionMode

private var currentTask = MutableLiveData<Task?>(null)
val currentTaskName: String get() = currentTask.value?.name ?: String()
val currentTaskPosition = Transformations.map(currentTask) { it?.position() ?: -1 }



fun onItemClicked(task: Task) {
currentTask.value = task
if (isActionMode) destroyActionMode() else setActionMode()
}

fun onPostponeCurrentTaskClicked() = postponeTask(
::selectTimeToPostponeCurrentTask,
R.string.alert_title_postpone_current_task,
sPostponeCurrentTask
)

fun onPostponeNextTaskClicked() = postponeTask(
::selectTimeToPostponeNextTask,
R.string.alert_title_postpone_next_task,
sPostponeNextTask
)

fun onRollClicked() {
when {
!settings.singleTask.canRoll ->
setMessage(R.string.message_not_enough_points)
currentTask.value?.canRoll(settings) == false ->
setMessage(R.string.message_over_limit_rolls_for_task)
else -> {
val dialog = MyConfirmAlertDialog(::rollSingleTask)
.setTitle(currentTaskName)
.setMessage(R.string.alert_title_single_task_roll)
setConfirmDialog(dialog)
}
}
}

fun onDoneClicked() = setConfirmDialog(
MyConfirmAlertDialog(::doneSingleTask)
.setTitle(currentTaskName)
.setMessage(R.string.alert_title_single_task_done)
)


private fun postponeTask(foo: (Int) -> Unit, title: Int, pointsForTask: Int): Boolean {
if (settings.singleTask.points > 0) {
val dialog = MySingleChoiceDialog(foo)
.setTitle(title)
.setItems(getTimesToPostpone(pointsForTask))
setSingleChoiceDialog(dialog)
} else {
setMessage(R.string.message_not_enough_points)
}
return true
}

private fun selectTimeToPostponeCurrentTask(index: Int) {
currentTask.value?.apply {
single.deadline += (index + 1) * sPostponeCurrentTask
settings.removePoints(index + 1)
destroyActionMode()
}?.update()
}

private fun selectTimeToPostponeNextTask(index: Int) {
val hours = (index + 1) * sPostponeNextTask
settings.apply {
singleTask.dateActivation = sDateActivation.addHours(hours)
settings.removePoints(index + 1)
}.update()
}

private fun rollSingleTask() {
val filteredTasks = { tasks.filter { it.readyToActivate } }
val tasksInRoll = when (settings.singleTask.currentTaskTakePartInRoll) {
true -> filteredTasks() + currentTask.value
false -> filteredTasks()
}
val newTask = tasksInRoll.shuffled().randomOrNull()
val oldTask = currentTask.value
if (newTask == null) {
setMessage(R.string.message_roll_not_find_task)
} else if (oldTask != null) {
newTask.apply { single.dateActivation = oldTask.single.dateActivation }.update()
oldTask.apply {
single.dateActivation = MyCalendar()
single.rolls++
}.update()
settings.removePoints(settings.singleTask.pointsForRoll)
destroyActionMode()
}
}

private fun doneSingleTask() {
//        currentTask.value?.delete()
currentTask.value?.apply { single.dateActivation = MyCalendar() }?.update() // FIXME: Del
if (settings.singleTask.rewards) {
settings.addPoints(settings.singleTask.pointsForTask)
}
destroyActionMode()
}

private fun Settings.addPoints(points: Int) = apply { singleTask.points += points }.update()
private fun Settings.removePoints(points: Int) = apply { singleTask.points -= points }.update()

private fun getTimesToPostpone(
pointsForTask: Int,
points: Int = settings.singleTask.points,
startValue: Int = 1
): List<String> = listOf("${startValue * pointsForTask}ч") + when {
points > 1 -> getTimesToPostpone(pointsForTask, points - 1, startValue + 1)
else -> emptyList()
}

private fun setActionMode() {
_showActionMode.value = Event(true)
isActionMode = true
}

fun destroyActionMode() {
if (isActionMode) {
isActionMode = false
currentTask.value = null
_hideActionMode.value = Event(true)
}
}

private fun needToActivateSingleTasks(tasks: List<Task>, date: MyCalendar) =
date < MyCalendar().now() && tasks.any { it.readyToActivate }

private fun Task.position() = shownSingleTasks.value?.indexOf(this) ?: -1

private fun Task.update() = viewModelScope.launch { repo.updateTask(this@update) }
private fun List<Task>.update() = viewModelScope.launch { repo.updateTasks(this@update) }
private fun Task.delete() = viewModelScope.launch { repo.deleteTask(this@delete) }
private fun Settings.update() = viewModelScope.launch { repo.updateSettings(this@update) }
}

 */