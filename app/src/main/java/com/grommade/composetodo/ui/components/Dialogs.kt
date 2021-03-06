package com.grommade.composetodo.ui.components

import android.widget.Toast
import androidx.compose.material.Divider
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.grommade.composetodo.add_classes.MyCalendar
import com.grommade.composetodo.util.MINUTES_IN_HOUR
import com.vanpra.composematerialdialogs.*
import com.vanpra.composematerialdialogs.datetime.datepicker.datepicker
import com.vanpra.composematerialdialogs.datetime.datetimepicker
import com.vanpra.composematerialdialogs.datetime.timepicker.timepicker
import java.time.*

@Composable
fun MaterialDialog.BuiltDateDialog(callback: (MyCalendar) -> Unit) {
    build {
        datepicker { date ->
            callback(MyCalendar(date.toMillis()))
        }
        SetButtonsOkCancel()
    }
}

@Composable
fun MaterialDialog.BuiltTimeDialog(initialTime: Int = 0, callback: (Int) -> Unit) {
//    title(stringResource(R.string.alert_title_time_picker)) // FIXME: Не работает
    build {
        timepicker(initialTime = initialTime.minutesToLocalTime()) { time ->
            callback(time.toMinutes())
        }
        SetButtonsOkCancel()
    }
}

@Composable
fun MaterialDialog.BuiltDateTimeDialog(initialTime: MyCalendar, callback: (MyCalendar) -> Unit) {
    build {
        datetimepicker(initialDateTime = initialTime.toLocalDateTime()) { dateTime ->
            callback(MyCalendar(dateTime.toMillis() ?: 0L))
        }
    }
}

private fun LocalDateTime.toMillis(zone: ZoneId = ZoneId.systemDefault()): Long? =
    atZone(zone)?.toInstant()?.toEpochMilli()

private fun LocalDate.toMillis(zone: ZoneId = ZoneId.systemDefault()): Long =
    atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()

private fun Int.minutesToLocalTime(): LocalTime =
    LocalTime.of(this / MINUTES_IN_HOUR, this % MINUTES_IN_HOUR)


private fun MyCalendar.toLocalDateTime(): LocalDateTime =
    Instant.ofEpochMilli(this.milli).atZone(ZoneId.systemDefault()).toLocalDateTime()


private fun LocalTime.toMinutes(): Int = hour * MINUTES_IN_HOUR + minute

@ExperimentalComposeUiApi
@Composable
fun MaterialDialog.BuiltInputDialog(
    title: String,
    message: String = "",
    prefill: String = "",
    label: String = "",
    hint: String = "",
    isTextValid: (String) -> Boolean = { true },
    callback: (String) -> Unit,
) {
    build {
        SetTitle(title, message)
        Divider(color = Color.Transparent, thickness = 16.dp)
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

@ExperimentalComposeUiApi
@Composable
fun MaterialDialog.BuiltTwoInputDialog(
    title: String,
    message: String = "",
    prefill1: String = "",
    prefill2: String = "",
    label1: String = "",
    label2: String = "",
    hint1: String = "",
    hint2: String = "",
    callback: (String, String) -> Unit = { _, _ -> },
    isTextValid1: (String) -> Boolean = { true },
    isTextValid2: (String) -> Boolean = { true },
    isTextValidGeneral: (String, String) -> Boolean = { _, _ -> true },
    error: String = "",
) {
    var value1 by remember { mutableStateOf(prefill1) }
    var value2 by remember { mutableStateOf(prefill2) }

    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val onClickOK = {
        if (isTextValidGeneral(value1, value2)) {
            callback(value1, value2)
            hide(focusManager)
        } else {
            Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
        }
    }
    build {
        SetTitle(title, message)
        input(
            label = label1,
            hint = hint1,
            prefill = prefill1,
            isTextValid = isTextValid1,
            waitForPositiveButton = false
        ) {
            value1 = it
        }
        Divider(color = Color.Transparent, thickness = 16.dp)
        input(
            label = label2,
            hint = hint2,
            prefill = prefill2,
            isTextValid = isTextValid2,
            waitForPositiveButton = false
        ) {
            value2 = it
        }
        buttons {
            positiveButton("Ok", onClick = onClickOK, disableDismiss = true)
            negativeButton("Cancel")
        }
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
fun MaterialDialog.BuiltSMultipleChoiceDialog(
    title: String,
    message: String = "",
    list: List<String>,
    initialSelection: List<Int> = emptyList(),
    callback: (List<Int>) -> Unit
) {
    build {
        SetTitle(title, message)
        listItemsMultiChoice(
            list = list,
            initialSelection = initialSelection
        ) { indexes ->
            callback(indexes.sortedBy { it })
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

@ExperimentalComposeUiApi
@Preview
@Composable
fun BuiltTwoInputDialogPreview() {
    MaterialDialog().BuiltTwoInputDialog("Поля ввода")
}