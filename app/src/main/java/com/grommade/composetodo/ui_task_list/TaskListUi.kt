package com.grommade.composetodo.ui_task_list

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Task
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.grommade.composetodo.R
import com.grommade.composetodo.TasksRoute
import com.grommade.composetodo.data.entity.Task
import com.grommade.composetodo.enums.ModeTaskList
import com.grommade.composetodo.enums.TypeTask
import com.grommade.composetodo.ui.common.rememberFlowWithLifecycle
import com.grommade.composetodo.ui.components.BuiltSimpleOkCancelDialog
import com.grommade.composetodo.ui.components.NavigationBackIcon
import com.grommade.composetodo.util.Keys
import com.vanpra.composematerialdialogs.MaterialDialog

@Composable
fun TaskListUi(navController: NavHostController) {

    TaskListUi(
        viewModel = hiltViewModel(),
        navController = navController
    )

    /**

    taskDone = ::onTaskDoneClicked,
    taskDel = ::onDeleteClicked,
    }*/

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
            is TaskListActions.OpenCloseGroup -> viewModel.openCloseGroup(action.task)
            is TaskListActions.OpenTask -> navController.navigate(
                when (viewModel.taskType) {
                    TypeTask.REGULAR_TASK -> TasksRoute.RegularTaskChildRoute.createRoute(action.id)
                    TypeTask.SINGLE_TASK -> TasksRoute.SingleTaskChildRoute.createRoute(action.id)
                }
            )
            is TaskListActions.BackWithID -> {
                navController.apply {
                    previousBackStackEntry?.savedStateHandle?.set(Keys.SELECTED_TASK_ID, action.id)
                    navigateUp()
                }
            }
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
    val selected = remember { SnapshotStateMap<Task, Boolean>() }
    val hasSelected = selected.containsValue(true)

    Scaffold(
        topBar = {
            when (hasSelected) {
                true -> {
                    val oneSelectNoGroup =
                        selected.filter { it.value }.let { it.count() == 1 && !it.keys.first().group }
                    TopBarActionModeBody(
                        title = selected.values.filter { it }.count().toString(),
                        showDoneActionMenu = oneSelectNoGroup,
                        clearSelected = selected::clear,
                    )
                }
                false -> TopBarDefault(
                    populateDBWithTasks = { actioner(TaskListActions.PopulateDBWithTasks) },
                    showOkSelectBtn = viewState.mode == ModeTaskList.SELECT_CATALOG,
                    enabledDoneBtn = hasSelected,
                    onBackWithID = { actioner(TaskListActions.BackWithID(-1)) }, // FIXME
                    onBack = { actioner(TaskListActions.Back) }
                )
            }
        },
        floatingActionButton = {
            if (viewState.mode.showAddBtn && !hasSelected) {
                FloatingActionButton(onClick = { actioner(TaskListActions.OpenTask(-1)) }) {
                    Icon(Icons.Filled.Add, "")
                }
            }
        }

    ) {
        TaskListScrollingContent(
            tasks = viewState.tasks,
            selected = selected,
            selectCatalog = viewState.mode == ModeTaskList.SELECT_CATALOG,
            actioner = actioner
        )
    }
}

@Composable
private fun TopBarDefault(
    populateDBWithTasks: () -> Unit,
    showOkSelectBtn: Boolean,
    enabledDoneBtn: Boolean,
    onBackWithID: () -> Unit,
    onBack: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(R.string.title_single_task_list),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = { NavigationBackIcon(onBack) },
        actions = {
            if (showOkSelectBtn) {
                IconButton(onClick = onBackWithID, enabled = enabledDoneBtn) {
                    Icon(Icons.Filled.Done, "")
                }
            } else {
                DropdownMenu(populateDBWithTasks)
            }
        }
    )
}

@Composable
fun DropdownMenu(
    populateDBWithTasks: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        IconButton(onClick = { expanded = true }) {
            Icon(Icons.Default.MoreVert, "")
        }
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
fun TopBarActionModeBody(
    title: String,
    showDoneActionMenu: Boolean,
    clearSelected: () -> Unit,
//    group: Boolean,
//    closeActionMode: () -> Unit,
//    taskDone: () -> Unit,
//    taskEdit: () -> Unit,
//    taskDel: () -> Unit,
) {
    val taskDoneDialog = remember { MaterialDialog() }.apply {
        BuiltSimpleOkCancelDialog(
            title = stringResource(R.string.alert_title_single_task_done),
            callback = {}
        )
    }
//    val taskDelDialog = remember { MaterialDialog() }.apply {
//        BuiltSimpleOkCancelDialog(
//            title = stringResource(R.string.alert_title_delete_task),
//            message = if (group) stringResource(R.string.alert_message_delete_group_task) else "",
//            callback = taskDel
//        )
//    }

    TopAppBar(
        title = { Text(title, maxLines = 1, overflow = TextOverflow.Ellipsis) },
        navigationIcon = {
            IconButton(onClick = clearSelected) {
                Icon(Icons.Filled.Close, "")
            }
        },
        backgroundColor = MaterialTheme.colors.onSecondary,
        contentColor = MaterialTheme.colors.onPrimary,
        actions = {
            if (showDoneActionMenu) {
                IconButton(onClick = taskDoneDialog::show) {
                    Icon(Icons.Filled.Done, "")
                }
            }
////            IconButton(onClick = taskDelDialog::show) {
////                Icon(Icons.Filled.Delete, "")
////            }
        }
    )
}

@Composable
fun TaskListScrollingContent(
    tasks: List<Task>,
    selected: SnapshotStateMap<Task, Boolean>,
    selectCatalog: Boolean,
    actioner: (TaskListActions) -> Unit
) {
    val markItem = { task: Task -> selected[task] = !(selected[task] ?: false) }

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
                selectCatalog = selectCatalog,
                openTask = { actioner(TaskListActions.OpenTask(task.id)) },
                onGroupClicked = { actioner(TaskListActions.OpenCloseGroup(task)) },
                selected = selected,
                backgroundColor = if (selected[task] == true) Color.Gray else Color.Transparent,
                markItem = { markItem(task) }
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
    level: Int,
    selectCatalog: Boolean,
    selected: SnapshotStateMap<Task, Boolean>,
    backgroundColor: Color,
    openTask: () -> Unit,
    onGroupClicked: () -> Unit,
    markItem: () -> Unit,
) {

//    val backgroundColor = when (selected[id] ?: false) {
//        true -> Color.Gray
//        false -> Color.Transparent
//    }
    val icon = when {
        groupOpen -> Icons.Filled.FolderOpen
        group -> Icons.Filled.Folder
        else -> Icons.Outlined.Task
    }

//    val markItem = { selected[id] = !(selected[id] ?: false) }
    val onClick = {
        when (selected.values.any { it } || selectCatalog) {
            true -> markItem()
            false -> openTask()
        }
    }

    Row(
        modifier = Modifier
            .background(backgroundColor)
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
                        onLongPress = { markItem() },
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