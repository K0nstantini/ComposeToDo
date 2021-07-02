package com.grommade.composetodo.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.grommade.composetodo.add_classes.MyCalendar

@Entity(tableName = "history_table")
data class History(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: MyCalendar = MyCalendar(),
    val value: String = ""
) {
    val isNew: Boolean
        get() = (id == 0L)
}