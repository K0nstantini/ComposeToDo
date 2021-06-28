package com.grommade.composetodo.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.res.stringResource
import com.grommade.composetodo.R
import com.grommade.composetodo.add_classes.MyCalendar
import com.grommade.composetodo.util.minutesToLocalTime
import com.grommade.composetodo.util.toMinutes
import com.vanpra.composematerialdialogs.*
import com.vanpra.composematerialdialogs.datetime.datepicker.datepicker
import com.vanpra.composematerialdialogs.datetime.timepicker.timepicker
import java.time.ZoneId

@Composable
fun MaterialDialog.BuiltDateDialog(callback: (MyCalendar) -> Unit) {
    build {
        datepicker { date ->
            val milli = date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
            callback(MyCalendar(milli))
        }
        SetButtonsOkCancel()
    }
}

@Composable
fun MaterialDialog.BuiltTimeDialog(initialTime: Int = 0, callback: (Int) -> Unit) {
    title(stringResource(R.string.alert_title_time_picker)) // FIXME: Не работает
    build {
        timepicker(initialTime = initialTime.minutesToLocalTime()) { time ->
            callback(time.toMinutes())
        }
        SetButtonsOkCancel()
    }
}

@ExperimentalComposeUiApi
@Composable
fun MaterialDialog.BuiltInputDialog(
    title: String,
    message: String = "",
    prefill: String = "",
    label: String = "",
    hint: String = "",
    callback: (String) -> Unit,
    isTextValid: (String) -> Boolean = { true }
) {
    build {
        SetTitle(title, message)
        input(
            label = label,
            hint = hint,
            prefill = prefill,
            isTextValid = isTextValid
        ) {
            callback(it)
        }
        SetButtonsOkCancel()
    }
}

@Composable
fun MaterialDialog.BuiltSimpleOkDialog(
    title: String,
    message: String = "",
) {
    build {
        SetTitle(title, message)
        buttons {
            positiveButton("Ok")
        }
    }
}

@Composable
fun MaterialDialog.BuiltSimpleOkCancelDialog(
    title: String,
    message: String = "",
    callback: () -> Unit
) {
    build {
        SetTitle(title, message)
        SetButtonsOkCancel(callback)
    }
}

@Composable
fun MaterialDialog.BuiltSingleChoiceDialog(
    title: String,
    message: String = "",
    list: List<String>,
    initialSelection: Int? = null,
    callback: (Int) -> Unit
) {
    build {
        SetTitle(title, message)
        listItemsSingleChoice(
            list = list,
            initialSelection = initialSelection
        ) {
            callback(it)
        }
        SetButtonsOkCancel()
    }
}

@Composable
fun MaterialDialog.BuiltListDialog(
    title: String,
    message: String = "",
    list: List<String>,
    callback: (Int) -> Unit
) {
    build {
        SetTitle(title, message)
        listItems(list = list) { index, _ ->
            callback(index)
        }
    }
}

@Composable
private fun MaterialDialog.SetTitle(
    title: String,
    message: String = "",
) {
    title(title)
    if (message.isNotEmpty()) {
        message(message)
    }
}

@Composable
private fun MaterialDialog.SetButtonsOkCancel(
    onClickOK: () -> Unit = {}
) {
    buttons {
        positiveButton("Ok", onClick = onClickOK)
        negativeButton("Cancel")
    }
}