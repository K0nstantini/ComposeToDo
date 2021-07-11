package com.grommade.composetodo.data.entity

import androidx.compose.runtime.Immutable
import com.grommade.composetodo.add_classes.MyCalendar

@Immutable
data class DatesTask(
    val dateCreation: MyCalendar = MyCalendar.now(),
    val dateActivation: MyCalendar = MyCalendar(),
    val dateStart: MyCalendar = MyCalendar.today(),
    val dateEnd: MyCalendar = MyCalendar(),
    val dateBefore: MyCalendar = MyCalendar(),
) {
}