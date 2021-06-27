package com.grommade.composetodo.task_list

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.grommade.composetodo.R
import com.grommade.composetodo.add_classes.TaskItem
import com.grommade.composetodo.ui.components.BuiltSimpleOkCancelDialog
import com.grommade.composetodo.util.Keys
import com.vanpra.composematerialdialogs.MaterialDialog

@Composable
fun TaskListScreen(
    viewModel: TaskListViewModel = hiltViewModel(),
    navController: NavHostController = rememberNavController()
) {
    val onBack: () -> Unit = { navController.navigateUp() }

    with(viewModel) {
        val addEditTask = {
            navController.navigate(routToAddEditTask)
            closeActionMode()
        }
        val onBackWithID = {
            navController.previousBackStackEntry?.savedStateHandle?.set(Keys.SELECTED_TASK_ID, currentIDTask)
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
            addEditTask = addEditTask,
            delTask = ::onDeleteClicked,
            onBackWithID = onBackWithID,
            onBack = onBack,
        )
    }
}

@Composable
private fun TaskListBody(
    title: String,
    actionMode: Boolean,
    tasks: List<TaskItem>,
    availability: TaskListViewModel.Availability,
    onTaskClicked: (Long) -> Unit,
    onTaskLongClicked: (Long) -> Unit,
    closeActionMode: () -> Unit,
    taskDone: () -> Unit,
    addEditTask: () -> Unit,
    delTask: () -> Unit,
    onBackWithID: () -> Unit,
    onBack: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopBar(
                title = title,
                availability = availability,
                actionMode = actionMode,
                closeActionMode = closeActionMode,
                taskDone = taskDone,
                editTask = addEditTask,
                delTask = delTask,
                onBackWithID = onBackWithID,
                onBack = onBack
            )
        },
        floatingActionButton = {
            if (availability.showAddButton) {
                FloatingActionButton(onClick = addEditTask) {
                    Icon(Icons.Filled.Add, "")
                }
            }
        }

    ) {
        LazyColumn(
            contentPadding = PaddingValues(top = 8.dp)
        ) {
            items(tasks, key = { task -> task.id }) { task ->
                TaskItem(
                    taskItem = task,
                    onTaskClicked = onTaskClicked,
                    onTaskLongClicked = onTaskLongClicked
                )
            }
        }
    }
}


@Composable
private fun TopBar(
    title: String,
    availability: TaskListViewModel.Availability,
    actionMode: Boolean,
    closeActionMode: () -> Unit,
    taskDone: () -> Unit,
    editTask: () -> Unit,
    delTask: () -> Unit,
    onBackWithID: () -> Unit,
    onBack: () -> Unit
) {
    when (actionMode) {
        true -> TopBarActionModeBody(
            title = title,
            availability = availability,
            closeActionMode = closeActionMode,
            taskDone = taskDone,
            editTask = editTask,
            delTask = delTask
        )
        false -> TopBarDefaultBody(
            title = title,
            availability = availability,
            onBackWithID = onBackWithID,
            onBack = onBack
        )
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
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.Filled.ArrowBack, "")
            }
        },
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
    availability: TaskListViewModel.Availability,
    closeActionMode: () -> Unit,
    taskDone: () -> Unit,
    editTask: () -> Unit,
    delTask: () -> Unit,
) {
    val taskDoneDialog = remember { MaterialDialog() }.apply {
        BuiltSimpleOkCancelDialog(
            title = stringResource(R.string.alert_title_single_task_done),
            onClick = taskDone
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
                IconButton(onClick = editTask) {
                    Icon(Icons.Filled.Edit, "")
                }
            }
            IconButton(onClick = delTask) {
                Icon(Icons.Filled.Delete, "")
            }
        }
    )
}

@Composable
private fun TaskItem(
    taskItem: TaskItem,
    onTaskClicked: (Long) -> Unit,
    onTaskLongClicked: (Long) -> Unit
) {
    val backgroundColor = when (taskItem.isSelected) {
        true -> MaterialTheme.colors.secondaryVariant
        false -> Color.Transparent
    }
    Row(
        modifier = Modifier
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { onTaskClicked(taskItem.id) },
                    onLongPress = { onTaskLongClicked(taskItem.id) },
                )
            }
            .background(backgroundColor)
            .padding(
                horizontal = taskItem.padding.dp,
                vertical = 8.dp
            ),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Icon(taskItem.icon, "")
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = taskItem.name,
            style = MaterialTheme.typography.body1.copy(
                fontSize = taskItem.fontSize.sp,
                fontWeight = taskItem.fontWeight
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