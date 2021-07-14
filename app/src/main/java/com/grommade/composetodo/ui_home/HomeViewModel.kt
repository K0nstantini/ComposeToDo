package com.grommade.composetodo.ui_home

import androidx.lifecycle.viewModelScope
import com.grommade.composetodo.add_classes.BaseViewModel
import com.grommade.composetodo.add_classes.MyCalendar
import com.grommade.composetodo.data.entity.Settings
import com.grommade.composetodo.data.entity.RandomTask
import com.grommade.composetodo.data.repos.RepoSettings
import com.grommade.composetodo.data.repos.RepoTask
import com.grommade.composetodo.use_cases.GenerateSingleTasks
import com.grommade.composetodo.use_cases.PerformSingleTask
import com.grommade.composetodo.util.extensions.change
import com.grommade.composetodo.util.extensions.singleSet
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repoSettings: RepoSettings,
    private val repoSingleTask: RepoTask,
    private val performSingleTask: PerformSingleTask,
    private val generateSingleTasks: GenerateSingleTasks
) : BaseViewModel() {

    private val pendingAction = MutableSharedFlow<HomeActions>()

    val state = repoSingleTask.activeTasks.map { tasks ->
        HomeViewState(tasks.sortedBy { it.deadlineDate })
    }

    private val settings: StateFlow<Settings> = repoSettings.settingsFlow.asState(Settings())

    init {
        viewModelScope.launch {
            pendingAction.collect { action ->
                when (action) {
                    is HomeActions.MarkTaskDone -> onMarkTaskDoneClicked(action.task)
                    HomeActions.Refresh -> refreshTasks()
                    HomeActions.ClearStateTasks -> clearStateTasks()
                    else -> {

                    }
                }
            }
        }
    }

    private fun refreshTasks() = viewModelScope.launch {
        if (repoSettings.getCountSettings() == 1) { // FIXME: WTF?
            generateSingleTasks()
        }
    }

    private fun clearStateTasks() = viewModelScope.launch {
        settings.value
            .change { set: singleSet -> set.copy(lastGeneration = MyCalendar()) }
            .save()
        val tasks = repoSingleTask.getAllTasks()
        tasks.filter { it.dateActivation.isNoEmpty() }.forEach { task ->
            task.copy(dateActivation = MyCalendar()).save()
        }
        tasks.filter { it.single.rolls > 0 }.forEach { task ->
            task.copy(single = task.single.copy(rolls = 0)).save()
        }
    }

    private fun onMarkTaskDoneClicked(task: RandomTask) {
        viewModelScope.launch {
            delay(500)
            performSingleTask(task)
        }
    }

    fun submitAction(action: HomeActions) {
        viewModelScope.launch { pendingAction.emit(action) }
    }

    private fun RandomTask.save() = viewModelScope.launch { repoSingleTask.saveTask(this@save) }
    private fun Settings.save() = viewModelScope.launch { repoSettings.updateSettings(this@save) }

}

/**


private val sPostponeCurrentTask get() = settings.singleTask.postponeCurrentTaskForOnePoint
private val sPostponeNextTask get() = settings.singleTask.postponeNextTaskForOnePoint


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