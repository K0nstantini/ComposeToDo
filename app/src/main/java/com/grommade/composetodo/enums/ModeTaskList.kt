package com.grommade.composetodo.enums

import androidx.annotation.StringRes
import com.grommade.composetodo.R

enum class ModeTaskList(
//    @MenuRes val menu: Int?,
    @StringRes val titleSingleTask: Int,
    @StringRes val titleRegularTask: Int,
    val showAddBtn: Boolean,
    val supportLongClick: Boolean
) {

    DEFAULT(
//        null,
        R.string.title_single_task_list,
        R.string.title_regular_task_list,
        true,
        true
    ),
    SELECT_CATALOG(
//        R.menu.confirm_menu,
        R.string.title_select_catalog,
        R.string.title_select_catalog,
        false,
        false
    ),
    SELECT_TASK(
//        R.menu.confirm_menu,
        R.string.title_select_task,
        R.string.title_select_task,
        false,
        false
    )
}