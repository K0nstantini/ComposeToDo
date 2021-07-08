package com.grommade.composetodo.util.extensions

import androidx.navigation.NavController
import com.grommade.composetodo.SelectTaskRoute
import com.grommade.composetodo.SettingsRoute
import com.grommade.composetodo.SettingsSingleTaskRoute
import com.grommade.composetodo.TasksRoute

fun NavController.toSingleTask(id: Long) =
    navigate(TasksRoute.SingleTaskChildRoute.createRoute(id))

fun NavController.toSelectParent(id: Long) =
    navigate(SelectTaskRoute.SingleTaskSelectRoute.createRoute(id))

fun NavController.toGeneralSettings() =
    navigate(SettingsRoute.SettingsGeneralChildRoute.route)

fun NavController.toRegularSettings() =
    navigate(SettingsRoute.SettingsRegularTaskChildRoute.route)

fun NavController.toSingleSettings() =
    navigate(SettingsRoute.SettingsSingleTaskChildRoute.route)

fun NavController.toSettingsSingleTask() =
    navigate(SettingsSingleTaskRoute.SettingsSingleTaskFrequencyChildRoute.route)