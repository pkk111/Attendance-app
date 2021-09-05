package com.pkk.android.attendance.connectionSetup

import android.content.Context
import android.util.Log
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*
import com.pkk.android.attendance.interfaces.ConnectionCallbackListener
import com.pkk.android.attendance.interfaces.ConnectionStatusListeners
import com.pkk.android.attendance.misc.CentralVariables
import com.pkk.android.attendance.misc.SharedPref.Companion.getString
import com.pkk.android.attendance.misc.Utils.Companion.showShortToast
import com.pkk.android.attendance.models.DeviceModel
import java.util.*

class ConnectionCallback(
    var context: Context,
    var payloadCallback: PayloadCallback,
    var listeners: ConnectionStatusListeners
) {

    private val localUserName: String
        get() = getString(context, CentralVariables.KEY_DISCOVERER_NAME, "")!!

    //Callback for discovery of devices
    fun getDiscoveryCallBack(deviceCallbackListener: ConnectionCallbackListener): EndpointDiscoveryCallback {

        return object : EndpointDiscoveryCallback() {
            override fun onEndpointFound(endpointId: String, info: DiscoveredEndpointInfo) {
                // An endpoint was found. We request a connection to it.
                names!![endpointId] = info.endpointName
                deviceCallbackListener.onDeviceDetected(
                    DeviceModel(
                        endpointId,
                        names!![endpointId]
                    )
                )
            }

            override fun onEndpointLost(endpointId: String) {
                // A previously discovered endpoint has gone away.
                names!!.remove(endpointId)
                deviceCallbackListener.onDeviceLost(endpointId)
            }
        }
    }

    fun requestConnection(device: DeviceModel) {
        Nearby.getConnectionsClient(context)
            .requestConnection(localUserName, device.endpointID, connectionLifecycleCallback)
            .addOnSuccessListener { Log.d("CONNECTION", "Connection requested successfully") }
            .addOnFailureListener { e: Exception ->
                listeners.onConnectionErrorOccurred(
                    "Error in requesting connection",
                    e
                )
            }
    }

    //Callback for the connection send and received
    @JvmField
    var connectionLifecycleCallback: ConnectionLifecycleCallback =
        object : ConnectionLifecycleCallback() {
            override fun onConnectionInitiated(endpointId: String, connectionInfo: ConnectionInfo) {
                names!![endpointId] = connectionInfo.endpointName
                Nearby.getConnectionsClient(context).acceptConnection(endpointId, payloadCallback)
            }

            override fun onConnectionResult(endpointId: String, result: ConnectionResolution) {
                when (result.status.statusCode) {
                    ConnectionsStatusCodes.STATUS_OK -> {
                        listeners.onConnectionEstablished(
                            DeviceModel(
                                endpointId,
                                names!![endpointId]
                            )
                        )
                    }
                    ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED -> {
                        names!!.remove(endpointId)
                        showShortToast(context, "Connection Denied")
                    }
                    ConnectionsStatusCodes.STATUS_ERROR -> {
                        names!!.remove(endpointId)
                        showShortToast(context, "Error occurred, Please try again")
                    }
                    else -> {
                    }
                }
            }

            override fun onDisconnected(endpointId: String) {
                names!!.remove(endpointId)
                listeners.onConnectionDisconnected(endpointId)
            }
        }

    companion object {
        var names: HashMap<String, String>? = HashMap()
    }
}