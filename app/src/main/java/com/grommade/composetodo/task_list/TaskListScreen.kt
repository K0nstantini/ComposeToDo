package com.grommade.composetodo.task_list

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Task
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
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
import androidx.navigation.compose.rememberNavController
import com.grommade.composetodo.R
import com.grommade.composetodo.ui.components.BuiltSimpleOkCancelDialog
import com.grommade.composetodo.ui.components.NavigationBackIcon
import com.grommade.composetodo.util.Keys
import com.vanpra.composematerialdialogs.MaterialDialog

@Composable
fun TaskListScreen(
    viewModel: TaskListViewModel = hiltViewModel(),
    navController: NavHostController = rememberNavController()
) {
    val onBack: () -> Unit = { navController.navigateUp() }

    with(viewModel) {
        val taskAddEdit = {
            navController.navigate(routToAddEditTask)
            closeActionMode()
        }
        val onBackWithID = {
            navController.previousBackStackEntry?.savedStateHandle?.set(Keys.SELECTED_TASK_ID, currentTaskID)
            onBack()
        }
        TaskListBody(
            title = title.collectAsState().value ?: stringResource(defaultTitle),
            actionMode = actionMode.collectAsState().value,
            tasks = shownTasks.collectAsState().value,
            availability = availability.collectAsState().value,
            onTaskClicked = ::onTaskClicked,
            onTaskLongClicked = ::onTaskLongClicked,
            closeActionMode = ::closeActionMode,
            taskDone = ::onTaskDoneClicked,
            taskAddEdit = taskAddEdit,
            taskDel = ::onDeleteClicked,
            onBackWithID = onBackWithID,
            onBack = onBack,
        )
    }
}

@Composable
private fun TaskListBody(
    title: String,
    actionMode: Boolean,
    tasks: List<TaskListViewModel.TaskItem>,
    availability: TaskListViewModel.Availability,
    onTaskClicked: (Long) -> Unit,
    onTaskLongClicked: (Long) -> Unit,
    closeActionMode: () -> Unit,
    taskDone: () -> Unit,
    taskAddEdit: () -> Unit,
    taskDel: () -> Unit,
    onBackWithID: () -> Unit,
    onBack: () -> Unit,
) {
    Scaffold(
        topBar = {
            when (actionMode) {
                true -> TopBarActionModeBody(
                    title = title,
                    group = tasks.any { it.isSelected && it.task.group },
                    availability = availability,
                    closeActionMode = closeActionMode,
                    taskDone = taskDone,
                    taskEdit = taskAddEdit,
                    taskDel = taskDel
                )
                false -> TopBarDefaultBody(
                    title = title,
                    availability = availability,
                    onBackWithID = onBackWithID,
                    onBack = onBack
                )
            }
        },
        floatingActionButton = {
            if (availability.showAddButton) {
                FloatingActionButton(onClick = taskAddEdit) {
                    Icon(Icons.Filled.Add, "")
                }
            }
        }

    ) {
        LazyColumn(
            contentPadding = PaddingValues(top = 8.dp)
        ) {
            items(tasks, key = { item -> item.task.id }) { item ->
                TaskItem(
                    id = item.task.id,
                    name = item.task.name,
                    group = item.task.group,
                    groupOpen = item.task.groupOpen,
                    level = item.level,
                    isSelected = item.isSelected,
                    onTaskClicked = onTaskClicked,
                    onTaskLongClicked = onTaskLongClicked
                )
            }
        }
    }
}

@Composable
private fun TopBarDefaultBody(
    title: String,
    availability: TaskListViewModel.Availability,
    onBackWithID: () -> Unit,
    onBack: () -> Unit
) {
    TopAppBar(
        title = { Text(title, maxLines = 1, overflow = TextOverflow.Ellipsis) },
        navigationIcon = { NavigationBackIcon(onBack) },
        actions = {
            if (availability.showDoneActionMenu) {
                IconButton(onClick = onBackWithID, enabled = availability.enabledDoneBtn) {
                    Icon(Icons.Filled.Done, "")
                }
            }
        }
    )
}

@Composable
private fun TopBarActionModeBody(
    title: String,
    group: Boolean,
    availability: TaskListViewModel.Availability,
    closeActionMode: () -> Unit,
    taskDone: () -> Unit,
    taskEdit: () -> Unit,
    taskDel: () -> Unit,
) {
    val taskDoneDialog = remember { MaterialDialog() }.apply {
        BuiltSimpleOkCancelDialog(
            title = stringResource(R.string.alert_title_single_task_done),
            callback = taskDone
        )
    }
    val taskDelDialog = remember { MaterialDialog() }.apply {
        BuiltSimpleOkCancelDialog(
            title = stringResource(R.string.alert_title_delete_task),
            message = if (group) stringResource(R.string.alert_message_delete_group_task) else "",
            callback = taskDel
        )
    }

    TopAppBar(
        title = { Text(title, maxLines = 1, overflow = TextOverflow.Ellipsis) },
        navigationIcon = {
            IconButton(onClick = closeActionMode) {
                Icon(Icons.Filled.Close, "")
            }
        },
        backgroundColor = MaterialTheme.colors.onSecondary,
        contentColor = MaterialTheme.colors.onPrimary,
        actions = {
            if (availability.showDoneActionMenu) {
                IconButton(onClick = { taskDoneDialog.show() }) {
                    Icon(Icons.Filled.Done, "")
                }
            }
            if (availability.showEditActionMenu) {
                IconButton(onClick = taskEdit) {
                    Icon(Icons.Filled.Edit, "")
                }
            }
            IconButton(onClick = { taskDelDialog.show() }) {
                Icon(Icons.Filled.Delete, "")
            }
        }
    )
}

@Composable
private fun TaskItem(
    id: Long,
    name: String,
    group: Boolean,
    groupOpen: Boolean,
    level: Int,
    isSelected: Boolean,
    onTaskClicked: (Long) -> Unit,
    onTaskLongClicked: (Long) -> Unit
) {
    val backgroundColor = when (isSelected) {
        true -> MaterialTheme.colors.secondaryVariant
        false -> Color.Transparent
    }
    val icon = when {
        groupOpen -> Icons.Filled.FolderOpen
        group -> Icons.Filled.Folder
        else -> Icons.Outlined.Task
    }
    Row(
        modifier = Modifier
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { onTaskClicked(id) },
                    onLongPress = { onTaskLongClicked(id) },
                )
            }
            .background(backgroundColor)
            .padding(
                horizontal = (16 + level * 4).dp,
                vertical = 8.dp
            ),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Icon(icon, "")
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = name,
            style = MaterialTheme.typography.body1.copy(
                fontSize = ((16 - level * 2).coerceIn(8..16)).sp,
                fontWeight = if (group) FontWeight.Bold else FontWeight.Normal
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}


@Preview
@Composable
fun TaskListScreenPreview() {
    TaskListScreen()
}