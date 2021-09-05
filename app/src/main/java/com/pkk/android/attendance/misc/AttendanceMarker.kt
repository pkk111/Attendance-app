package com.pkk.android.attendance.misc

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.pkk.android.attendance.adapter.TeacherAdapter
import com.pkk.android.attendance.interfaces.PassDataListener
import com.pkk.android.attendance.models.MessageCodes
import com.pkk.android.attendance.models.MessageModel

class AttendanceMarker(
    private val context: Context,
    private val messageExtractor: MessageExtractor,
    private val adapter: TeacherAdapter,
    private val passDataListener: PassDataListener
) {

    private val gson: Gson = Gson()
    private var messageModel: MessageModel? = null

    fun markAttendance(inputJsonMessage: String): String {
        try {
            messageModel = gson.fromJson(inputJsonMessage, MessageModel::class.java)
        } catch (e: Exception) {
            Log.e("Error converting", "message is $inputJsonMessage\n\tException is $e")
        }
        val reply = processMessage(messageModel!!)
        return gson.toJson(reply)
    }

    private fun processMessage(m: MessageModel): MessageModel {
        val output = MessageModel()
        if (m.messageCodes == MessageCodes.CUSTOM) {
            passDataListener.passData(m.message)
        } else if (m.messageCodes == MessageCodes.NORMAL) {
            //Validating the incoming message
            if (messageExtractor.validateRollNo(m.rollNo)) {
                val ip = m.ip!!
                if (messageExtractor.isIPAddressUnique(ip)) {
                    //Updating the dataset after validation
                    val index = messageExtractor.updateRollNoDetails(m.rollNo, ip, m.isPresent)
                    assert(index != -1)
                    adapter.notifyItemChanged(index)
                    output.messageCodes = MessageCodes.NORMAL
                    output.isPresent = messageExtractor.getStatus(m.rollNo)
                } else {
                    output.messageCodes = MessageCodes.REDUNDANT
                }
            } else {
                output.messageCodes = MessageCodes.INVALID
            }
        }
        return output
    }
}