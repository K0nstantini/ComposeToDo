package com.grommade.composetodo.ui_task_list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.outlined.Task
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.grommade.composetodo.R
import com.grommade.composetodo.data.entity.Task
import com.grommade.composetodo.enums.TypeTask
import com.grommade.composetodo.ui.common.rememberFlowWithLifecycle
import com.grommade.composetodo.ui.components.*
import com.grommade.composetodo.util.extensions.toAddEditTask
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.title

@Composable
fun TaskListUi(navController: NavHostController) {

    TaskListUi(
        viewModel = hiltViewModel(),
        navController = navController
    )
}

@Composable
fun TaskListUi(
    viewModel: TaskListViewModel,
    navController: NavHostController
) {
    val viewState by rememberFlowWithLifecycle(viewModel.state)
        .collectAsState(initial = TaskListViewState.Empty)

    TaskListUi(viewState) { action ->
        when (action) {
            is TaskListActions.OpenTask -> navController.toAddEditTask(action.type, action.id)
            is TaskListActions.NewTask -> navController.toAddEditTask(action.type)
            TaskListActions.Back -> navController.navigateUp()
            else -> viewModel.submitAction(action)
        }
    }
}


@Composable
fun TaskListUi(
    viewState: TaskListViewState,
    actioner: (TaskListActions) -> Unit
) {
    val selected = remember { SnapshotStateMap<Long, Boolean>() }
    val selectedTasks = viewState.tasks.filter { task -> task.id in selected.filter { it.value }.keys }

    Scaffold(
        topBar = {
            when (selectedTasks.isNotEmpty()) {
                true -> {
                    TopBarActionModeBody(
                        title = selectedTasks.count().toString(),
                        showDoneActionMenu = selectedTasks.count() == 1 && !selectedTasks.first().group,
                        hasGroups = selectedTasks.any { it.group },
                        clearSelected = selected::clear,
                        performTask = {
                            selectedTasks.firstOrNull()?.let { actioner(TaskListActions.PerformTask(it)) }
                        },
                        deleteTasks = {
                            actioner(TaskListActions.DeleteTasks(selectedTasks))
                            selected.clear()
                        }
                    )
                }
                false -> TopBarDefault(
                    populateDBWithTasks = { actioner(TaskListActions.PopulateDBWithTasks) },
                    onBack = { actioner(TaskListActions.Back) }
                )
            }
        },
        floatingActionButton = {
            if (selectedTasks.isEmpty()) {
                AddTaskButton(actioner)
            }
        }

    ) {
        TaskListScrollingContent(
            tasks = viewState.tasks,
            selected = selected,
            actioner = actioner
        )
    }
}

@Composable
private fun TopBarDefault(
    populateDBWithTasks: () -> Unit,
    onBack: () -> Unit
) {
    TopAppBar(
        title = { Text(stringResource(R.string.title_single_task_list)) },
        navigationIcon = { NavigationBackIcon(onBack) },
        actions = { DropdownMenu(populateDBWithTasks) }
    )
}

@Composable
fun TopBarActionModeBody(
    title: String,
    showDoneActionMenu: Boolean,
    hasGroups: Boolean,
    clearSelected: () -> Unit,
    performTask: () -> Unit,
    deleteTasks: () -> Unit,
) {
    val taskDoneDialog = remember { MaterialDialog() }.apply {
        BuiltSimpleOkCancelDialog(
            title = stringResource(R.string.alert_title_single_task_done),
            callback = performTask
        )
    }
    val taskDelDialog = remember { MaterialDialog() }.apply {
        BuiltSimpleOkCancelDialog(
            title = stringResource(R.string.alert_title_delete_task),
            message = if (hasGroups) stringResource(R.string.alert_message_delete_group_task) else "",
            callback = deleteTasks
        )
    }

    TopAppBar(
        title = { Text(title) },
        navigationIcon = { NavigationCloseIcon(clearSelected) },
        backgroundColor = MaterialTheme.colors.onSecondary,
        contentColor = MaterialTheme.colors.onPrimary,
        actions = {
            if (showDoneActionMenu) {
                DoneIcon(taskDoneDialog::show)
            }
            DeleteIcon(taskDelDialog::show)
        }
    )
}

@Composable
fun DropdownMenu(populateDBWithTasks: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        MoreVertIcon { expanded = true }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(onClick = {
                expanded = false
                populateDBWithTasks()
            }) {
                Text("Заполнить")
            }
        }
    }
}

@Composable
fun AddTaskButton(
    actioner: (TaskListActions) -> Unit
) {
    val typeTaskDialog = typeTaskDialog(actioner)

    FloatingActionButton(onClick = typeTaskDialog::show) {
        Icon(Icons.Filled.Add, "")
    }
}

@Composable
private fun typeTaskDialog(
    actioner: (TaskListActions) -> Unit
): MaterialDialog = remember { MaterialDialog() }.apply {
    build {
        title("Тип задачи")
        Column(modifier = Modifier.padding(start = 24.dp)) {
            Text("Разовые", color = Color.Blue)
            TypeTaskItem(TypeTask.IMPORTANT, actioner)
            TypeTaskItem(TypeTask.UNIMPORTANT, actioner)
            Text("Разовые", color = Color.Blue)
            TypeTaskItem(TypeTask.LONG_REGULAR_TASK, actioner)
            TypeTaskItem(TypeTask.SHORT_REGULAR_TASK, actioner)
            TypeTaskItem(TypeTask.CONTAINER_TASK, actioner)
        }
    }
}

@Composable
fun MaterialDialog.TypeTaskItem(
    type: TypeTask,
    actioner: (TaskListActions) -> Unit
) {
    Text(
        text = stringResource(type.title),
        color = MaterialTheme.colors.onSurface,
        style = MaterialTheme.typography.body1,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                onClick = {
                    hide()
                    actioner(TaskListActions.NewTask(type))
                }
            )
            .padding(top = 12.dp, bottom = 12.dp, start = 24.dp, end = 24.dp)
            .wrapContentWidth(Alignment.Start)
    )
}

@Composable
fun TaskListScrollingContent(
    tasks: List<Task>,
    selected: SnapshotStateMap<Long, Boolean>,
    actioner: (TaskListActions) -> Unit
) {
    val markItem = { id: Long ->
        selected[id] = !(selected.getOrDefault(id, false))
    }

    LazyColumn(
        contentPadding = PaddingValues(top = 8.dp)
    ) {
        items(tasks, key = { task -> task.id }) { task ->
            TaskItem(
                id = task.id,
                name = task.name,
                group = task.group,
                groupOpen = task.groupOpen,
                level = task.getLevel(tasks),
                onClick = {
                    when (selected.containsValue(true)) {
                        true -> markItem(task.id)
                        false -> actioner(TaskListActions.OpenTask(task.type, task.id))
                    }
                },
                onGroupClicked = {
                    when (selected.containsValue(true)) {
                        true -> markItem(task.id)
                        false -> actioner(TaskListActions.OpenCloseGroup(task))
                    }
                },
                selectedItem = selected[task.id] == true,
                markItem = markItem
            )
        }
    }
}

@Composable
private fun TaskItem(
    id: Long,
    name: String,
    group: Boolean,
    groupOpen: Boolean,
    selectedItem: Boolean,
    level: Int,
    onClick: () -> Unit,
    onGroupClicked: () -> Unit,
    markItem: (Long) -> Unit,
) {

    val icon = when {
        groupOpen -> Icons.Filled.FolderOpen
        group -> Icons.Filled.Folder
        else -> Icons.Outlined.Task
    }

    Row(
        modifier = Modifier
            .background(if (selectedItem) Color.Gray else Color.Transparent)
            .padding(
                horizontal = (16 + level * 4).dp,
                vertical = 4.dp
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onGroupClicked) {
            Icon(icon, "")
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = name,
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures(
                        onLongPress = { markItem(id) },
                        onTap = { onClick() },
                    )
                },

            style = MaterialTheme.typography.body1.copy(
                fontSize = ((16 - level * 2).coerceIn(8..16)).sp,
                fontWeight = if (group) FontWeight.Bold else FontWeight.Normal
            ),
        )
    }
}


@Preview
@Composable
fun TaskListScreenPreview() {
//    TaskItem()
}