package com.grommade.composetodo.util.extensions

import androidx.navigation.NavController
import com.grommade.composetodo.TasksRoute

fun NavController.toSingleTask(id: Long) = navigate(TasksRoute.SingleTaskChildRoute.createRoute(id))