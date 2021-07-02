package com.grommade.composetodo.enums

import androidx.annotation.StringRes
import com.grommade.composetodo.R

enum class DialogSelectPeriod(@StringRes val title: Int) {
    FROM(R.string.choice_select_period_from),
    TO(R.string.choice_select_period_to),
    NO_RESTRICTIONS(R.string.choice_select_period_no_period);

    companion object {
        fun toList() = values().map { it.title }
    }
}

enum class DialogSelectDays(@StringRes val title: Int) {
    NO_RESTRICTIONS(R.string.choice_select_days_no_days),
    DAYS_OF_WEEK(R.string.choice_select_days_days_of_week),
    EVERY_FEW_DAYS(R.string.choice_select_days_every_few_days);

    companion object {
        fun toList() = values().map { it.title }
    }
}

enum class DialogDaysOfWeek(
    @StringRes val title: Int,
    @StringRes val abbr: Int,
) {
    MONDAY(R.string.monday, R.string.abbr_monday),
    TUESDAY(R.string.tuesday, R.string.abbr_tuesday),
    WEDNESDAY(R.string.wednesday, R.string.abbr_wednesday),
    THURSDAY(R.string.thursday, R.string.abbr_thursday),
    FRIDAY(R.string.friday, R.string.abbr_friday),
    SATURDAY(R.string.saturday, R.string.abbr_saturday),
    SUNDAY(R.string.sunday, R.string.abbr_sunday);

    companion object {
        fun toList() = values().map { it.title }
    }
}