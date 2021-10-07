package com.pkk.android.attendance.connectionSetup

import android.app.Activity
import android.app.AlertDialog
import android.util.Log
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.DiscoveryOptions
import com.google.android.gms.nearby.connection.Strategy
import com.pkk.android.attendance.connectionSetup.PayloadHandler.ReceiveBytesPayloadListener
import com.pkk.android.attendance.interfaces.ConnectionCallbackListener
import com.pkk.android.attendance.interfaces.ConnectionEstablishedListener
import com.pkk.android.attendance.interfaces.ConnectionStatusListeners
import com.pkk.android.attendance.interfaces.PayloadCallbackListener
import com.pkk.android.attendance.misc.CentralVariables
import com.pkk.android.attendance.misc.SharedPref
import com.pkk.android.attendance.misc.Utils.Companion.showShortToast
import com.pkk.android.attendance.models.DeviceModel

class Discoverer(
    private val context: Activity,
    private val strategy: Strategy,
    private val connectionEstablishedListener: ConnectionEstablishedListener,
    private val connectionCallbackListener: ConnectionCallbackListener
) : ConnectionStatusListeners {

    private var connectionCallback: ConnectionCallback? = null
    private var connectedDevice: DeviceModel? = null

    fun startDiscovering(payloadCallbackListener: PayloadCallbackListener) {
        connectionCallback =
            ConnectionCallback(context, ReceiveBytesPayloadListener(payloadCallbackListener), this)
        val discoveryOptions =
            DiscoveryOptions.Builder().setStrategy(strategy).build()
        Nearby.getConnectionsClient(context)
            .startDiscovery(
                CentralVariables.SERVICE_ID,
                connectionCallback!!.getDiscoveryCallBack(connectionCallbackListener),
                discoveryOptions
            )
            .addOnSuccessListener { Log.d("discovery", "successfully started discovering") }
            .addOnFailureListener { e: Exception ->
                showShortToast(context, "Failure in discovering devices")
                Log.e("discovery", "Failure in discovering, exception: $e")
            }
    }

    fun stopDiscovering() {
        Nearby.getConnectionsClient(context).stopAdvertising()
        Nearby.getConnectionsClient(context).stopAllEndpoints()
    }

    fun requestConnection(device: DeviceModel) {
        connectionCallback!!.requestConnection(
            device,
            SharedPref.getString(context, CentralVariables.KEY_USERNAME, "")!!
        )
    }

    fun sendMessage(message: String) {
        PayloadHandler(context).sendString(connectedDevice!!.endpointID, message)
    }

    override fun onConnectionRequested(device: DeviceModel, authDigits: String) {
        connectionCallback?.acceptConnectionRequest(device)
        if (strategy == Strategy.P2P_POINT_TO_POINT) {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Connection Code: $authDigits")
            builder.setPositiveButton("Ok") { _, _ ->
            }
            builder.show()
        }

    }

    override fun onConnectionEstablished(device: DeviceModel) {
        connectedDevice = device
        connectionEstablishedListener.onConnectionEstablished()
    }

    override fun onConnectionErrorOccurred(part: String, e: Exception) {
        Log.e("Error", "$part: $e")
        showShortToast(context, "Error occurred, Please Try Again")
    }

    override fun onConnectionDisconnected(endpoint: String) {
        if (connectedDevice!!.endpointID == endpoint)
            this.connectedDevice = null
    }
}