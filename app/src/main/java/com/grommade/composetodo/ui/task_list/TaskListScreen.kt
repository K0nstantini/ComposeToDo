package com.grommade.composetodo.ui.task_list

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
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
import com.grommade.composetodo.add_classes.TaskItem

@Composable
fun TaskListScreen(
    onBack: () -> Unit,
    viewModel: TaskListViewModel = hiltViewModel()
) {
    TaskListBody(
        title = stringResource(viewModel.title),
        actionMode = viewModel.actionMode.collectAsState().value,
        tasks = viewModel.shownTasks.collectAsState().value,
        onTaskClicked = viewModel::onTaskClicked,
        onTaskLongClicked = viewModel::onTaskLongClicked,
        closeActionMode = viewModel::closeActionMode,
        editTask = viewModel::onEditClicked,
        delTask = viewModel::onDeleteClicked,
        onBack = onBack,
    )
}

@Composable
private fun TaskListBody(
    title: String,
    actionMode: Boolean,
    tasks: List<TaskItem>,
    onTaskClicked: (Long) -> Unit,
    onTaskLongClicked: (Long) -> Unit,
    closeActionMode: () -> Unit,
    editTask: () -> Unit,
    delTask: () -> Unit,
    onBack: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopBar(
                title = title,
                actionMode = actionMode,
                closeActionMode = closeActionMode,
                editTask = editTask,
                delTask = delTask,
                onBack = onBack
            )
        },
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
    actionMode: Boolean,
    closeActionMode: () -> Unit,
    editTask: () -> Unit,
    delTask: () -> Unit,
    onBack: () -> Unit
) {
    when (actionMode) {
        true -> TopBarActionModeBody(
            title = title,
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
fun TopBarDefaultBody(
    title: String,
    onBack: () -> Unit
) {
    TopAppBar(
        title = { Text(title) },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.Filled.ArrowBack, contentDescription = null)
            }
        }
    )
}

@Composable
fun TopBarActionModeBody(
    title: String,
    closeActionMode: () -> Unit,
    editTask: () -> Unit,
    delTask: () -> Unit,
) {
    TopAppBar(
        title = { Text(title) },
        navigationIcon = {
            IconButton(onClick = closeActionMode) {
                Icon(Icons.Filled.Close, contentDescription = null)
            }
        },
        backgroundColor = MaterialTheme.colors.onSecondary,
        contentColor = MaterialTheme.colors.onPrimary,
        actions = {
            IconButton(onClick = editTask) {
                Icon(Icons.Filled.Edit, contentDescription = null)
            }
            IconButton(onClick = delTask) {
                Icon(Icons.Filled.Delete, contentDescription = null)
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
//            .selectable(true, true)
            .background(backgroundColor)
            .padding(
                horizontal = taskItem.padding.dp,
                vertical = 8.dp
            ),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Icon(taskItem.icon, contentDescription = null)
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
    TaskListScreen({})
}