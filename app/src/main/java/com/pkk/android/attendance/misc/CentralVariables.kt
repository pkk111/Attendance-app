package com.pkk.android.attendance.misc

import com.google.android.gms.nearby.connection.Strategy

object CentralVariables {
    const val SERVICE_ID = "com.pkk.android.attendance"

    @JvmField
    val STRATEGY = Strategy.P2P_STAR
    const val student = 1
    const val teacher = 2
    const val KEY_DISCOVERER_NAME = "discoverer_name"
    const val KEY_HOST_NAME = "host_name"
    const val KEY_PROFILE_PIC = "profile_pic"
    const val KEY_FRAGMENT_MESSAGE_KEY = "message transfer key"
}