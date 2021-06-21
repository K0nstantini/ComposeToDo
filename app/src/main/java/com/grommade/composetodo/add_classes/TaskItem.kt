package com.grommade.composetodo.add_classes

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import com.grommade.composetodo.db.entity.Task

data class TaskItem(
    val id: Long,
    val name: String,
    val padding: Int,
    val icon: ImageVector,
    val fontSize: Int,
    val fontWeight: FontWeight,
)