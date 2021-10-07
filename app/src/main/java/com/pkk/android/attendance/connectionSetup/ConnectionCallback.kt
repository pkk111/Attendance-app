package com.pkk.android.attendance.connectionSetup

import android.content.Context
import android.util.Log
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*
import com.pkk.android.attendance.interfaces.ConnectionCallbackListener
import com.pkk.android.attendance.interfaces.ConnectionStatusListeners
import com.pkk.android.attendance.misc.Utils.Companion.showShortToast
import com.pkk.android.attendance.models.DeviceModel
import java.util.*

class ConnectionCallback(
    var context: Context,
    private var payloadCallback: PayloadCallback,
    var listeners: ConnectionStatusListeners
) {

    //Callback for discovery of devices
    fun getDiscoveryCallBack(deviceCallbackListener: ConnectionCallbackListener): EndpointDiscoveryCallback {

        return object : EndpointDiscoveryCallback() {
            override fun onEndpointFound(endpointId: String, info: DiscoveredEndpointInfo) {
                // An endpoint was found. We request a connection to it.
                names[endpointId] = DeviceModel(
                    endpointId,
                    info.endpointName,
                    info.endpointInfo
                )
                deviceCallbackListener.onDeviceDetected(
                    names[endpointId]!!
                )
            }

            override fun onEndpointLost(endpointId: String) {
                // A previously discovered endpoint has gone away.
                names.remove(endpointId)
                deviceCallbackListener.onDeviceLost(endpointId)
            }
        }
    }

    fun requestConnection(device: DeviceModel, localUserName: String) {
        Nearby.getConnectionsClient(context)
            .requestConnection(localUserName, device.endpointID, connectionLifecycleCallback)
            .addOnSuccessListener { Log.d("CONNECTION", "Connection requested successfully") }
            .addOnFailureListener { e: Exception ->
                if ((e as ApiException).statusCode == 3) {
                    //TODO code to handled already connected mode
                }
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
                listeners.onConnectionRequested(
                    DeviceModel(
                        endpointId,
                        connectionInfo.endpointName,
                        connectionInfo.endpointInfo
                    ), connectionInfo.authenticationDigits
                )
            }

            override fun onConnectionResult(endpointId: String, result: ConnectionResolution) {
                when (result.status.statusCode) {
                    ConnectionsStatusCodes.STATUS_OK -> {
                        listeners.onConnectionEstablished(if (names.containsKey(endpointId)) names[endpointId]!! else DeviceModel())
                    }
                    ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED -> {
                        names.remove(endpointId)
                        showShortToast(context, "Connection Denied")
                    }
                    ConnectionsStatusCodes.STATUS_ERROR -> {
                        names.remove(endpointId)
                        showShortToast(context, "Error occurred, Please try again")
                    }
                    else -> {
                    }
                }
            }

            override fun onDisconnected(endpointId: String) {
                if (names.containsKey(endpointId)) {
                    names.remove(endpointId)
                    listeners.onConnectionDisconnected(endpointId)
                }
            }
        }

    fun acceptConnectionRequest(device: DeviceModel) {
        names[device.endpointID] = device
        Nearby.getConnectionsClient(context).acceptConnection(device.endpointID, payloadCallback)
    }

    companion object {
        var names: HashMap<String, DeviceModel> = HashMap()
    }
}