package com.grommade.composetodo.ui_statistics

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.grommade.composetodo.R
import com.grommade.composetodo.ui.common.rememberFlowWithLifecycle
import com.grommade.composetodo.ui.components.NavigationBackIcon

@Composable
fun StatisticsUi(navController: NavHostController) {

    StatisticsUi(
        viewModel = hiltViewModel(),
        navController = navController
    )
}

@Composable
fun StatisticsUi(
    viewModel: StatisticsViewModel,
    navController: NavHostController
) {
    val viewState by rememberFlowWithLifecycle(viewModel.state)
        .collectAsState(initial = StatisticsViewState.Empty)

    StatisticsUi(viewState) { navController.navigateUp() }
}

@Composable
fun StatisticsUi(
    viewState: StatisticsViewState,
    Close: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.title_statistic)) },
                navigationIcon = { NavigationBackIcon(Close) },
            )
        }
    ) {
        Column(modifier = Modifier.padding(start = 16.dp)) {
            Text(
                text = stringResource(R.string.statistic_title_points),
                style = MaterialTheme.typography.h6.copy(fontSize = 18.sp)
            )
            Column(modifier = Modifier.padding(start = 20.dp)) {
                Text(stringResource(R.string.statistic_text_count_points_r_tasks, viewState.singlePoints))
            }

        }
    }
}

@Preview
@Composable
fun StatisticsScreenPreview() {
    StatisticsUi(StatisticsViewState()) {}
}