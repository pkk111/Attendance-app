package com.pkk.android.attendance.models

import android.os.Build
import androidx.annotation.RequiresApi
import java.util.*

class DetectedDevice(var x: Float, var y: Float) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as DetectedDevice
        return that.x.compareTo(x) == 0 &&
                that.y.compareTo(y) == 0
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    override fun hashCode(): Int {
        return Objects.hash(x, y)
    }
}