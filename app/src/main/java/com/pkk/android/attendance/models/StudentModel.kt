package com.pkk.android.attendance.models

import androidx.room.*

//@AutoValue
@Entity(
    tableName = "student_attendance",
    indices = [Index(value = ["roll_no"], unique = true)],
    foreignKeys = [ForeignKey(
        entity = SessionModel::class,
        parentColumns = ["id"],
        childColumns = ["session_id"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class StudentModel(
//    @CopyAnnotations
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "roll_no")
    var rollNo: Int = 0,

    @ColumnInfo(name = "is_present")
    var isPresent: Boolean = false,

    @ColumnInfo(name = "ip_address")
    var ipAddress: String? = null,

    @ColumnInfo(name = "endpoint_name")
    var endpointId: String? = null,

    @ColumnInfo(name = "device_info")
    var deviceInfo: ByteArray? = null,

    @ColumnInfo(name = "session_id", index = true)
    var sessionId: Long = 0
) {

    companion object {
        fun getInstance(rollNo: Int): StudentModel {
            val studentModel = StudentModel()
            studentModel.rollNo = rollNo
            return studentModel
        }

        fun getInstance(
            rollNo: Int,
            isPresent: Boolean,
            ipAddress: String?,
            endpointId: String?,
            deviceInfo: ByteArray?,
            sessionId: Long
        ): StudentModel {
            val studentModel = StudentModel()
            studentModel.rollNo = rollNo
            studentModel.isPresent = isPresent
            studentModel.ipAddress = ipAddress
            studentModel.endpointId = endpointId
            studentModel.deviceInfo = deviceInfo
            studentModel.sessionId = sessionId
            return studentModel
        }
    }
}