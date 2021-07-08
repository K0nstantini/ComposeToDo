package com.grommade.composetodo.ui_history

sealed class HistoryActions {
    object Delete: HistoryActions()
    object Close: HistoryActions()
}