package com.grommade.composetodo.ui_single_task

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.grommade.composetodo.R
import com.grommade.composetodo.add_classes.MyCalendar
import com.grommade.composetodo.data.entity.DEFAULT_DEADLINE_SINGLE_TASK
import com.grommade.composetodo.ui.common.rememberFlowWithLifecycle
import com.grommade.composetodo.ui.components.*
import com.grommade.composetodo.util.Keys
import com.grommade.composetodo.util.extensions.toSelectParent
import com.vanpra.composematerialdialogs.MaterialDialog
import timber.log.Timber

@ExperimentalComposeUiApi
@ExperimentalMaterialApi
@Composable
fun SingleTaskUi(navController: NavHostController) {
    SingleTaskUi(
        viewModel = hiltViewModel(),
        navController = navController
    )
}

@ExperimentalMaterialApi
@ExperimentalComposeUiApi
@Composable
fun SingleTaskUi(
    viewModel: SingleTaskViewModel,
    navController: NavHostController,
) {
    val handle = navController.currentBackStackEntry?.savedStateHandle
    handle?.get<Long>(Keys.SELECTED_TASK_ID)?.let {
        viewModel.setParentsID(it)
        handle.remove<Long>(Keys.SELECTED_TASK_ID)
    }

    Timber.tag("-Timber-").d("Refreshed")
    viewModel.navigateToBack.collectAsState().value?.let { navController.navigateUp() }

    val viewState by rememberFlowWithLifecycle(viewModel.state)
        .collectAsState(initial = SingleTaskViewState.Empty)

    SingleTaskUi(viewState) { action ->
        when (action) {
            SingleTaskActions.SelectParent -> navController.toSelectParent(viewState.parentId)
            SingleTaskActions.Close -> navController.navigateUp()
            else -> viewModel.submitAction(action)
        }
    }

}


@ExperimentalComposeUiApi
@ExperimentalMaterialApi
@Composable
fun SingleTaskUi(
    viewState: SingleTaskViewState,
    actioner: (SingleTaskActions) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = viewState.title ?: stringResource(R.string.title_add_task_new_task),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = { NavigationCloseIcon { actioner(SingleTaskActions.Close) } },
                actions = {
                    IconButton(
                        onClick = { actioner(SingleTaskActions.Save) },
                        enabled = viewState.name.isNotEmpty() && viewState.deadline > 0
                    ) {
                        Icon(Icons.Filled.Save, "")
                    }
                }
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
                name = viewState.name,
                actioner = actioner
            )
            Divider(color = Color.Transparent, thickness = 8.dp)
            SetSettingsItems(
                viewState = viewState,
                actioner = actioner
            )
        }
    }
}

@Composable
fun TaskNameEditField(
    name: String,
    actioner: (SingleTaskActions) -> Unit
) {
    TextField(
        value = name,
        onValueChange = { actioner(SingleTaskActions.ChangeName(it)) },
        label = { Text(stringResource(R.string.hint_edit_text_name)) },
        colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.Transparent),
        isError = name.isEmpty(),
        singleLine = true,
        textStyle = MaterialTheme.typography.h6.copy(color = MaterialTheme.colors.secondaryVariant),
        modifier = Modifier.fillMaxWidth()
    )
}


@ExperimentalMaterialApi
@ExperimentalComposeUiApi
@Composable
fun SetSettingsItems(
    viewState: SingleTaskViewState,
    actioner: (SingleTaskActions) -> Unit
) {

    val dateStartDialog = dateStartDialog { date: MyCalendar ->
        actioner(SingleTaskActions.ChangeDateStart(date))
    }
    val deadlineDialog = deadlineDialog(viewState.deadline) { deadline: Int ->
        actioner(SingleTaskActions.ChangeDeadline(deadline))
    }

    LazyColumn {
        /** Group */
        item {
            SetItemSwitch(
                title = stringResource(R.string.settings_add_task_title_group),
                stateSwitch = viewState.group,
                onClick = { actioner(SingleTaskActions.ChangeGroup(!viewState.group)) },
                onClickSwitch = { group: Boolean ->
                    actioner(SingleTaskActions.ChangeGroup(group))
                }
            )
        }
        /** Parent */
        item {
            SetItemWithClear(
                title = stringResource(R.string.settings_add_task_title_parent),
                value = viewState.parentStr ?: stringResource(R.string.settings_main_catalog_text),
                showClear = viewState.parentStr != null,
                onClick = { actioner(SingleTaskActions.SelectParent) },
                onClickClear = { actioner(SingleTaskActions.ClearParent) },
            )
        }
        /** DateStart */
        item {
            SetItemDefault(
                title = stringResource(R.string.settings_add_single_task_title_date_start),
                value = viewState.dateStart,
                enabled = !viewState.group,
                onClick = dateStartDialog::show,
            )
        }
        /** Deadline */
        item {
            SetItemDefault(
                title = stringResource(R.string.settings_add_single_task_title_deadline),
                value = when (viewState.deadline) {
                    0 -> stringResource(R.string.settings_add_single_task_deadline_zero_text)
                    else -> stringResource(
                        R.string.settings_add_single_task_deadline_time_hours_text,
                        viewState.deadline
                    )
                },
                enabled = !viewState.group,
                onClick = deadlineDialog::show,
            )
        }
    }
}

@Composable
fun dateStartDialog(
    callback: (MyCalendar) -> Unit
) = remember { MaterialDialog() }.apply {
    BuiltDateDialog(callback)
}

@ExperimentalComposeUiApi
@Composable
fun deadlineDialog(
    prefill: Int,
    callback: (Int) -> Unit
) = remember { MaterialDialog() }.apply {
    BuiltInputDialog(
        title = stringResource(R.string.alert_title_add_single_task_deadline),
        prefill = prefill.toString(),
        label = stringResource(R.string.alert_label_add_single_task_deadline),
        hint = stringResource(R.string.alert_hint_add_single_task_deadline, DEFAULT_DEADLINE_SINGLE_TASK),
        isTextValid = { text -> text.toIntOrNull() ?: 0 > 0 }
    ) { value ->
        value.toIntOrNull()?.let { callback(it) }
    }
}

@ExperimentalMaterialApi
@ExperimentalComposeUiApi
@Preview
@Composable
fun SingleTaskScreenPreview() {

}