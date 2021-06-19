package com.grommade.composetodo.ui.task_list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.outlined.Task
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.grommade.composetodo.R
import com.grommade.composetodo.add_classes.TaskItem
import com.grommade.composetodo.db.entity.Task
import com.homemade.anothertodo.enums.TypeTask

@Composable
fun TaskListScreen(
    typeTask: TypeTask,
    onBack: () -> Unit,
    drawerGesturesEnabled: MutableState<Boolean> = remember { mutableStateOf(true) },
) {
    drawerGesturesEnabled.value = false

    val title = stringResource(
        when (typeTask) {
            TypeTask.REGULAR_TASK -> R.string.nav_regular_task_list
            TypeTask.SINGLE_TASK -> R.string.nav_single_task_list
        }
    )

    val items = generateTasks()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        },
    ) {
        LazyColumn(
            contentPadding = PaddingValues(top = 8.dp)
        ) {
            items(items) {
                TaskItem(it)
            }
        }
    }
}

@Composable
fun TaskItem(taskItem: TaskItem) {

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
            text = taskItem.task.name,
            style = MaterialTheme.typography.body1.copy(
                fontSize = taskItem.fontSize.sp,
                fontWeight = taskItem.fontWeight
            )
        )
    }
}

private fun generateTasks(): List<TaskItem> {
    val music = Task(
        id = 1,
        name = "Music",
        group = true
    )
    val playGuitar = Task(
        id = 12,
        name = "Play Guitar",
        parent = music.id
    )
    val physics = Task(
        id = 2,
        name = "Physics",
        group = true,
        groupOpen = true
    )
    val squads = Task(
        id = 21,
        name = "Squads",
        parent = physics.id
    )
    val bar = Task(
        id = 3,
        name = "Bar"
    )
    val tasks = listOf(music, playGuitar, physics, squads, bar)
    return tasks
        .map { task ->
            val level = task.getLevel(tasks)
            TaskItem(
                task = task,
                padding = 16 + level * 4,
                icon = when {
                    task.groupOpen -> Icons.Filled.FolderOpen
                    task.group -> Icons.Filled.Folder
                    else -> Icons.Outlined.Task
                },
                fontSize = (16 - level*2).coerceIn(8..16),
                fontWeight = if (task.group) FontWeight.Bold else FontWeight.Normal
            )
        }
}

@Preview
@Composable
fun TaskListScreenPreview() {
    TaskListScreen(TypeTask.SINGLE_TASK, {})
}