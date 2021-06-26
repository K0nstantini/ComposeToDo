package com.grommade.composetodo.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import com.grommade.composetodo.add_classes.MyCalendar
import com.vanpra.composematerialdialogs.*
import com.vanpra.composematerialdialogs.datetime.datepicker.datepicker
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
            waitForPositiveButton = true,
            isTextValid = isTextValid
        ) {
            callback(it)
        }
        SetButtonsOkCancel()
    }
}

@Composable
fun MaterialDialog.BuiltSimpleOkCancelDialog(
    title: String,
    message: String = "",
    onClick: () -> Unit
) {
    build {
        SetTitle(title, message)
        SetButtonsOkCancel(onClick)
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