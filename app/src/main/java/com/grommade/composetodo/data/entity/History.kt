package com.grommade.composetodo.data.entity

import androidx.compose.runtime.Immutable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.grommade.composetodo.add_classes.MyCalendar

@Entity(tableName = "history_table")
@Immutable
data class History(
    @PrimaryKey(autoGenerate = true) override val id: Long = 0,
    val date: MyCalendar = MyCalendar(),
    val value: String = ""
) : AppEntity {
    val isNew: Boolean
        get() = (id == 0L)
}