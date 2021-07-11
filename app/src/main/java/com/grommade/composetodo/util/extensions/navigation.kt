package com.grommade.composetodo.util.extensions

import androidx.navigation.NavController
import com.grommade.composetodo.*
import com.grommade.composetodo.enums.TypeTask

fun NavController.toAddEditTask(type: TypeTask, id: Long = -1) =
    navigate(TasksRoute.TaskChildRoute.createRoute(type, id))

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

fun NavController.toTypeTask(type: TypeTask) =
    navigate(TypeTaskRoute.TypeTaskChildRoute.createRoute(type.name))