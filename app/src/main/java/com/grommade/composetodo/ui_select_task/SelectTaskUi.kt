package com.grommade.composetodo.ui_select_task

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.grommade.composetodo.R
import com.grommade.composetodo.data.entity.Task
import com.grommade.composetodo.ui.common.rememberFlowWithLifecycle
import com.grommade.composetodo.util.Keys

@Composable
fun SelectTaskUi(
    id: Long,
    navController: NavHostController
) {
    SelectTaskUi(
        viewModel = hiltViewModel(),
        id = id,
        navController = navController
    )
}

@Composable
fun SelectTaskUi(
    viewModel: SelectTaskViewModel,
    id: Long,
    navController: NavHostController
) {

    val viewState by rememberFlowWithLifecycle(viewModel.state)
        .collectAsState(initial = SelectTaskViewState.Empty)

    SelectTaskUi(viewState, id) { action ->
        when (action) {
            is SelectTaskActions.Confirmation -> {
                navController.previousBackStackEntry?.savedStateHandle?.set(Keys.SELECTED_TASK_ID, action.id)
                navController.navigateUp()
            }
            SelectTaskActions.Close -> navController.navigateUp()
        }
    }
}

@Composable
fun SelectTaskUi(
    viewState: SelectTaskViewState,
    id: Long,
    actioner: (SelectTaskActions) -> Unit
) {
    val (selected, setSelected) = remember { mutableStateOf(id) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.title_select_catalog)) },
                navigationIcon = {
                    IconButton(
                        onClick = { actioner(SelectTaskActions.Close) }
                    ) {
                        Icon(Icons.Filled.Close, "")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { actioner(SelectTaskActions.Confirmation(selected)) },
                        enabled = selected > 0
                    ) {
                        Icon(Icons.Filled.Done, "")
                    }
                }
            )
        },

        ) {
        TaskListScrollingContent(
            tasks = viewState.tasks,
            selected = selected,
            setSelected = setSelected,
        )
    }
}

@Composable
fun TaskListScrollingContent(
    tasks: List<Task>,
    selected: Long,
    setSelected: (Long) -> Unit,
) {
    LazyColumn(contentPadding = PaddingValues(top = 8.dp)) {
        items(tasks, key = { task -> task.id }) { task ->
            TaskItem(
                name = task.name,
                level = task.getLevel(tasks),
                selectedItem = selected == task.id,
                setSelected = { setSelected(task.id) }
            )
        }
    }
}

@Composable
private fun TaskItem(
    name: String,
    level: Int,
    selectedItem: Boolean,
    setSelected: () -> Unit,
) {

    Row(
        modifier = Modifier
            .background(if (selectedItem) Color.Gray else Color.Transparent)
            .padding(
                horizontal = (16 + level * 4).dp,
                vertical = 4.dp
            )
            .clickable(onClick = setSelected),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Icon(Icons.Filled.FolderOpen, "")
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = name,
            modifier = Modifier.fillMaxSize(),
            style = MaterialTheme.typography.body1.copy(
                fontSize = ((16 - level * 2).coerceIn(8..16)).sp,
                fontWeight = FontWeight.Bold
            ),
        )
    }
}