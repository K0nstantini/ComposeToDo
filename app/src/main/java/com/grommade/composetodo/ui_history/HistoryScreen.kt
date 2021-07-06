package com.grommade.composetodo.ui_history

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.grommade.composetodo.R
import com.grommade.composetodo.add_classes.MyCalendar
import com.grommade.composetodo.data.entity.History
import com.grommade.composetodo.ui.components.BuiltSimpleOkCancelDialog
import com.vanpra.composematerialdialogs.MaterialDialog

@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel = hiltViewModel(),
    navController: NavHostController = rememberNavController(),
) {
    with(viewModel) {
        HistoryScreenBody(
            histories = histories.collectAsState(emptyList()).value,
            countRecords = countRecords.collectAsState().value,
            onDelete = ::onClickDeleteAll,
            onBack = navController::navigateUp
        )
    }
}

@Composable
private fun HistoryScreenBody(
    histories: List<History> = emptyList(),
    countRecords: Int = 0,
    onDelete: () -> Unit = {},
    onBack: () -> Unit = {}
) {
    Scaffold(
        topBar = { AppBar(
            countRecords = countRecords,
            onDelete = onDelete,
            onBack = onBack
        ) },
    ) {
        LazyColumn(
            modifier = Modifier.padding(start = 8.dp, end = 8.dp)
        ) {
            items(histories, key = { item -> item.id }) { item ->
                HistoryItem(item.date, item.value)
            }
        }
    }
}

@Composable
private fun AppBar(
    countRecords: Int,
    onDelete: () -> Unit,
    onBack: () -> Unit
) {
    val dialogDelete = remember { MaterialDialog() }.apply {
        BuiltSimpleOkCancelDialog(
            title = stringResource(R.string.alert_title_delete_history),
            callback = onDelete
        )
    }

    TopAppBar(
        title = { Text(stringResource(R.string.nav_history)) },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.Filled.ArrowBack, "")
            }
        },
        actions = {
            IconButton(onClick = dialogDelete::show, enabled = countRecords > 0) {
                Icon(Icons.Filled.Delete, "")
            }
        }
    )
}

@Composable
private fun HistoryItem(
    date: MyCalendar,
    text: String
) {
    Column(Modifier.padding(4.dp)) {
        Row {
            Text(date.toString(), color = Color.Blue)
            Spacer(modifier = Modifier.padding(4.dp))
            Text(text)
        }
        Divider()
    }
}

@Preview
@Composable
fun HistoryScreenPreview() {
    HistoryScreenBody(
        histories = listOf(
            History(id = 1, date = MyCalendar.now(), value = "Inserted new task: 'Task-1'"),
            History(id = 2, date = MyCalendar.now(), value = "Updated new task: 'Task-2'")
        )
    )
}