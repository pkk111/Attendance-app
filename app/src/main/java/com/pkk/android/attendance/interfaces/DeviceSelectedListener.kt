package com.pkk.android.attendance.interfaces

import com.pkk.android.attendance.models.DeviceModel

interface DeviceSelectedListener {
    fun onDeviceSelected(device: DeviceModel)
}