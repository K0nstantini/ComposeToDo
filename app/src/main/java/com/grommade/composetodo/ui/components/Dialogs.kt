package com.grommade.composetodo.ui.components

import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.core.text.isDigitsOnly
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
        buttons {
            positiveButton("Ok")
            negativeButton("Cancel")
        }
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
        title(title)
        if (message.isNotEmpty()) {
            message(message)
        }
        input(
            label = label,
            hint = hint,
            prefill = prefill,
            waitForPositiveButton = true,
            isTextValid = isTextValid
        ) {
            callback(it)
        }
        buttons {
            positiveButton("Ok")
            negativeButton("Cancel")
        }
    }
}