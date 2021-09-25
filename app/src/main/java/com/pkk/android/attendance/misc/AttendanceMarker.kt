package com.pkk.android.attendance.misc

import android.util.Log
import com.google.gson.Gson
import com.pkk.android.attendance.models.MessageCodes
import com.pkk.android.attendance.models.MessageModel

class AttendanceMarker(
    private val messageExtractor: MessageExtractor
) {

    private val gson: Gson = Gson()
    private var messageModel: MessageModel? = null

    fun markAttendance(inputJsonMessage: String): Pair<Int, String> {
        try {
            messageModel = gson.fromJson(inputJsonMessage, MessageModel::class.java)
        } catch (e: Exception) {
            Log.e("Error converting", "message is $inputJsonMessage\n\tException is $e")
        }
        return processMessage(messageModel!!)
    }

    private fun processMessage(m: MessageModel): Pair<Int, String> {
        var outputString = ""
        var outputCode = -1
        val output = MessageModel()
        if (m.messageCodes == MessageCodes.CUSTOM) {
            outputString = m.message
        } else if (m.messageCodes == MessageCodes.NORMAL) {
            //Validating the incoming message
            if (messageExtractor.validateRollNo(m.rollNo)) {
                val ip = m.ip!!
                if (messageExtractor.isIPAddressUnique(ip)) {
                    //Updating the dataset after validation
                    val index = messageExtractor.updateRollNoDetails(m.rollNo, ip, m.isPresent)
                    outputCode = index
                    output.messageCodes = MessageCodes.NORMAL
                    output.isPresent = messageExtractor.getStatus(m.rollNo)
                } else {
                    output.messageCodes = MessageCodes.REDUNDANT
                }
            } else {
                output.messageCodes = MessageCodes.INVALID
            }
            outputString = gson.toJson(output)
        }
        return Pair(outputCode, outputString)
    }
}