package com.grommade.composetodo.statistics

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.grommade.composetodo.R

@Composable
fun StatisticsScreen(
    viewModel: StatisticsViewModel = hiltViewModel(),
    navController: NavHostController = rememberNavController()
) {
    val onBack: () -> Unit = { navController.navigateUp() }

    with(viewModel) {
        StatisticsScreenBody(
            regularPoints = regularPoints.collectAsState().value,
            singlePoints = singlePoints.collectAsState().value,
            onBack = onBack
        )
    }
}

@Composable
private fun StatisticsScreenBody(
    regularPoints: Int = 0,
    singlePoints: Int = 0,
    onBack: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.title_statistic)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, "")
                    }
                },
            )
        }
    ) {
        Column(modifier = Modifier.padding(start = 16.dp)) {
            Text(
                text = stringResource(R.string.statistic_title_points),
            style = MaterialTheme.typography.h6.copy(fontSize = 18.sp)
            )
            Column(modifier = Modifier.padding(start = 20.dp)) {
                Text(stringResource(R.string.statistic_text_count_points_s_tasks, regularPoints))
                Text(stringResource(R.string.statistic_text_count_points_r_tasks, singlePoints))
            }

        }
    }
}

@Preview
@Composable
fun StatisticsScreenPreview() {
    StatisticsScreenBody()
}