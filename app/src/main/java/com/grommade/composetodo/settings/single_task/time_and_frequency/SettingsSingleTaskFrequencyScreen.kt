package com.grommade.composetodo.settings.single_task.time_and_frequency

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.grommade.composetodo.R
import com.grommade.composetodo.db.entity.Settings
import com.grommade.composetodo.db.entity.Settings.SettingsSingleTask
import com.grommade.composetodo.enums.ModeGenerationSingleTasks
import com.grommade.composetodo.ui.components.*
import com.grommade.composetodo.util.toStrTime
import com.vanpra.composematerialdialogs.MaterialDialog

@ExperimentalMaterialApi
@Composable
fun SettingsSingleTaskFrequencyScreen(
    viewModel: SettingsSingleTaskFrequencyViewModel = hiltViewModel(),
    navController: NavHostController = rememberNavController()
) {

    with(viewModel) {

        SettingsSingleTaskFrequencyScreenBody(
            settings = settings.collectAsState(Settings()).value.singleTask,
            saveMode = ::saveMode,
            savePeriodFrom = ::savePeriodFrom,
            savePeriodTo = ::savePeriodTo,
            resetPeriod = ::resetPeriod,
            onBack = { navController.navigateUp() }
        )
    }

}

@ExperimentalMaterialApi
@Composable
private fun SettingsSingleTaskFrequencyScreenBody(
    settings: SettingsSingleTask = SettingsSingleTask(),
    saveMode: (Int) -> Unit = {},
    savePeriodFrom: (Int) -> Unit = {},
    savePeriodTo: (Int) -> Unit = {},
    resetPeriod: () -> Unit = {},
    onBack: () -> Unit = {}
) {
    val modeDialog = modeDialog(settings.modeGeneration.ordinal, saveMode)
    val timeFromDialog = timeFromDialog(settings.periodFrom, savePeriodFrom)
    val timeToDialog = timeToDialog(settings.periodTo, savePeriodTo)

    val selectPeriod: (Int) -> Unit = { index: Int ->
        when (index) {
            0 -> timeFromDialog.show()
            1 -> timeToDialog.show()
            else -> resetPeriod()
        }
    }
    val periodListDialog = periodListDialog(selectPeriod)

    Scaffold(
        topBar = {
            // TODO: Appbar вынести
            TopAppBar(
                title = { Text(stringResource(R.string.title_settings_s_tasks_time_and_frequency)) },
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
                SetItemDefault(
                    title = stringResource(R.string.settings_s_task_title_mode_generation),
                    value = stringResource(settings.modeGeneration.title),
                    onClick = { modeDialog.show() },
                )
            }
            item {
                SetItemDefault(
                    title = stringResource(R.string.settings_s_task_title_period),
                    value = when (settings.periodFrom + settings.periodTo) {
                        0 -> stringResource(R.string.settings_s_task_value_no_period)
                        else -> stringResource(
                            R.string.settings_s_task_value_period,
                            settings.periodFrom.toStrTime(),
                            settings.periodTo.toStrTime()
                        )
                    },
                    onClick = { periodListDialog.show() },
                )
            }
        }
    }
}

@Composable
fun modeDialog(
    initialSelection: Int,
    callback: (Int) -> Unit
): MaterialDialog = remember { MaterialDialog() }.apply {
    BuiltSingleChoiceDialog(
        title = stringResource(R.string.alert_mode_generation_single_tasks),
        list = ModeGenerationSingleTasks.toList().map { stringResource(it) },
        initialSelection = initialSelection,
        callback = callback
    )
}

@Composable
fun timeFromDialog(
    periodFrom: Int,
    savePeriodFrom: (Int) -> Unit
): MaterialDialog = remember { MaterialDialog() }.apply {
    BuiltTimeDialog(periodFrom, savePeriodFrom)
}

@Composable
fun timeToDialog(
    periodTo: Int,
    savePeriodTo: (Int) -> Unit
): MaterialDialog = remember { MaterialDialog() }.apply {
    BuiltTimeDialog(periodTo, savePeriodTo)
}

@Composable
fun periodListDialog(
    selectPeriod: (Int) -> Unit
): MaterialDialog = remember { MaterialDialog() }.apply {
    BuiltListDialog(
        title = stringResource(R.string.settings_s_task_title_period),
        list = stringArrayResource(R.array.from_to).toList(),
        callback = selectPeriod
    )
}

@ExperimentalMaterialApi
@Preview
@Composable
fun SettingsSingleTaskFrequencyScreenPreview() {
    SettingsSingleTaskFrequencyScreenBody()
}