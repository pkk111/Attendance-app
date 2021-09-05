package com.pkk.android.attendance.models

enum class MessageCodes(val message: String) {
    CUSTOM("Client Side message"),
    NORMAL(""),
    REDUNDANT("You cannot mark more than one attendance in this session"),
    INVALID("Enter a valid roll number")
}