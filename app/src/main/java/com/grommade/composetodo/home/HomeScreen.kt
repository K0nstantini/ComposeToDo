package com.grommade.composetodo.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.grommade.composetodo.R
import com.grommade.composetodo.add_classes.MyCalendar

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    openDrawer: () -> Unit,
) {

    with(viewModel) {
        HomeScreenBody(
            tasksItems = tasksItems.collectAsState().value.sortedBy { it.deadline.milli },
            refreshTasks = ::refreshTasks,
            deactivateTasks = ::deactivateTasks,
            openDrawer = openDrawer
        )
    }
}

@Composable
fun HomeScreenBody(
    tasksItems: List<HomeViewModel.HomeTaskItem>,
    refreshTasks: () -> Unit = {},
    deactivateTasks: () -> Unit = {},
    openDrawer: () -> Unit = {},
) {
    Scaffold(
        topBar = {
            TopBar(
                refreshTasks = refreshTasks,
                deactivateTasks = deactivateTasks,
                openDrawer = openDrawer
            )
        }
    ) {
        LazyColumn(
            modifier = Modifier.padding(start = 16.dp, end = 8.dp),
            contentPadding = PaddingValues(top = 8.dp)
        ) {
            items(tasksItems, key = { task -> task.id }) { task ->
                TaskItem(
                    taskName = task.name,
                    deadline = stringResource(R.string.main_screen_single_task_second_text, task.deadline.toString())
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
fun TaskItem(
    taskName: String,
    deadline: String
) {
    Surface(modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)) {
        Column() {
            Text(taskName, style = MaterialTheme.typography.body2)
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
                deadline = MyCalendar().now()
            ),
            HomeViewModel.HomeTaskItem(
                id = 2,
                name = "Task-2",
                deadline = MyCalendar().now().addHours(12)
            )
        )
    )
}