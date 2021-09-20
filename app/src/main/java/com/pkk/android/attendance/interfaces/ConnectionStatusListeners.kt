package com.pkk.android.attendance.interfaces

import com.pkk.android.attendance.models.DeviceModel

interface ConnectionStatusListeners {
    fun onConnectionRequested(device: DeviceModel, authDigits: String)
    fun onConnectionEstablished(device: DeviceModel)
    fun onConnectionErrorOccurred(part: String, e: Exception)
    fun onConnectionDisconnected(endpoint: String)
}