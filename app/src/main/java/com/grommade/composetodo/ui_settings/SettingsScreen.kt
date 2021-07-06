package com.grommade.composetodo.ui_settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.grommade.composetodo.R
import com.grommade.composetodo.SettingsRoute
import com.grommade.composetodo.ui.components.NavigationBackIcon

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    navController: NavHostController = rememberNavController()
) {

    with(viewModel) {
        SettingsScreenBody(
            onClickGeneralSettings = { navController.navigate(SettingsRoute.SettingsGeneralChildRoute.route) },
            onClickRegularSettings = { navController.navigate(SettingsRoute.SettingsRegularTaskChildRoute.route) },
            onClickSingleSettings = { navController.navigate(SettingsRoute.SettingsSingleTaskChildRoute.route) },
            onBack = navController::navigateUp
        )
    }

}

@Composable
private fun SettingsScreenBody(
    onBack: () -> Unit = {},
    onClickGeneralSettings: () -> Unit = {},
    onClickRegularSettings: () -> Unit = {},
    onClickSingleSettings: () -> Unit = {},
) {
    Scaffold(
        topBar = {
            // TODO: Appbar вынести
            TopAppBar(
                title = { Text(stringResource(R.string.title_settings)) },
                navigationIcon = { NavigationBackIcon(onBack) },
            )
        }
    ) {
        Column(
            modifier = Modifier.padding(start = 16.dp, top = 16.dp)
        ) {
            SetItem(stringResource(R.string.settings_general_title), onClickGeneralSettings)
            SetItem(stringResource(R.string.settings_r_tasks_title), onClickRegularSettings)
            SetItem(stringResource(R.string.settings_s_tasks_title), onClickSingleSettings)
        }
    }
}

@Composable
private fun SetItem(
    text: String,
    callback: () -> Unit
) {
    Text(
        text = text,
        modifier = Modifier.clickable(onClick = callback),
        style = MaterialTheme.typography.h6.copy(fontSize = 18.sp, color = MaterialTheme.colors.secondaryVariant),
    )
    Divider(color = Color.Transparent, thickness = 16.dp)
}

@Preview
@Composable
fun SettingsScreenPreview() {
    SettingsScreenBody()
}