package com.grommade.composetodo.enums

import androidx.annotation.StringRes
import com.grommade.composetodo.R

enum class ModeGenerationSingleTasks(@StringRes val title: Int) {
    RANDOM(R.string.choice_mode_generation_s_tasks_random),
    FIXED(R.string.choice_mode_generation_s_tasks_fixed);

    companion object {
        fun toList() = values().map { it.title }
    }
}