package com.grommade.composetodo.enums

import androidx.annotation.StringRes
import com.grommade.composetodo.R

enum class TypeTask(
    @StringRes val title: Int,
    val regular: Boolean,
) {
    IMPORTANT(R.string.choice_type_task_important, false),
    UNIMPORTANT(R.string.choice_type_task_unimportant, false),
    SHORT_REGULAR_TASK(R.string.choice_type_task_long_regular, true),
    LONG_REGULAR_TASK(R.string.choice_type_task_short_regular, true),
    CONTAINER_TASK(R.string.choice_type_task_container, true)

}