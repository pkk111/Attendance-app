package com.pkk.android.attendance.models

import android.os.Build
import androidx.annotation.RequiresApi
import java.util.*

class DetectedLocation(private var x: Float, private var y: Float) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as DetectedLocation
        return that.x.compareTo(x) == 0 &&
                that.y.compareTo(y) == 0
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    override fun hashCode(): Int {
        return Objects.hash(x, y)
    }
}