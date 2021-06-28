package com.grommade.composetodo.settings.single_task

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
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
import com.grommade.composetodo.SettingsSingleTaskRoute
import com.grommade.composetodo.db.entity.Settings
import com.grommade.composetodo.db.entity.Settings.SettingsSingleTask
import com.grommade.composetodo.ui.components.NavigationBackIcon
import com.grommade.composetodo.ui.components.SetItemDefault
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
            onClickTimeAndFrequency = {
                navController.navigate(SettingsSingleTaskRoute.SettingsSingleTaskFrequencyChildRoute.route)
            },
            onBack = { navController.navigateUp() }
        )
    }

}

@ExperimentalMaterialApi
@Composable
private fun SettingsSingleTaskScreenBody(
    settings: SettingsSingleTask = SettingsSingleTask(),
    onClickActive: (Boolean) -> Unit = {},
    onClickTimeAndFrequency: () -> Unit = {},
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
            Modifier
                .padding(8.dp)
                .padding(start = 16.dp)
        ) {
            item {
                SetItemSwitch(
                    title = stringResource(R.string.settings_s_task_title_active),
                    value = when (settings.active) {
                        true -> stringResource(R.string.settings_s_task_value_active_true)
                        false -> stringResource(R.string.settings_s_task_value_active_false)
                    },
                    stateSwitch = settings.active,
                    onClick = { onClickActive(!settings.active) },
                    onClickSwitch = onClickActive
                )
            }
            item {
                SetItemDefault(
                    title = stringResource(R.string.settings_s_task_title_frequency_and_time),
                    value = "Каждый день",
                    onClick = onClickTimeAndFrequency,
                )
            }
        }
    }
}

@ExperimentalMaterialApi
@Preview
@Composable
fun SettingsSingleTaskScreenPreview() {
    SettingsSingleTaskScreenBody()
}