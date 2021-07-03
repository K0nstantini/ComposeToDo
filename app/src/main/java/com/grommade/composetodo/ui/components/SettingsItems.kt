package com.grommade.composetodo.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.grommade.composetodo.R
import com.grommade.composetodo.add_classes.MyCalendar
import com.grommade.composetodo.ui.theme.ComposeToDoTheme
import timber.log.Timber

@ExperimentalMaterialApi
@Composable
fun SetItemDefault(
    title: String,
    value: String = "",
    enabled: Boolean = true,
    onClick: () -> Unit = {},
) {
    SetItemBody(
        title = title,
        value = value,
        enabled = enabled,
        onClick = onClick
    )
}

@ExperimentalMaterialApi
@Composable
fun SetItemWithClear(
    title: String,
    value: String = "",
    showClear: Boolean = true,
    enabled: Boolean = true,
    onClick: () -> Unit = {},
    onClickClear: () -> Unit = {}
) {
    SetItemBody(
        title = title,
        value = value,
        showClear = showClear,
        enabled = enabled,
        onClick = onClick,
        onClickClear = onClickClear
    )
}

@ExperimentalMaterialApi
@Composable
fun SetItemSwitch(
    title: String,
    value: String = "",
    stateSwitch: Boolean = false,
    enabled: Boolean = true,
    onClick: () -> Unit = {},
    onClickSwitch: (Boolean) -> Unit = {},
) {
    SetItemBody(
        title = title,
        value = value,
        showSwitch = true,
        enabled = enabled,
        stateSwitch = stateSwitch,
        onClick = onClick,
        onClickSwitch = onClickSwitch
    )
}

@ExperimentalMaterialApi
@Composable
private fun SetItemBody(
    title: String,
    value: String,
    showSwitch: Boolean = false,
    stateSwitch: Boolean = false,
    showClear: Boolean = false,
    enabled: Boolean = true,
    onClick: () -> Unit = {},
    onClickSwitch: (Boolean) -> Unit = {},
    onClickClear: () -> Unit = {}
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.padding(vertical = 8.dp),
        enabled = enabled
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column {
                Text(
                    text = title,
                    style = when (enabled) {
                        true -> MaterialTheme.typography.h6.copy(fontSize = 16.sp)
                        false -> MaterialTheme.typography.h6.copy(fontSize = 16.sp, color = Color.Gray)
                    }
                )
                Row(
                    modifier = if (showClear) Modifier.fillMaxWidth() else Modifier, // FIXME: WTF?
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (enabled) value else "-",
                        style = MaterialTheme.typography.body2.copy(color = Color.DarkGray)
                    )
                    if (showClear) {
                        Text(
                            text = stringResource(R.string.btn_clear),
                            style = MaterialTheme.typography.body2.copy(color = MaterialTheme.colors.primary),
                            modifier = Modifier.selectable(
                                selected = false,
                                onClick = onClickClear
                            )
                        )
                    }
                }
            }
            if (showSwitch) {
                Switch(
                    checked = stateSwitch,
                    onCheckedChange = onClickSwitch,
                    enabled = enabled
                )
            }
        }
    }
    Divider(thickness = 1.dp)
}

@Composable
fun SetItemTitle(text: String) {
    Divider(color = Color.Transparent, thickness = 4.dp)
    Text(
        text = text,
        style = MaterialTheme.typography.body2.copy(
            color = MaterialTheme.colors.secondaryVariant,
            fontWeight = FontWeight.Medium
        )
    )
}

@ExperimentalMaterialApi
@Preview
@Composable
fun SetItemDefaultPreview() {
    ComposeToDoTheme {
        SetItemDefault(
            title = stringResource(R.string.settings_add_single_task_title_date_start),
            value = MyCalendar.now().toString(false)
        )
    }
}

@ExperimentalMaterialApi
@Preview
@Composable
fun SetItemWithClearPreview() {
    ComposeToDoTheme {
        SetItemWithClear(
            title = stringResource(R.string.settings_add_task_title_parent),
            value = stringResource(R.string.settings_main_catalog_text)
        )
    }
}

@ExperimentalMaterialApi
@Preview
@Composable
fun SetItemSwitchPreview() {
    ComposeToDoTheme {
        SetItemSwitch(stringResource(R.string.settings_add_task_title_group))
    }
}

@ExperimentalMaterialApi
@Preview
@Composable
fun SetItemTitlePreview() {
    ComposeToDoTheme {
        Column {
            SetItemTitle(stringResource(R.string.settings_s_task_title_period))
            SetItemDefault(
                title = stringResource(R.string.settings_add_single_task_title_date_start),
                value = MyCalendar.now().toString(false)
            )
        }

    }
}