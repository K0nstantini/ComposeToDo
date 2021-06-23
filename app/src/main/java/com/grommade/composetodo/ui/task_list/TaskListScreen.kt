package com.grommade.composetodo.ui.task_list

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.grommade.composetodo.add_classes.TaskItem

@Composable
fun TaskListScreen(
    viewModel: TaskListViewModel = hiltViewModel(),
    navController: NavHostController = rememberNavController(),
    onBack: () -> Unit
) {
    with(viewModel) {
        val onAddEditClicked: () -> Unit = {
            navController.navigate(navigateToAddEditTask)
            closeActionMode() // FIXME
        }
        TaskListBody(
            title = titleActionMode.collectAsState().value ?: stringResource(title),
            actionMode = actionMode.collectAsState().value,
            tasks = shownTasks.collectAsState().value,
            showAddBtn = showAddButton.collectAsState().value,
            showDoneActionMenu = showDoneActionMenu.collectAsState().value,
            showEditActionMenu = showEditActionMenu.collectAsState().value,
            onTaskClicked = ::onTaskClicked,
            onTaskLongClicked = ::onTaskLongClicked,
            closeActionMode = ::closeActionMode,
            addEditTask = onAddEditClicked,
            delTask = ::onDeleteClicked,
            onBack = onBack,
        )
    }
}

@Composable
private fun TaskListBody(
    title: String,
    actionMode: Boolean,
    tasks: List<TaskItem>,
    showAddBtn: Boolean,
    showDoneActionMenu: Boolean,
    showEditActionMenu: Boolean,
    onTaskClicked: (Long) -> Unit,
    onTaskLongClicked: (Long) -> Unit,
    closeActionMode: () -> Unit,
    addEditTask: () -> Unit,
    delTask: () -> Unit,
    onBack: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopBar(
                title = title,
                showDoneActionMenu = showDoneActionMenu,
                showEditActionMenu = showEditActionMenu,
                actionMode = actionMode,
                closeActionMode = closeActionMode,
                editTask = addEditTask,
                delTask = delTask,
                onBack = onBack
            )
        },
        floatingActionButton = {
            if (showAddBtn) {
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
    showDoneActionMenu: Boolean,
    showEditActionMenu: Boolean,
    actionMode: Boolean,
    closeActionMode: () -> Unit,
    editTask: () -> Unit,
    delTask: () -> Unit,
    onBack: () -> Unit
) {
    when (actionMode) {
        true -> TopBarActionModeBody(
            title = title,
            showDoneActionMenu = showDoneActionMenu,
            showEditActionMenu = showEditActionMenu,
            closeActionMode = closeActionMode,
            editTask = editTask,
            delTask = delTask
        )
        false -> TopBarDefaultBody(
            title = title,
            onBack = onBack
        )
    }
}

@Composable
private fun TopBarDefaultBody(
    title: String,
    onBack: () -> Unit
) {
    TopAppBar(
        title = { Text(title) },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.Filled.ArrowBack, "")
            }
        }
    )
}

@Composable
private fun TopBarActionModeBody(
    title: String,
    showDoneActionMenu: Boolean,
    showEditActionMenu: Boolean,
    closeActionMode: () -> Unit,
    editTask: () -> Unit,
    delTask: () -> Unit,
) {
    TopAppBar(
        title = { Text(title) },
        navigationIcon = {
            IconButton(onClick = closeActionMode) {
                Icon(Icons.Filled.Close, "")
            }
        },
        backgroundColor = MaterialTheme.colors.onSecondary,
        contentColor = MaterialTheme.colors.onPrimary,
        actions = {
            if (showDoneActionMenu) {
                IconButton(onClick = { /** TODO */ }) {
                    Icon(Icons.Filled.Done, "")
                }
            }
            if (showEditActionMenu) {
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
    TaskListScreen() {}
}