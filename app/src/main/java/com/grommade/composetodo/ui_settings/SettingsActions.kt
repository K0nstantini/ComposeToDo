package com.grommade.composetodo.ui_settings

sealed class SettingsActions {
    object GeneralSettings: SettingsActions()
    object RegularSettings: SettingsActions()
    object SingleSettings: SettingsActions()
    object Close: SettingsActions()
}