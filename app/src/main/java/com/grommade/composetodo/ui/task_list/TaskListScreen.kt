package com.grommade.composetodo.ui.task_list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
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
    val title = stringResource(viewModel.title)
    val tasks by viewModel.shownTasks.collectAsState()

    TaskListBody(title, onBack, tasks)
}

@Composable
private fun TaskListBody(
    title: String,
    onBack: () -> Unit,
    tasks: List<TaskItem>
) {
    Scaffold(
        topBar = { TopBar(title, onBack) },
    ) {
        LazyColumn(
            contentPadding = PaddingValues(top = 8.dp)
        ) {
            items(tasks, key = { task -> task.id }) { task ->
                TaskItem(task)
            }
        }
    }
}


@Composable
private fun TopBar(
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
private fun TaskItem(taskItem: TaskItem) {
    Row(
        modifier = Modifier
            .clickable { /** TODO */ }
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
            )
        )
    }
}


@Preview
@Composable
fun TaskListScreenPreview() {
    TaskListScreen({})
}