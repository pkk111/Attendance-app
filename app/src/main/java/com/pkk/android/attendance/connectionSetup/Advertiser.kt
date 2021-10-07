package com.pkk.android.attendance.connectionSetup

import android.app.AlertDialog
import android.content.Context
import android.util.Log
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.AdvertisingOptions
import com.google.android.gms.nearby.connection.Strategy
import com.pkk.android.attendance.connectionSetup.PayloadHandler.ReceiveBytesPayloadListener
import com.pkk.android.attendance.interfaces.ConnectionStatusListeners
import com.pkk.android.attendance.interfaces.PayloadCallbackListener
import com.pkk.android.attendance.misc.CentralVariables
import com.pkk.android.attendance.misc.CentralVariables.SERVICE_ID
import com.pkk.android.attendance.misc.SharedPref.Companion.getString
import com.pkk.android.attendance.misc.Utils.Companion.showShortToast
import com.pkk.android.attendance.models.DeviceModel

class Advertiser(private val context: Context, private val strategy: Strategy) :
    ConnectionStatusListeners {

    private val endpoints: HashMap<String, DeviceModel> = HashMap()
    private var connectionCallback: ConnectionCallback? = null
    private val hostUsername: String?
        get() = getString(context, CentralVariables.KEY_USERNAME, "")

    fun startAdvertising(payloadCallbackListener: PayloadCallbackListener) {
        connectionCallback =
            ConnectionCallback(context, ReceiveBytesPayloadListener(payloadCallbackListener), this)
        val advertisingOptions = AdvertisingOptions.Builder().setStrategy(strategy).build()
        Nearby.getConnectionsClient(context)
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
                showShortToast(context, "Error occurred while advertising, Try Again")
                Log.e("Advertising", "Error while advertising $e")
            }
    }

    fun stopAdvertising() {
        Nearby.getConnectionsClient(context).stopAdvertising()
        Nearby.getConnectionsClient(context).stopAllEndpoints()
    }

    override fun onConnectionRequested(device: DeviceModel, authDigits: String) {
        if (strategy == Strategy.P2P_STAR)
            connectionCallback?.acceptConnectionRequest(device)
        else if (strategy == Strategy.P2P_POINT_TO_POINT) {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Connection Code: $authDigits")

            // Set up the buttons
            builder.setPositiveButton("OK") { _, _ ->
                connectionCallback?.acceptConnectionRequest(device)
            }
            builder.setNegativeButton("No") { _, _ ->
            }
            builder.show()
        }
    }

    override fun onConnectionEstablished(device: DeviceModel) {
        endpoints[device.endpointID] = device
    }

    override fun onConnectionErrorOccurred(part: String, e: Exception) {
        Log.e("Error", "$part: $e")
        showShortToast(context, "Error occurred, Please Try Again")
    }

    override fun onConnectionDisconnected(endpoint: String) {
        endpoints.remove(endpoint)
    }

    fun disconnect(endpoint: String) {
        Nearby.getConnectionsClient(context).disconnectFromEndpoint(endpoint)
        endpoints.remove(endpoint)
    }

    fun getDeviceMap(endpoint: String): DeviceModel {
        if (endpoints.containsKey(endpoint))
            return endpoints[endpoint]!!
        throw IllegalArgumentException("Unknown endpoint provided")
    }
}