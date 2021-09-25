package com.pkk.android.attendance.viewModels

import androidx.lifecycle.ViewModel

class PulseLayoutViewModel : ViewModel() {

    private var _isRunning = false
    val isRunning get() = _isRunning

    fun setRunning(running: Boolean) {
        _isRunning = running
    }
}