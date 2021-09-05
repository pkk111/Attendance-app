package com.pkk.android.attendance.connectionSetup

import android.app.NotificationManager
import android.content.Context
import android.util.Log
import androidx.collection.SimpleArrayMap
import androidx.core.app.NotificationCompat
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.Payload
import com.google.android.gms.nearby.connection.PayloadCallback
import com.google.android.gms.nearby.connection.PayloadTransferUpdate
import com.pkk.android.attendance.interfaces.PayloadCallbackListener
import java.io.File
import java.io.InputStream

class PayloadHandler(private val context: Context) {

    class ReceiveWithProgressCallback(private val context: Context) : PayloadCallback() {
        private val incomingPayloads = SimpleArrayMap<Long, NotificationCompat.Builder>()
        private val outgoingPayloads = SimpleArrayMap<Long, NotificationCompat.Builder>()
        var notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        private fun sendPayload(endpointId: String, payload: Payload) {
            if (payload.type == Payload.Type.BYTES) {
                // No need to track progress for bytes.
                return
            }

            // Build and start showing the notification.
            val notification = buildNotification(payload,  /*isIncoming=*/false)
            notificationManager.notify(payload.id.toInt(), notification.build())

            // Add it to the tracking list so we can update it.
            outgoingPayloads.put(payload.id, notification)
        }

        private fun buildNotification(
            payload: Payload,
            isIncoming: Boolean
        ): NotificationCompat.Builder {
            val notification = NotificationCompat.Builder(context, "PAYLOAD")
                .setContentTitle(if (isIncoming) "Receiving..." else "Sending...")
            var indeterminate = false
            if (payload.type == Payload.Type.STREAM) {
                // We can only show indeterminate progress for stream payloads.
                indeterminate = true
            }
            notification.setProgress(100, 0, indeterminate)
            return notification
        }

        override fun onPayloadReceived(endpointId: String, payload: Payload) {
            if (payload.type == Payload.Type.BYTES) {
                // No need to track progress for bytes.
                return
            }

            // Build and start showing the notification.
            val notification = buildNotification(payload, true /*isIncoming*/)
            notificationManager.notify(payload.id.toInt(), notification.build())

            // Add it to the tracking list so we can update it.
            incomingPayloads.put(payload.id, notification)
        }

        override fun onPayloadTransferUpdate(endpointId: String, update: PayloadTransferUpdate) {
            val payloadId = update.payloadId
            var notification: NotificationCompat.Builder? = null
            if (incomingPayloads.containsKey(payloadId)) {
                notification = incomingPayloads[payloadId]
                if (update.status != PayloadTransferUpdate.Status.IN_PROGRESS) {
                    // This is the last update, so we no longer need to keep track of this notification.
                    incomingPayloads.remove(payloadId)
                }
            } else if (outgoingPayloads.containsKey(payloadId)) {
                notification = outgoingPayloads[payloadId]
                if (update.status != PayloadTransferUpdate.Status.IN_PROGRESS) {
                    // This is the last update, so we no longer need to keep track of this notification.
                    outgoingPayloads.remove(payloadId)
                }
            }
            if (notification == null) {
                return
            }
            when (update.status) {
                PayloadTransferUpdate.Status.IN_PROGRESS -> {
                    val size = update.totalBytes
                    if (size == -1L) {
                        // This is a stream payload, so we don't need to update anything at this point.
                        return
                    }
                    val percentTransferred =
                        (100.0 * (update.bytesTransferred / update.totalBytes.toDouble())).toInt()
                    notification.setProgress(100, percentTransferred,  /* indeterminate= */false)
                }
                PayloadTransferUpdate.Status.SUCCESS ->                     // SUCCESS always means that we transferred 100%.
                    notification
                        .setProgress(100, 100,  /* indeterminate= */false)
                        .setContentText("Transfer complete!")
                PayloadTransferUpdate.Status.FAILURE, PayloadTransferUpdate.Status.CANCELED -> notification.setProgress(
                    0,
                    0,
                    false
                ).setContentText("Transfer failed")
                else -> {
                }
            }
            notificationManager.notify(payloadId.toInt(), notification.build())
        }
    }

    class ReceiveBytesPayloadListener(private val listeners: PayloadCallbackListener) :
        PayloadCallback() {
        override fun onPayloadReceived(endpointId: String, payload: Payload) {
            // This always gets the full data of the payload. Is null if it's not a BYTES payload.
            if (payload.type == Payload.Type.BYTES) {
                val receivedBytes = payload.asBytes()
                val message = String(receivedBytes!!)
                listeners.onPayloadReceived(message, endpointId)
            }
        }

        override fun onPayloadTransferUpdate(endpointId: String, update: PayloadTransferUpdate) {
            // Bytes payloads are sent as a single chunk, so you'll receive a SUCCESS update immediately
            // after the call to onPayloadReceived().
        }
    }

    private fun sendPayload(endpointId: String, payload: Payload) {
        Nearby.getConnectionsClient(context).sendPayload(endpointId, payload)
            .addOnSuccessListener { Log.d("Payload", "Payload send") }
    }

    fun sendString(endpointId: String, message: String) {
        val payload = Payload.fromBytes(message.toByteArray())
        sendPayload(endpointId, payload)
    }

    fun sendFile(endpointId: String, messageFile: File) {
        val payload = Payload.fromFile(messageFile)
        sendPayload(endpointId, payload)
    }

    fun sendStream(endpointId: String, messageStream: InputStream) {
        val payload = Payload.fromStream(messageStream)
        sendPayload(endpointId, payload)
    }

}