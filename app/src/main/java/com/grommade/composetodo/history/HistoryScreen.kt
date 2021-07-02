package com.grommade.composetodo.history

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
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
        LazyColumn() {
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
    Row() {
        Text(date.toString())
        Text(text)
    }
}

@Preview
@Composable
fun HistoryScreenPreview() {
    HistoryScreenBody()
}