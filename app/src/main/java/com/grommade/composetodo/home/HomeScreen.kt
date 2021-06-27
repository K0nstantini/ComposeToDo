package com.grommade.composetodo.home

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.grommade.composetodo.R
import com.grommade.composetodo.add_classes.MyCalendar
import com.grommade.composetodo.ui.components.BuiltSimpleOkCancelDialog
import com.grommade.composetodo.ui.components.TopBarActionMode
import com.vanpra.composematerialdialogs.MaterialDialog

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    openDrawer: () -> Unit,
) {

    with(viewModel) {
        HomeScreenBody(
            tasksItems = tasksItems.collectAsState().value.sortedBy { it.deadline.milli },
            actionMode = actionMode.collectAsState().value,
            actionTitle = actionTitle.collectAsState().value,
            onTaskLongClicked = ::onTaskLongClicked,
            refreshTasks = ::refreshTasks,
            deactivateTasks = ::deactivateTasks,
            closeActionMode = ::closeActionMode,
            taskDone = ::onTaskDoneClicked,
            openDrawer = openDrawer
        )
    }
}

@Composable
private fun HomeScreenBody(
    tasksItems: List<HomeViewModel.HomeTaskItem>,
    actionMode: Boolean = false,
    actionTitle: String = "",
    onTaskLongClicked: (Long) -> Unit = {},
    refreshTasks: () -> Unit = {},
    deactivateTasks: () -> Unit = {},
    closeActionMode: () -> Unit = {},
    taskDone: () -> Unit = {},
    openDrawer: () -> Unit = {},
) {
    Scaffold(
        topBar = {
            if (actionMode) {
                TopBarActionMode(
                    title = actionTitle,
                    actions = actionsTopBar(
                        taskDone = taskDone
                    ),
                    closeActionMode = closeActionMode
                )
            } else {
                TopBar(
                    refreshTasks = refreshTasks,
                    deactivateTasks = deactivateTasks,
                    openDrawer = openDrawer
                )
            }
        }
    ) {
        LazyColumn(
            modifier = Modifier.padding(start = 16.dp, end = 8.dp),
            contentPadding = PaddingValues(top = 8.dp)
        ) {
            items(tasksItems, key = { task -> task.id }) { task ->
                TaskItem(
                    taskID = task.id,
                    taskName = task.name,
                    deadline = stringResource(R.string.main_screen_single_task_second_text, task.deadline.toString()),
                    selected = task.selected,
                    onTaskLongClicked = onTaskLongClicked
                )
            }
        }
    }
}

@Composable
private fun TopBar(
    refreshTasks: () -> Unit,
    deactivateTasks: () -> Unit,
    openDrawer: () -> Unit
) {
    TopAppBar(
        title = { Text(stringResource(R.string.app_name)) },
        navigationIcon = {
            IconButton(onClick = openDrawer) {
                Icon(Icons.Filled.Menu, "")
            }
        },
        actions = {
            IconButton(onClick = deactivateTasks) {
                Icon(Icons.Filled.Clear, "")
            }
            IconButton(onClick = refreshTasks) {
                Icon(Icons.Filled.Refresh, "")
            }
        }
    )
}

@Composable
private fun actionsTopBar(
    taskDone: () -> Unit
): @Composable RowScope.() -> Unit = {
    val taskDoneDialog = remember { MaterialDialog() }.apply {
        BuiltSimpleOkCancelDialog(
            title = stringResource(R.string.alert_title_single_task_done),
            onClick = taskDone
        )
    }

    IconButton(onClick = { taskDoneDialog.show() }) {
        Icon(Icons.Filled.Done, "")
    }
}


@Composable
private fun TaskItem(
    taskID: Long,
    taskName: String,
    deadline: String,
    selected: Boolean,
    onTaskLongClicked: (Long) -> Unit
) {
    val backgroundColor = when (selected) {
        true -> MaterialTheme.colors.secondaryVariant
        false -> Color.Transparent
    }
    Surface(
        color = backgroundColor,
        modifier = Modifier
            .padding(top = 8.dp, bottom = 8.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = { onTaskLongClicked(taskID) },
                )
            }
            .fillMaxWidth()
    ) {
        Column {
            Text(taskName, style = MaterialTheme.typography.body2.copy(fontWeight = FontWeight.Bold))
            Text(deadline, style = MaterialTheme.typography.subtitle2.copy(fontSize = 12.sp, color = Color.Gray))
        }
    }
    Divider()
}


@Preview
@Composable
fun HomeScreenPreview() {
    HomeScreenBody(
        tasksItems = listOf(
            HomeViewModel.HomeTaskItem(
                id = 1,
                name = "Task-1",
                deadline = MyCalendar.now(),
                selected = false
            ),
            HomeViewModel.HomeTaskItem(
                id = 2,
                name = "Task-2",
                deadline = MyCalendar.now().addHours(12),
                selected = true
            )
        )
    )
}