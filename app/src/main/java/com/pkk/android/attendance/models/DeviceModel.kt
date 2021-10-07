package com.pkk.android.attendance.models

data class DeviceModel(
    var endpointID: String = "",
    var deviceName: String? = "",
    var deviceInfo: ByteArray? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DeviceModel

        if (endpointID != other.endpointID) return false
        if (deviceName != other.deviceName) return false
        if (deviceInfo != null) {
            if (other.deviceInfo == null) return false
            if (!deviceInfo.contentEquals(other.deviceInfo)) return false
        } else if (other.deviceInfo != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = endpointID.hashCode()
        result = 31 * result + (deviceName?.hashCode() ?: 0)
        result = 31 * result + (deviceInfo?.contentHashCode() ?: 0)
        return result
    }
}
