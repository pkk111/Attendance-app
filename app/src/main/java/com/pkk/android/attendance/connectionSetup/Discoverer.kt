package com.pkk.android.attendance.connectionSetup

import android.app.Activity
import android.util.Log
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.DiscoveryOptions
import com.pkk.android.attendance.connectionSetup.PayloadHandler.ReceiveBytesPayloadListener
import com.pkk.android.attendance.interfaces.ConnectionCallbackListener
import com.pkk.android.attendance.interfaces.ConnectionEstablishedListener
import com.pkk.android.attendance.interfaces.ConnectionStatusListeners
import com.pkk.android.attendance.interfaces.PayloadCallbackListener
import com.pkk.android.attendance.misc.CentralVariables
import com.pkk.android.attendance.misc.Utils.Companion.showShortToast
import com.pkk.android.attendance.models.DeviceModel

class Discoverer(
    private val activity: Activity,
    private val connectionEstablishedListener: ConnectionEstablishedListener,
    private val connectionCallbackListener: ConnectionCallbackListener
) : ConnectionStatusListeners {

    private var connectionCallback: ConnectionCallback? = null
    private var connectedDevice: DeviceModel? = null

    fun startDiscovering(payloadCallbackListener: PayloadCallbackListener) {
        connectionCallback =
            ConnectionCallback(activity, ReceiveBytesPayloadListener(payloadCallbackListener), this)
        val discoveryOptions =
            DiscoveryOptions.Builder().setStrategy(CentralVariables.STRATEGY).build()
        Nearby.getConnectionsClient(activity)
            .startDiscovery(
                CentralVariables.SERVICE_ID,
                connectionCallback!!.getDiscoveryCallBack(connectionCallbackListener),
                discoveryOptions
            )
            .addOnSuccessListener { Log.d("discovery", "successfully started discovering") }
            .addOnFailureListener { e: Exception ->
                showShortToast(activity, "Failure in discovering devices")
                Log.e("discovery", "Failure in discovering, exception: $e")
            }
    }

    fun stopDiscovering() {
        Nearby.getConnectionsClient(activity).stopAdvertising()
        Nearby.getConnectionsClient(activity).stopAllEndpoints()
    }

    fun requestConnection(device: DeviceModel) {
        connectionCallback!!.requestConnection(device)
    }

    fun sendMessage(message: String) {
        PayloadHandler(activity).sendString(connectedDevice!!.endpointID, message)
    }

    override fun onConnectionEstablished(device: DeviceModel) {
        connectedDevice = device
        connectionEstablishedListener.onConnectionEstablished()
    }

    override fun onConnectionErrorOccurred(part: String, e: Exception) {
        Log.e("Error", "$part: $e")
        showShortToast(activity, "Error occurred, Please Try Again")
    }

    override fun onConnectionDisconnected(endpoint: String) {
        if (connectedDevice!!.endpointID == endpoint)
            this.connectedDevice = null
    }
}