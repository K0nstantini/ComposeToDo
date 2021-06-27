package com.grommade.composetodo.settings.single_task

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.grommade.composetodo.R
import com.grommade.composetodo.db.entity.Settings
import com.grommade.composetodo.db.entity.Settings.SettingsSingleTask
import com.grommade.composetodo.ui.components.NavigationBackIcon
import com.grommade.composetodo.ui.components.SetItemSwitch

@ExperimentalMaterialApi
@Composable
fun SettingsSingleTaskScreen(
    viewModel: SettingsSingleTaskViewModel = hiltViewModel(),
    navController: NavHostController = rememberNavController()
) {

    with(viewModel) {
        SettingsSingleTaskScreenBody(
            settings = settings.collectAsState(Settings()).value.singleTask,
            onClickActive = ::onClickActive,
            onBack = { navController.navigateUp() }
        )
    }

}

@ExperimentalMaterialApi
@Composable
private fun SettingsSingleTaskScreenBody(
    settings: SettingsSingleTask = SettingsSingleTask(),
    onClickActive: (Boolean) -> Unit = {},
    onBack: () -> Unit = {}
) {

    Scaffold(
        topBar = {
            // TODO: Appbar вынести
            TopAppBar(
                title = { Text(stringResource(R.string.title_settings_s_tasks)) },
                navigationIcon = { NavigationBackIcon(onBack) },
            )
        }
    ) {
        LazyColumn(
            Modifier.padding(8.dp).padding(start = 16.dp)
        ) {
            item {
                SetItemSwitch(
                    title = stringResource(R.string.settings_s_task_title_active),
                    value = stringResource(R.string.settings_s_task_value_active),
                    stateSwitch = settings.active,
                    onClick = { onClickActive(!settings.active) },
                    onClickSwitch = onClickActive
                )
            }
        }
    }
}

@ExperimentalMaterialApi
@Preview
@Composable
fun SettingsSingleTaskScreenPReview() {
    SettingsSingleTaskScreenBody()
}