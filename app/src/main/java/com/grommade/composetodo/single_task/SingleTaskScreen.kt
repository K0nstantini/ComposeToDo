package com.grommade.composetodo.single_task

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
            sets = settings.collectAsState().value,
            onTaskNameChange = ::onTaskNameChange,
            onBack = navController::navigateUp
        )
    }
}

@Composable
private fun SingleTaskBody(
    title: String,
    taskName: String,
    sets: List<SettingItem>,
    onTaskNameChange: (String) -> Unit,
    onBack: () -> Unit
) {

    Scaffold(
        topBar = {
            TopBar(
                title = title,
                onBack = onBack
            )
        }
    ) {
        Column(
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
            Divider(color = Color.Transparent, thickness = 8.dp)
            LazyColumn {
                items(sets) { set ->
                    SetItem(set)
                }
            }
        }
    }
}

private fun getItems(): List<SettingItem> {
    return listOf(
        SettingItem(R.string.settings_add_task_title_group)
            .setSwitch({}),
        SettingItem(R.string.settings_add_task_title_parent)
            .setClear({})
            .setValue(res = R.string.settings_main_catalog_text),
        SettingItem(R.string.settings_add_single_task_title_date_start),
    )
}

@Composable
fun SetItem(set: SettingItem) {
    SetItemBody(
        title = stringResource(set.title),
        value = set.getValue(),
        showSwitch = set.showSwitch,
        stateSwitch = set.stateSwitch,
        showClear = set.showClear,
        action = set.action,
        actionSwitch = set.actionSwitch,
        actionClear = set.actionClear
    )
}

@Composable
private fun SetItemBody(
    title: String,
    value: String,
    showSwitch: Boolean,
    stateSwitch: Boolean,
    showClear: Boolean,
    action: () -> Unit,
    actionSwitch: (Boolean) -> Unit,
    actionClear: () -> Unit
) {
    Box(
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .selectable(
                    selected = false,
                    onClick = action
                ),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(title, style = MaterialTheme.typography.h6.copy(fontSize = 16.sp))
                Row(
                    modifier = if (showClear) Modifier.fillMaxWidth() else Modifier, // FIXME: WTF?
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(value, style = MaterialTheme.typography.body2.copy(color = Color.DarkGray))
                    if (showClear) {
                        Text(
                            text = stringResource(R.string.btn_clear),
                            style = MaterialTheme.typography.body2.copy(color = MaterialTheme.colors.primary),
                            modifier = Modifier.selectable(
                                selected = false,
                                onClick = actionClear
                            )
                        )
                    }
                }
            }
            if (showSwitch) {
                Switch(checked = stateSwitch, onCheckedChange = actionSwitch)
            }
        }
    }
    Divider(thickness = 1.dp)
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
        textStyle = MaterialTheme.typography.h6,
        modifier = Modifier.fillMaxWidth()
    )
}


@Preview
@Composable
fun SingleTaskScreenPreview() {
    SingleTaskBody(
        title = stringResource(R.string.title_add_task_new_task),
        taskName = "",
        sets = getItems(),
        onTaskNameChange = {},
        onBack = {}
    )
}