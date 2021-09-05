package com.pkk.android.attendance.models

import android.os.Build
import androidx.annotation.RequiresApi
import java.util.*

class DeviceLocation(var x: Float, var y: Float) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as DeviceLocation
        return java.lang.Float.compare(that.x, x) == 0 &&
                java.lang.Float.compare(that.y, y) == 0
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    override fun hashCode(): Int {
        return Objects.hash(x, y)
    }
}