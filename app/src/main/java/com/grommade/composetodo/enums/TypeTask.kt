package com.grommade.composetodo.enums

import androidx.annotation.StringRes
import com.grommade.composetodo.R

enum class TypeTask(
    @StringRes val title: Int,
    val regular: Boolean,
) {
    EXACT_TIME(R.string.choice_type_task_exact_time, false),
    RANDOM(R.string.choice_type_task_random, false),
    SHORT_REGULAR(R.string.choice_type_task_long_regular, true),
    LONG_REGULAR(R.string.choice_type_task_short_regular, true),
    CONTAINER(R.string.choice_type_task_container, true)

}