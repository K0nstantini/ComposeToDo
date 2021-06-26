package com.grommade.composetodo.single_task

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Save
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.grommade.composetodo.R
import com.grommade.composetodo.add_classes.MyCalendar
import com.grommade.composetodo.db.entity.DEFAULT_DEADLINE_SINGLE_TASK
import com.grommade.composetodo.ui.components.*
import com.grommade.composetodo.util.Keys
import com.vanpra.composematerialdialogs.MaterialDialog

@ExperimentalMaterialApi
@ExperimentalComposeUiApi
@Composable
fun SingleTaskScreen(
    viewModel: SingleTaskViewModel = hiltViewModel(),
    navController: NavHostController = rememberNavController(),
) {

    with(viewModel) {
        val handle = navController.currentBackStackEntry?.savedStateHandle
        handle?.get<Long>(Keys.SELECTED_TASK_ID)?.let {
            setParentsID(it)
            handle.remove<Long>(Keys.SELECTED_TASK_ID)
        }

        val onClickParent: () -> Unit = {
            navController.navigate(navigateToSelectParent)
        }

        val onClickSave: () -> Unit = {
            saveTask()
            navController.navigateUp()
        }

        SingleTaskBody(
            title = title.collectAsState().value ?: stringResource(R.string.title_add_task_new_task),
            taskItem = taskItem.collectAsState().value,
            readyToSafe = readyToSafe.collectAsState().value,
            onTaskNameChange = ::onTaskNameChange,
            onClickGroup = ::onGroupClicked,
            onClickParent = onClickParent,
            onClickClearParent = ::onParentClearClicked,
            saveDateStart = ::saveDateStart,
            saveDeadline = ::saveDeadline,
            onClickSave = onClickSave,
            onBack = navController::navigateUp
        )
    }
}

@ExperimentalMaterialApi
@ExperimentalComposeUiApi
@Composable
private fun SingleTaskBody(
    title: String,
    taskItem: SingleTaskViewModel.SingleTaskItem,
    readyToSafe: Boolean,
    onTaskNameChange: (String) -> Unit,
    onClickGroup: (Boolean) -> Unit,
    onClickParent: () -> Unit,
    onClickClearParent: () -> Unit,
    saveDateStart: (MyCalendar) -> Unit,
    saveDeadline: (String) -> Unit,
    onClickSave: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopBar(
                title = title,
                readyToSafe = readyToSafe,
                onClickSave = onClickSave,
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
            TaskNameEditField(
                text = taskItem.name,
                onTextChange = onTaskNameChange
            )
            Divider(color = Color.Transparent, thickness = 8.dp)
            SetSettingsItems(
                taskItem = taskItem,
                onClickGroup = onClickGroup,
                onClickParent = onClickParent,
                onClickClearParent = onClickClearParent,
                saveDateStart = saveDateStart,
                saveDeadline = saveDeadline,
            )
        }
    }
}


@ExperimentalMaterialApi
@ExperimentalComposeUiApi
@Composable
fun SetSettingsItems(
    taskItem: SingleTaskViewModel.SingleTaskItem,
    onClickGroup: (Boolean) -> Unit,
    onClickParent: () -> Unit,
    onClickClearParent: () -> Unit,
    saveDateStart: (MyCalendar) -> Unit,
    saveDeadline: (String) -> Unit,
) {
    val dateStartDialog = remember { MaterialDialog() }.apply { BuiltDateDialog(saveDateStart) }
    val deadlineDialog = remember { MaterialDialog() }.apply {
        BuiltInputDialog(
            title = stringResource(R.string.alert_title_add_single_task_deadline),
            prefill = taskItem.deadline.toString(),
            label = stringResource(R.string.alert_label_add_single_task_deadline),
            hint = stringResource(R.string.alert_hint_add_single_task_deadline, DEFAULT_DEADLINE_SINGLE_TASK),
            callback = saveDeadline,
            isTextValid = { text -> text.toIntOrNull() ?: 0 > 0 }
        )
    }
    val onClickDateStart: () -> Unit = { dateStartDialog.show() }
    val onClickDeadline: () -> Unit = { deadlineDialog.show() }

    LazyColumn {
        /** Group */
        item {
            SetItemSwitch(
                title = stringResource(R.string.settings_add_task_title_group),
                stateSwitch = taskItem.group,
                onClick = { onClickGroup(!taskItem.group) },
                onClickSwitch = onClickGroup
            )
        }
        /** Parent */
        item {
            SetItemWithClear(
                title = stringResource(R.string.settings_add_task_title_parent),
                value = taskItem.parent ?: stringResource(R.string.settings_main_catalog_text),
                showClear = taskItem.parent != null,
                onClick = onClickParent,
                onClickClear = onClickClearParent
            )
        }
        /** DateStart */
        item {
            SetItemDefault(
                title = stringResource(R.string.settings_add_single_task_title_date_start),
                value = taskItem.dateStart,
                enabled = !taskItem.group,
                onClick = onClickDateStart,
            )
        }
        /** Deadline */
        item {
            SetItemDefault(
                title = stringResource(R.string.settings_add_single_task_title_deadline),
                value = when (taskItem.deadline) {
                    0 -> stringResource(R.string.settings_add_single_task_deadline_zero_text)
                    else -> stringResource(
                        R.string.settings_add_single_task_deadline_time_hours_text,
                        taskItem.deadline
                    )
                },
                enabled = !taskItem.group,
                onClick = onClickDeadline,
            )
        }
    }
}

@Composable
private fun TopBar(
    title: String,
    readyToSafe: Boolean,
    onClickSave: () -> Unit,
    onBack: () -> Unit
) {
    TopAppBar(
        title = { Text(title, maxLines = 1, overflow = TextOverflow.Ellipsis) },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.Filled.Close, "")
            }
        },
        actions = {
            IconButton(onClick = onClickSave, enabled = readyToSafe) {
                Icon(Icons.Filled.Save, "")
            }
        }
    )
}

@Composable
private fun TaskNameEditField(
    text: String,
    onTextChange: (String) -> Unit
) {
    TextField(
        value = text,
        onValueChange = onTextChange,
        label = { Text(stringResource(R.string.hint_edit_text_name)) },
        colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.Transparent),
        maxLines = 1,
        textStyle = MaterialTheme.typography.h6.copy(color = MaterialTheme.colors.secondaryVariant),
        modifier = Modifier.fillMaxWidth()
    )
}

@ExperimentalMaterialApi
@ExperimentalComposeUiApi
@Preview
@Composable
fun SingleTaskScreenPreview() {
    SingleTaskBody(
        title = stringResource(R.string.title_add_task_new_task),
        taskItem = SingleTaskViewModel.SingleTaskItem(name = "New Task"),
        readyToSafe = true,
        onTaskNameChange = {},
        onClickGroup = {},
        onClickParent = {},
        onClickClearParent = {},
        saveDateStart = {},
        saveDeadline = {},
        onClickSave = {},
        onBack = {}
    )
}