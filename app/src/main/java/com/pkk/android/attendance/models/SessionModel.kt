package com.pkk.android.attendance.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.pkk.android.attendance.misc.Utils
import java.util.*

@Entity(
    tableName = "sessions_table",
    foreignKeys = [ForeignKey(
        entity = MeetingModel::class,
        parentColumns = ["id"],
        childColumns = ["classes"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class SessionModel(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo
    var startTime: Date = Calendar.getInstance().time as Date,

    @ColumnInfo
    var endTime: Date = Calendar.getInstance().time as Date,

    @ColumnInfo
    var noOfPresets: Int = 0,

    @ColumnInfo
    var noOfAbsent: Int = 0,

    @ColumnInfo
    var res: Int = Utils.getBackgrounds()[0],

    @ColumnInfo(name = "classes", index = true)
    var meetingId: Long = 0
) {

    companion object {
        fun getInstance(
            start: Date,
            end: Date?,
            presents: Int,
            absents: Int,
            background: Int,
            meetingId: Long
        ): SessionModel {
            val sessionModel = SessionModel()
            sessionModel.startTime = start
            if (end != null) sessionModel.endTime = end
            sessionModel.noOfPresets = presents
            sessionModel.noOfAbsent = absents
            sessionModel.res = background
            sessionModel.meetingId = meetingId
            return sessionModel

        }
    }

}