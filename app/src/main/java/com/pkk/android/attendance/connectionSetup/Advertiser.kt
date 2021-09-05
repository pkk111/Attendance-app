package com.pkk.android.attendance.connectionSetup

import android.app.Activity
import android.util.Log
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.AdvertisingOptions
import com.pkk.android.attendance.connectionSetup.PayloadHandler.ReceiveBytesPayloadListener
import com.pkk.android.attendance.interfaces.ConnectionStatusListeners
import com.pkk.android.attendance.interfaces.PayloadCallbackListener
import com.pkk.android.attendance.misc.CentralVariables
import com.pkk.android.attendance.misc.CentralVariables.SERVICE_ID
import com.pkk.android.attendance.misc.CentralVariables.STRATEGY
import com.pkk.android.attendance.misc.SharedPref.Companion.getString
import com.pkk.android.attendance.misc.Utils.Companion.showShortToast
import com.pkk.android.attendance.models.DeviceModel
import java.util.*

class Advertiser(private val activity: Activity) : ConnectionStatusListeners {

    private val endpoints: MutableList<DeviceModel>
    private var connectionCallback: ConnectionCallback? = null
    private val hostUsername: String?
        get() = getString(activity, CentralVariables.KEY_HOST_NAME, "")

    fun startAdvertising(payloadCallbackListener: PayloadCallbackListener) {
        connectionCallback =
            ConnectionCallback(activity, ReceiveBytesPayloadListener(payloadCallbackListener), this)
        val advertisingOptions = AdvertisingOptions.Builder().setStrategy(STRATEGY).build()
        Nearby.getConnectionsClient(activity)
            .startAdvertising(
                hostUsername!!,
                SERVICE_ID,
                connectionCallback!!.connectionLifecycleCallback,
                advertisingOptions
            )
            .addOnSuccessListener {
                Log.d("TeacherActivity", "Advertising started")
            }
            .addOnFailureListener { e: Exception? ->
                showShortToast(activity, "Error occurred while advertising, Try Again")
                Log.e("Advertising", "Error while advertising $e")
            }
    }

    fun stopAdvertising() {
        Nearby.getConnectionsClient(activity).stopAdvertising()
        Nearby.getConnectionsClient(activity).stopAllEndpoints()
    }

    override fun onConnectionEstablished(device: DeviceModel) {
        endpoints.add(device)
    }

    override fun onConnectionErrorOccurred(part: String, e: Exception) {
        Log.e("Error", "$part: $e")
        showShortToast(activity, "Error occurred, Please Try Again")
    }

    override fun onConnectionDisconnected(endpoint: String) {
        for (x in endpoints)
            if (x.endpointID == endpoint) {
                endpoints.remove(x)
                break
            }
    }

    init {
        endpoints = ArrayList()
    }
}