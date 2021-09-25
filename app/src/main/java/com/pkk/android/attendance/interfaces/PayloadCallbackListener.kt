package com.pkk.android.attendance.interfaces

interface PayloadCallbackListener {
    fun onPayloadReceived(message: String, endpointId: String?)
}