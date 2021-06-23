package com.grommade.composetodo.single_task

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.grommade.composetodo.R
import com.grommade.composetodo.settings.SettingItem

@Composable
fun SingleTaskScreen(
    viewModel: SingleTaskViewModel = hiltViewModel(),
    navController: NavHostController = rememberNavController(),
) {
    with(viewModel) {
        SingleTaskBody(
            title = title.collectAsState().value ?: stringResource(R.string.title_add_task_new_task),
            taskName = taskName.collectAsState().value,
            onTaskNameChange = ::onTaskNameChange,
            onBack = navController::navigateUp
        )
    }
}

@Composable
private fun SingleTaskBody(
    title: String,
    taskName: String,
    onTaskNameChange: (String) -> Unit,
    onBack: () -> Unit
) {

    val items = getItems()

    Scaffold(
        topBar = {
            TopBar(
                title = title,
                onBack = onBack
            )
        }
    ) {
        Row(
            modifier = Modifier
                .padding(
                    start = 16.dp,
                    end = 8.dp
                )
        ) {
            TaskName(
                text = taskName,
                onTextChange = onTaskNameChange
            )
            LazyColumn {
                item {
                    Divider(color = Color.Blue, thickness = 2.dp)
                }
            }
        }
    }
}

private fun getItems(): List<SettingItem> {
    return listOf(

    )
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
                Icon(Icons.Filled.Close, "")
            }
        }
    )
}

@Composable
private fun TaskName(
    text: String,
    onTextChange: (String) -> Unit
) {
    TextField(
        value = text,
        onValueChange = onTextChange,
        label = { Text(stringResource(R.string.hint_edit_text_name)) },
        colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.Transparent),
        maxLines = 1,
        textStyle = TextStyle(color = Color.Blue, fontWeight = FontWeight.Bold),
        modifier = Modifier
            .fillMaxWidth()
    )
}


@Preview
@Composable
fun SingleTaskScreenPreview() {
    SingleTaskBody(
        title = stringResource(R.string.title_add_task_new_task),
        taskName = "",
        onTaskNameChange = {},
        onBack = {}
    )
}