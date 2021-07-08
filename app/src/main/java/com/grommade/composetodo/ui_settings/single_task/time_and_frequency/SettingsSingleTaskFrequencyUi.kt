package com.grommade.composetodo.ui_settings.single_task.time_and_frequency

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
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.grommade.composetodo.R
import com.grommade.composetodo.data.entity.Settings.SettingsSingleTask
import com.grommade.composetodo.data.entity.Settings.SettingsSingleTask.Companion.FREQUENCY_GENERATE_FROM
import com.grommade.composetodo.data.entity.Settings.SettingsSingleTask.Companion.FREQUENCY_GENERATE_TO
import com.grommade.composetodo.enums.DialogDaysOfWeek
import com.grommade.composetodo.enums.DialogSelectDays
import com.grommade.composetodo.enums.DialogSelectPeriod
import com.grommade.composetodo.enums.ModeGenerationSingleTasks
import com.grommade.composetodo.enums.ModeGenerationSingleTasks.FIXED
import com.grommade.composetodo.enums.ModeGenerationSingleTasks.RANDOM
import com.grommade.composetodo.ui.common.rememberFlowWithLifecycle
import com.grommade.composetodo.ui.components.*
import com.grommade.composetodo.util.extensions.*
import com.vanpra.composematerialdialogs.MaterialDialog

@ExperimentalComposeUiApi
@ExperimentalMaterialApi
@Composable
fun SettingsSingleTaskFrequencyUi(navController: NavHostController) {
    SettingsSingleTaskFrequencyUi(
        viewModel = hiltViewModel(),
        navController = navController
    )
}

@ExperimentalComposeUiApi
@ExperimentalMaterialApi
@Composable
fun SettingsSingleTaskFrequencyUi(
    viewModel: SettingsSingleTaskFrequencyViewModel,
    navController: NavHostController
) {
    val settings by rememberFlowWithLifecycle(viewModel.settingsState)
        .collectAsState(SettingsSingleTask())

    SettingsSingleTaskFrequencyUi(settings) { action ->
        when (action) {
            SetSTaskFreqActions.Back -> navController.navigateUp()
            else -> viewModel.submitAction(action)
        }
    }
}

@ExperimentalComposeUiApi
@ExperimentalMaterialApi
@Composable
private fun SettingsSingleTaskFrequencyUi(
    settings: SettingsSingleTask,
    actioner: (SetSTaskFreqActions) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(R.string.title_settings_s_tasks_time_and_frequency))
                },
                navigationIcon = {
                    NavigationBackIcon { actioner(SetSTaskFreqActions.Back) }
                },
            )
        }
    ) {
        LazyColumn(
            Modifier
                .padding(8.dp)
                .padding(start = 16.dp)
        ) {
            with(settings) {
                item { ModeItem(modeGeneration, actioner) }
                item { TimeItem(periodFrom, periodTo, actioner) }
                item { DaysItem(everyFewDays, daysOfWeek, modeGeneration, actioner) }
                item {
                    when (modeGeneration) {
                        RANDOM -> FrequencyItem(frequencyFrom, frequencyTo, actioner)
                        FIXED -> CountTasksItem(countGeneratedTasksAtATime, actioner)
                    }
                }
            }
        }
    }
}

/** ============================================ Settings Items  ================================================== */

@ExperimentalMaterialApi
@Composable
private fun ModeItem(
    mode: ModeGenerationSingleTasks,
    actioner: (SetSTaskFreqActions) -> Unit
) {
    val modeDialog = modeDialog(mode.ordinal) { value: Int ->
        actioner(SetSTaskFreqActions.Mode(value))
    }

    SetItemDefault(
        title = stringResource(R.string.settings_s_task_title_mode_generation),
        value = stringResource(mode.title),
        onClick = modeDialog::show,
    )
}

@ExperimentalMaterialApi
@Composable
private fun TimeItem(
    periodFrom: Int,
    periodTo: Int,
    actioner: (SetSTaskFreqActions) -> Unit
) {
    val timeFromDialog = timeFromDialog(periodFrom) { value: Int ->
        actioner(SetSTaskFreqActions.PeriodFrom(value))
    }
    val timeToDialog = timeToDialog(periodTo) { value: Int ->
        actioner(SetSTaskFreqActions.PeriodTo(value))
    }

    val selectPeriod: (Int) -> Unit = { index: Int ->
        when (DialogSelectPeriod.values()[index]) {
            DialogSelectPeriod.FROM -> timeFromDialog.show()
            DialogSelectPeriod.TO -> timeToDialog.show()
            DialogSelectPeriod.NO_RESTRICTIONS -> actioner(SetSTaskFreqActions.PeriodNoRestrictions)
        }
    }
    val periodListDialog = periodListDialog(selectPeriod)

    val valueSelectTime = when (periodTo - periodFrom) {
        "23:59".timeToMinutes() -> stringResource(R.string.settings_s_task_value_no_period)
        else -> stringResource(R.string.settings_s_task_value_period, periodFrom.toStrTime(), periodTo.toStrTime())
    }

    SetItemDefault(
        title = stringResource(R.string.settings_s_task_title_period),
        value = valueSelectTime,
        onClick = periodListDialog::show,
    )
}

@ExperimentalMaterialApi
@ExperimentalComposeUiApi
@Composable
private fun DaysItem(
    everyFewDays: Int,
    daysOfWeek: String,
    mode: ModeGenerationSingleTasks,
    actioner: (SetSTaskFreqActions) -> Unit
) {
    val resources = LocalContext.current.resources

    val valueSelectDays = when {
        everyFewDays > 1 -> resources.getQuantityString(R.plurals.every_n_days, everyFewDays, everyFewDays)
        daysOfWeek.isNotEmpty() ->
            stringResource(R.string.settings_s_task_value_days_of_week, daysOfWeek.toDaysOfWeek(resources))
        else -> stringResource(R.string.settings_s_task_value_no_days)
    }

    val everyFewDaysInputDialog = everyFewDaysInputDialog(everyFewDays) { value: String ->
        actioner(SetSTaskFreqActions.EveryFewDays(value))
    }
    val daysOfWeekInputDialog = daysOfWeekDialog(daysOfWeek.toListInt()) { value: List<Int> ->
        actioner(SetSTaskFreqActions.DaysOfWeek(value))
    }

    val selectDays: (Int) -> Unit = { index: Int ->
        when (DialogSelectDays.values()[index]) {
            DialogSelectDays.EVERY_FEW_DAYS -> everyFewDaysInputDialog.show()
            DialogSelectDays.DAYS_OF_WEEK -> daysOfWeekInputDialog.show()
            DialogSelectDays.NO_RESTRICTIONS -> actioner(SetSTaskFreqActions.DaysNoRestriction)
        }
    }

    val daysListDialog = daysListDialog(mode, selectDays)

    SetItemDefault(
        title = stringResource(R.string.settings_s_task_title_days),
        value = valueSelectDays,
        onClick = daysListDialog::show,
    )
}

@ExperimentalMaterialApi
@ExperimentalComposeUiApi
@Composable
private fun CountTasksItem(
    countTasks: Int,
    actioner: (SetSTaskFreqActions) -> Unit
) {
    val countTasksDialog = countTasksDialog(countTasks) { value: String ->
        actioner(SetSTaskFreqActions.CountTasks(value))
    }
    SetItemDefault(
        title = stringResource(R.string.settings_s_task_title_count_tasks),
        value = countTasks.toString(),
        onClick = countTasksDialog::show,
    )
}

@ExperimentalMaterialApi
@ExperimentalComposeUiApi
@Composable
private fun FrequencyItem(
    frequencyFrom: Int,
    frequencyTo: Int,
    actioner: (SetSTaskFreqActions) -> Unit
) {
    val frequencyDialog = frequencyDialog(frequencyFrom, frequencyTo) { from: String, to: String ->
        actioner(SetSTaskFreqActions.Frequency(from, to))
    }
    SetItemDefault(
        title = stringResource(R.string.settings_s_task_title_frequency),
        value = stringResource(R.string.settings_s_task_value_frequency, frequencyFrom, frequencyTo),
        onClick = frequencyDialog::show,
    )
}

/** ============================================ Dialogs ========================================================== */

@Composable
private fun modeDialog(
    initialSelection: Int,
    callback: (Int) -> Unit
): MaterialDialog = remember { MaterialDialog() }.apply {
    BuiltSingleChoiceDialog(
        title = stringResource(R.string.alert_title_mode_generation_s_tasks),
        list = ModeGenerationSingleTasks.toList().map { stringResource(it) },
        initialSelection = initialSelection,
        callback = callback
    )
}

@Composable
private fun timeFromDialog(
    periodFrom: Int,
    savePeriodFrom: (Int) -> Unit
): MaterialDialog = remember { MaterialDialog() }.apply {
    BuiltTimeDialog(periodFrom, savePeriodFrom)
}

@Composable
private fun timeToDialog(
    periodTo: Int,
    savePeriodTo: (Int) -> Unit
): MaterialDialog = remember { MaterialDialog() }.apply {
    BuiltTimeDialog(periodTo, savePeriodTo)
}

@Composable
private fun periodListDialog(
    selectPeriod: (Int) -> Unit
): MaterialDialog = remember { MaterialDialog() }.apply {
    BuiltListDialog(
        title = stringResource(R.string.alert_title_select_time_s_tasks),
        list = DialogSelectPeriod.toList().map { stringResource(it) },
        callback = selectPeriod
    )
}

@Composable
private fun daysListDialog(
    mode: ModeGenerationSingleTasks,
    selectDays: (Int) -> Unit
): MaterialDialog = remember { MaterialDialog() }.apply {
    val list = when (mode) {
        RANDOM -> DialogSelectDays.toList() - DialogSelectDays.EVERY_FEW_DAYS.title
        FIXED -> DialogSelectDays.toList()
    }.map { stringResource(it) }

    BuiltListDialog(
        title = stringResource(R.string.alert_title_select_days_s_tasks),
        list = list,
        callback = selectDays
    )
}

@ExperimentalComposeUiApi
@Composable
fun everyFewDaysInputDialog(
    days: Int,
    callback: (String) -> Unit,
): MaterialDialog = remember { MaterialDialog() }.apply {
    BuiltInputDialog(
        title = stringResource(R.string.alert_title_select_every_few_days),
        message = stringResource(R.string.alert_message_select_every_few_days),
        prefill = days.toString(),
        label = stringResource(R.string.alert_label_select_every_few_days),
        hint = "1",
        callback = callback,
        isTextValid = { s: String -> s.isDigitsOnly() }
    )
}

@Composable
private fun daysOfWeekDialog(
    initialSelection: List<Int>,
    callback: (List<Int>) -> Unit
): MaterialDialog = remember { MaterialDialog() }.apply {
    BuiltSMultipleChoiceDialog(
        title = stringResource(R.string.alert_title_select_days_of_week),
        list = DialogDaysOfWeek.toList().map { stringResource(it) },
        initialSelection = initialSelection,
        callback = callback
    )
}

@ExperimentalComposeUiApi
@Composable
fun countTasksDialog(
    countTasks: Int,
    callback: (String) -> Unit,
): MaterialDialog = remember { MaterialDialog() }.apply {
    BuiltInputDialog(
        title = stringResource(R.string.alert_title_select_count_tasks),
        message = stringResource(R.string.alert_message_select_count_tasks),
        prefill = countTasks.toString(),
        label = stringResource(R.string.alert_label_select_count_tasks),
        hint = "1",
        callback = callback,
        isTextValid = { s: String -> s.isDigitsOnly() && s.isNotEmpty() && s != "0" }
    )
}

@ExperimentalComposeUiApi
@Composable
fun frequencyDialog(
    frequencyFrom: Int,
    frequencyTo: Int,
    callback: (String, String) -> Unit,
): MaterialDialog = remember { MaterialDialog() }.apply {
    BuiltTwoInputDialog(
        title = stringResource(R.string.alert_title_frequency_generation_tasks),
        message = stringResource(R.string.alert_message_frequency_generation_tasks),
        prefill1 = frequencyFrom.toString(),
        prefill2 = frequencyTo.toString(),
        label1 = "От",
        label2 = "До",
        hint1 = FREQUENCY_GENERATE_FROM.toString(),
        hint2 = FREQUENCY_GENERATE_TO.toString(),
        callback = callback,
        isTextValid1 = { s: String -> s.isDigitsOnly() },
        isTextValid2 = { s: String -> s.isDigitsOnly() && s.isNotEmpty() && s != "0" },
        isTextValidGeneral = { s1, s2 -> s1.toIntOrNull() ?: 0 < s2.toIntOrNull() ?: 0 },
        error = stringResource(R.string.toast_settings_s_task_frequency_error)
    )
}

/** ============================================ Preview ========================================================== */

@ExperimentalComposeUiApi
@ExperimentalMaterialApi
@Preview
@Composable
fun SettingsSingleTaskFrequencyUiPreview() {
    SettingsSingleTaskFrequencyUi(SettingsSingleTask()) {}
}