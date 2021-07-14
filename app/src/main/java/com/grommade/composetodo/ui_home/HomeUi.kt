package com.grommade.composetodo.ui_home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.grommade.composetodo.R
import com.grommade.composetodo.add_classes.MyCalendar
import com.grommade.composetodo.data.entity.RandomTask
import com.grommade.composetodo.ui.common.rememberFlowWithLifecycle

@Composable
fun HomeUi(openDrawer: () -> Unit) {
    HomeUi(
        viewModel = hiltViewModel(),
        openDrawer = openDrawer
    )
}

@Composable
private fun HomeUi(
    viewModel: HomeViewModel,
    openDrawer: () -> Unit
) {
    val viewState by rememberFlowWithLifecycle(viewModel.state)
        .collectAsState(initial = HomeViewState.Empty)

    HomeUi(viewState) { action ->
        when (action) {
            HomeActions.OpenDrawer -> openDrawer()
            else -> viewModel.submitAction(action)
        }
    }
}

@Composable
fun HomeUi(
    viewState: HomeViewState,
    actioner: (HomeActions) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                navigationIcon = {
                    IconButton(onClick = { actioner(HomeActions.OpenDrawer) }) {
                        Icon(Icons.Filled.Menu, "")
                    }
                },
                actions = {
                    IconButton(onClick = { actioner(HomeActions.ClearStateTasks) }) {
                        Icon(Icons.Filled.Clear, "")
                    }
                    IconButton(onClick = { actioner(HomeActions.Refresh) }) {
                        Icon(Icons.Filled.Refresh, "")
                    }
                }
            )
        }
    ) {
        HomeScrollingContent(viewState.tasks, actioner)
    }
}

@Composable
fun HomeScrollingContent(
    tasks: List<RandomTask>,
    actioner: (HomeActions) -> Unit
) {
    LazyColumn(
        modifier = Modifier.padding(start = 16.dp, end = 8.dp),
        contentPadding = PaddingValues(top = 8.dp)
    ) {
        items(tasks, key = { task -> task.id }) { task ->
            TaskItem(
                name = task.name,
                deadline = task.deadlineDate,
                markTask = { actioner(HomeActions.MarkTaskDone(task)) }
            )
        }
    }
}

@Composable
fun TaskItem(
    name: String,
    deadline: MyCalendar,
    markTask: () -> Unit
) {
    Surface(
        modifier = Modifier
            .padding(top = 8.dp, bottom = 8.dp)
            .fillMaxWidth()
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(name, style = MaterialTheme.typography.body2.copy(fontWeight = FontWeight.Bold))
                Text(
                    text = stringResource(R.string.main_screen_single_task_second_text, deadline.toString()),
                    style = MaterialTheme.typography.subtitle2.copy(fontSize = 12.sp, color = Color.Gray)
                )
            }
            var checkedState by remember { mutableStateOf(false) }
            Checkbox(
                checked = checkedState,
                onCheckedChange = {
                    checkedState = true
                    markTask()
                }
            )
        }
    }
    Divider()
}

@Preview
@Composable
fun HomeScreenPreview() {
    TaskItem(
        name = "Задача",
        deadline = MyCalendar.now(),
        markTask = {}
    )
}