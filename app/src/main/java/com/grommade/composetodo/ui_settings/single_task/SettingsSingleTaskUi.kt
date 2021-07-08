package com.grommade.composetodo.ui_settings.single_task

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.grommade.composetodo.R
import com.grommade.composetodo.add_classes.MyCalendar
import com.grommade.composetodo.data.entity.Settings.SettingsSingleTask
import com.grommade.composetodo.enums.ModeGenerationSingleTasks
import com.grommade.composetodo.ui.common.rememberFlowWithLifecycle
import com.grommade.composetodo.ui.components.BuiltDateTimeDialog
import com.grommade.composetodo.ui.components.NavigationBackIcon
import com.grommade.composetodo.ui.components.SetItemDefault
import com.grommade.composetodo.ui.components.SetItemSwitch
import com.grommade.composetodo.util.extensions.toDaysOfWeek
import com.grommade.composetodo.util.extensions.toSettingsSingleTask
import com.grommade.composetodo.util.extensions.toStrTime
import com.vanpra.composematerialdialogs.MaterialDialog

@ExperimentalMaterialApi
@Composable
fun SettingsSingleTaskUi(navController: NavHostController) {
    SettingsSingleTaskUi(
        viewModel = hiltViewModel(),
        navController = navController
    )
}

@ExperimentalMaterialApi
@Composable
fun SettingsSingleTaskUi(
    viewModel: SettingsSingleTaskViewModel,
    navController: NavHostController
) {
    val settings by rememberFlowWithLifecycle(viewModel.settingsState)
        .collectAsState(SettingsSingleTask())

    SettingsSingleTaskUi(settings) { action ->
        when (action) {
            SettingsSingleTaskActions.ToTimeAndFrequency -> navController.toSettingsSingleTask()
            SettingsSingleTaskActions.Back -> navController.navigateUp()
            is SettingsSingleTaskActions.ChangeStartGeneration -> viewModel.changeStartGeneration(action.date)
        }
    }
}

@ExperimentalMaterialApi
@Composable
fun SettingsSingleTaskUi(
    settings: SettingsSingleTask,
    actioner: (SettingsSingleTaskActions) -> Unit
) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.title_settings_s_tasks)) },
                navigationIcon = { NavigationBackIcon { actioner(SettingsSingleTaskActions.Back) } }
            )
        }
    ) {
        LazyColumn(
            Modifier
                .padding(8.dp)
                .padding(start = 16.dp)
        ) {
            item {
                ActiveItem(settings) { date: MyCalendar ->
                    actioner(SettingsSingleTaskActions.ChangeStartGeneration(date))
                }
            }
            item {
                FrequencyItem(settings) {
                    actioner(SettingsSingleTaskActions.ToTimeAndFrequency)
                }
            }
        }
    }
}

/** ============================================ Settings Items  ================================================== */

@ExperimentalMaterialApi
@Composable
private fun ActiveItem(
    settings: SettingsSingleTask,
    onClickActive: (MyCalendar) -> Unit,
) {
    val activeDialog = activeDialog(onClickActive)

    val callback = {
        if (!settings.active) activeDialog.show() else onClickActive(MyCalendar())
    }
    SetItemSwitch(
        title = stringResource(R.string.settings_s_task_title_active),
        value = when (settings.active) {
            true -> stringResource(R.string.settings_s_task_value_active_true, settings.startGeneration.toString())
            false -> stringResource(R.string.settings_s_task_value_active_false)
        },
        stateSwitch = settings.active,
        onClick = callback,
        onClickSwitch = { callback() }
    )
}

@ExperimentalMaterialApi
@Composable
private fun FrequencyItem(
    settings: SettingsSingleTask,
    onClickTimeAndFrequency: () -> Unit
) {
    SetItemDefault(
        title = stringResource(R.string.settings_s_task_title_frequency_and_time),
        value = getValueFrequency(settings),
        onClick = onClickTimeAndFrequency,
    )
}

/** ============================================ Dialogs ========================================================== */

@Composable
private fun activeDialog(
    saveActiveTime: (MyCalendar) -> Unit
): MaterialDialog = remember { MaterialDialog() }
    .apply { BuiltDateTimeDialog(MyCalendar.now(), saveActiveTime) }


/** ============================================ Other ========================================================== */

@Composable
fun getValueFrequency(set: SettingsSingleTask): String {
    val resources = LocalContext.current.resources
    val period = if (set.periodNoRestriction) "" else (set.periodFrom..set.periodTo).toStrTime()
    val daysOfWeek = set.daysOfWeek.toDaysOfWeek(resources)
    val frequency = "${set.frequencyFrom} - ${set.frequencyTo}"
    val everyFewDays = resources.getQuantityString(R.plurals.every_n_days, set.everyFewDays, set.everyFewDays)

    val value = if (set.modeGeneration == ModeGenerationSingleTasks.RANDOM) {
        stringResource(R.string.settings_s_task_value_frequency_and_time_random, frequency)
    } else {
        if (set.daysOfWeekNoRestriction) everyFewDays else ""
    } + "\n" + "$daysOfWeek $period".trim()

    return value.trim()

}

/** ============================================ Preview ========================================================== */

@ExperimentalMaterialApi
@Preview
@Composable
fun SettingsSingleTaskScreenPreview() {
    SettingsSingleTaskUi(SettingsSingleTask()) {}
}
