package com.pkk.android.attendance.interfaces

import com.pkk.android.attendance.models.DeviceModel

interface ConnectionCallbackListener {
    fun onDeviceDetected(device: DeviceModel)
    fun onDeviceLost(endpoint: String)
}