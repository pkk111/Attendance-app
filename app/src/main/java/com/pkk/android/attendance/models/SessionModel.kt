package com.pkk.android.attendance.models

import java.util.*

data class SessionModel(
    val id: Int,
    val startTime: Date,
    val endTime: Date,
    val noOfPresets: Int,
    val noOfAbsent: Int,
    val res: Int
)