package com.grommade.composetodo.ui_task

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.grommade.composetodo.R
import com.grommade.composetodo.enums.TypeTask
import com.grommade.composetodo.ui.common.rememberFlowWithLifecycle
import com.grommade.composetodo.ui.components.NavigationCloseIcon
import com.grommade.composetodo.ui.components.SetItemDefault
import com.grommade.composetodo.util.extensions.toTypeTask

@ExperimentalMaterialApi
@Composable
fun TaskUi(navController: NavController) {
    TaskUi(
        viewModel = hiltViewModel(),
        navController = navController
    )
}

@ExperimentalMaterialApi
@Composable
fun TaskUi(
    viewModel: TaskViewModel,
    navController: NavController
) {
    viewModel.navigateToBack.collectAsState().value?.let { navController.navigateUp() }

    val viewState by rememberFlowWithLifecycle(viewModel.state)
        .collectAsState(initial = TaskViewState.Empty)

    TaskUi(viewState) { action ->
        when (action) {
            TaskActions.ChangeType -> navController.toTypeTask(viewState.task.type)
            TaskActions.Close -> navController.navigateUp()
            else -> viewModel.submitAction(action)
        }
    }
}

@ExperimentalMaterialApi
@Composable
fun TaskUi(
    viewState: TaskViewState,
    actioner: (TaskActions) -> Unit
) {
    val task = viewState.task
    Scaffold(
        topBar = {
            TopBar(
                name = task.name,
                isNew = task.isNew,
                actioner = actioner
            )
        },
    ) {
        Column(
            modifier = Modifier
                .padding(
                    start = 16.dp,
                    end = 8.dp
                )
        ) {
            TaskNameEditField(
                name = task.name,
                actioner = actioner
            )
            SetTypeTask(task.type, actioner)
        }
    }
}

@Composable
fun TopBar(
    name: String,
    isNew: Boolean,
    actioner: (TaskActions) -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = when (isNew) {
                    true -> stringResource(R.string.title_add_task_new_task)
                    false -> name
                },
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = { NavigationCloseIcon { actioner(TaskActions.Close) } },
        actions = {
            IconButton(
                onClick = { actioner(TaskActions.Save) },
                enabled = name.isNotEmpty()
            ) {
                Icon(Icons.Filled.Save, "")
            }
        }
    )
}

@Composable
fun TaskNameEditField(
    name: String,
    actioner: (TaskActions) -> Unit
) {
    TextField(
        value = name,
        onValueChange = { actioner(TaskActions.ChangeName(it)) },
        label = { Text(stringResource(R.string.hint_edit_text_name)) },
        colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.Transparent),
        isError = name.isEmpty(),
        singleLine = true,
        textStyle = MaterialTheme.typography.h6.copy(color = MaterialTheme.colors.secondaryVariant),
        modifier = Modifier.fillMaxWidth()
    )
}

@ExperimentalMaterialApi
@Composable
fun SetTypeTask(
    type: TypeTask,
    actioner: (TaskActions) -> Unit
) {
    SetItemDefault(
        title = stringResource(R.string.settings_add_task_title_type),
        value = type.name,
        onClick = { actioner(TaskActions.ChangeType) },
    )
}

/** ============================================ Dialogs ========================================================== */


@ExperimentalMaterialApi
@Preview
@Composable
fun TaskUiPreview() {
    TaskUi(TaskViewState()) {}
}