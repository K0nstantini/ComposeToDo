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
import com.grommade.composetodo.util.extensions.toGeneralSettings
import com.grommade.composetodo.util.extensions.toRegularSettings
import com.grommade.composetodo.util.extensions.toSingleSettings

@Composable
fun SettingsUi(navController: NavHostController) {

    SettingsUi(
        viewModel = hiltViewModel(),
        navController = navController
    )
}

@Composable
fun SettingsUi(
    viewModel: SettingsViewModel,
    navController: NavHostController
) {
    SettingsUi { action ->
        when (action) {
            SettingsActions.GeneralSettings -> navController.toGeneralSettings()
            SettingsActions.RegularSettings -> navController.toRegularSettings()
            SettingsActions.SingleSettings -> navController.toSingleSettings()
            SettingsActions.Close -> navController.navigateUp()
        }
    }
}

@Composable
fun SettingsUi(
    actioner: (SettingsActions) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(R.string.title_settings))
                },
                navigationIcon = {
                    NavigationBackIcon { actioner(SettingsActions.Close) }
                },
            )
        }
    ) {
        Column(
            modifier = Modifier.padding(start = 16.dp, top = 16.dp)
        ) {
            SetItem(stringResource(R.string.settings_general_title)) { actioner(SettingsActions.GeneralSettings) }
            SetItem(stringResource(R.string.settings_r_tasks_title)) { actioner(SettingsActions.RegularSettings) }
            SetItem(stringResource(R.string.settings_s_tasks_title)) { actioner(SettingsActions.SingleSettings) }
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
    SettingsUi {}
}