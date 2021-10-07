package com.pkk.android.attendance.misc

import com.google.android.gms.nearby.connection.Strategy

object CentralVariables {

    const val SERVICE_ID = "com.pkk.android.attendance"
    val STAR_STRATEGY = Strategy.P2P_STAR
    val P2P_STRATEGY = Strategy.P2P_POINT_TO_POINT

    const val KEY_USERNAME = "username"
    const val KEY_PROFILE_PIC = "profile_pic"
    const val KEY_PULSE_FRAGMENT_MESSAGE_KEY = "pulse fragment"
    const val KEY_SAVE_ATTENDANCE_DIALOG_FRAGMENT_MESSAGE = "save attendance"
    const val KEY_START_ATTENDANCE_DIALOG_FRAGMENT_MESSAGE = "get attendance details"
    const val KEY_ENTER_ROLL_NUMBER_DIALOG_FRAGMENT_MESSAGE = "get roll number"
    const val KEY_MESSAGE = "message"
    const val KEY_POSITION: Int = -1
    const val KEY_ID: String = "id"
    const val KEY_SESSION_ID: Int = -1
    const val KEY_MEETING_ID: String = "meeting_id"
    const val KEY_CANCELLED: String = "cancelled"
    const val KEY_START: String = "start"
    const val KEY_END: String = "end"
    const val Key_ROLL_NO: String = "Roll Number"
    const val KEY_DEVICE: String = "device"
}