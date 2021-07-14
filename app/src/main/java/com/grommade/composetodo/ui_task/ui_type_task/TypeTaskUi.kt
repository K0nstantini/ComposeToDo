package com.grommade.composetodo.ui_task.ui_type_task

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.grommade.composetodo.R
import com.grommade.composetodo.enums.TypeTask
import com.grommade.composetodo.ui.common.rememberFlowWithLifecycle
import com.grommade.composetodo.ui.components.DoneIcon
import com.grommade.composetodo.ui.components.NavigationCloseIcon

@Composable
fun TypeTaskUi(navController: NavController) {
    TypeTaskUi(
        viewModel = hiltViewModel(),
        navController = navController
    )
}

@Composable
fun TypeTaskUi(
    viewModel: TypeTaskViewModel,
    navController: NavController
) {
    val type by rememberFlowWithLifecycle(viewModel.type)
        .collectAsState(initial = TypeTask.EXACT_TIME)

    TypeTaskUi(type) { action ->
        when (action) {
            is TypeTaskActions.ChangeType -> viewModel.changeType(action.value)
            TypeTaskActions.Confirm -> navController.navigateUp()
            TypeTaskActions.Close -> navController.navigateUp()
        }
    }

}

@Composable
fun TypeTaskUi(
    type: TypeTask,
    actioner: (TypeTaskActions) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings_add_task_title_type)) },
                navigationIcon = { NavigationCloseIcon { actioner(TypeTaskActions.Close) } },
                actions = { DoneIcon { actioner(TypeTaskActions.Confirm) } }
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
            Divider(color = Color.Transparent, thickness = 4.dp)
            TypeTaskBody(type) { type ->
                actioner(TypeTaskActions.ChangeType(type))
            }
        }
    }
}

@Composable
fun TypeTaskBody(
    type: TypeTask,
    onClick: (TypeTask) -> Unit
) {
    Column(Modifier.selectableGroup()) {
        RadioButtonItem(
            selected = type == TypeTask.EXACT_TIME,
            title = stringResource(R.string.task_card_title_type_task_temp),
            value = stringResource(R.string.task_card_value_type_task_temp),
        ) { onClick(TypeTask.EXACT_TIME) }
        RadioButtonItem(
            selected = type == TypeTask.RANDOM,
            title = stringResource(R.string.task_card_title_type_task_single),
            value = stringResource(R.string.task_card_value_type_task_single),
        ) { onClick(TypeTask.RANDOM) }
        RadioButtonItem(
            selected = type.regular,
            title = stringResource(R.string.task_card_title_type_task_regular),
            value = stringResource(R.string.task_card_value_type_task_regular),
        ) { onClick(TypeTask.SHORT_REGULAR) }
    }

    Column(Modifier.selectableGroup()) {
        RadioButtonItem(
            selected = type == TypeTask.LONG_REGULAR,
            title = stringResource(R.string.task_card_title_type_task_regular_long),
            value = stringResource(R.string.task_card_value_type_task_regular_long),
        ) { onClick(TypeTask.LONG_REGULAR) }
        RadioButtonItem(
            selected = type == TypeTask.SHORT_REGULAR || type == TypeTask.CONTAINER,
            title = stringResource(R.string.task_card_title_type_task_regular_short),
            value = stringResource(R.string.task_card_value_type_task_regular_short),
        ) { onClick(TypeTask.SHORT_REGULAR) }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End,
        modifier = Modifier.padding(8.dp)
    ) {
        Checkbox(
            checked = type == TypeTask.CONTAINER,
            onCheckedChange = { value ->
                when (value) {
                    true -> onClick(TypeTask.SHORT_REGULAR)
                    false -> onClick(TypeTask.CONTAINER)
                }
            }
        )
        Column(modifier = Modifier.padding(start = 16.dp)) {
            Text(
                text = stringResource(R.string.task_card_title_type_task_regular_container),
                style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.SemiBold)
            )
            Text(
                text = stringResource(R.string.task_card_value_type_task_regular_container),
                style = MaterialTheme.typography.body2.copy(fontSize = 12.sp, color = Color.DarkGray)
            )
        }
    }


}

@Composable
fun RadioButtonItem(
    selected: Boolean,
    title: String,
    value: String,
    onClick: () -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .selectable(
                selected = selected,
                onClick = onClick,
                role = Role.RadioButton
            )
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = null // null recommended for accessibility with screenreaders
        )
        Column(modifier = Modifier.padding(start = 16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.SemiBold)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.body2.copy(fontSize = 12.sp, color = Color.DarkGray)
            )
        }


    }
}

@Preview
@Composable
fun TypeTaskUiPreview() {
    TypeTaskUi(TypeTask.EXACT_TIME) {}
}