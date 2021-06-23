package com.grommade.composetodo.settings

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

class SettingItem(@StringRes val title: Int) {

    @StringRes
    private var valueR = 0
    private var value = ""

    private var _action: () -> Unit = {}
    val action: () -> Unit get() = _action

    private var _showSwitch = false
    val showSwitch: Boolean get() = _showSwitch

    private var _stateSwitch = false
    val stateSwitch: Boolean get() = _stateSwitch

    private var _actionSwitch: (Boolean) -> Unit = {}
    val actionSwitch: (Boolean) -> Unit get() = _actionSwitch

    private var _showClear = false
    val showClear: Boolean get() = _showClear

    private var _actionClear: () -> Unit = {}
    val actionClear: () -> Unit get() = _actionClear

    @Composable
    fun getValue() = when {
        valueR == 0 -> value
        value.isEmpty() -> stringResource(valueR)
        else -> value.replace("*R.string*", stringResource(valueR))
    }

    fun setValue(str: String? = "", @StringRes res: Int = 0, default: String = "") =
        this.apply {
            value = str ?: default
            valueR = res
        }

    fun setAction(callback: () -> Unit) =
        this.apply { _action = callback }

    fun setSwitch(callback: (Boolean) -> Unit, state: Boolean = false) =
        this.apply {
            _actionSwitch = callback
            _showSwitch = true
            _stateSwitch = state
        }


    fun setStateSwitch(state: Boolean) =
        this.apply {
            _showSwitch = true
            _stateSwitch = state
        }

    fun setClear(callback: () -> Unit, show: Boolean = true) =
        this.apply {
            _showClear = show
            _actionClear = callback
        }

    fun setShowClear(value: Boolean) =
        this.apply { _showClear = value }

}