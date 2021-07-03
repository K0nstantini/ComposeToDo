package com.grommade.composetodo.history

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.grommade.composetodo.R
import com.grommade.composetodo.add_classes.MyCalendar
import com.grommade.composetodo.db.entity.History
import com.grommade.composetodo.ui.components.TopBarStandard

@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel = hiltViewModel(),
    navController: NavHostController
) {
    with(viewModel) {
        HistoryScreenBody(
            histories = histories.collectAsState(emptyList()).value,
            onBack = { navController.navigateUp() }
        )
    }
}

@Composable
private fun HistoryScreenBody(
    histories: List<History> = emptyList(),
    onBack: () -> Unit = {}
) {
    Scaffold(
        topBar = { TopBarStandard(stringResource(R.string.nav_history), onBack) }
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
fun HistoryItem(
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