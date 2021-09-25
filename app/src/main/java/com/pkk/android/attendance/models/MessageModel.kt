package com.pkk.android.attendance.models

class MessageModel {
    var ip: String? = null
    var rollNo = 0
    var isPresent = false
    var messageCodes = MessageCodes.NORMAL
    var message: String = ""

    constructor()

    constructor(messageCodes: MessageCodes, message: String) {
        this.messageCodes = messageCodes
        this.message = message
    }
}