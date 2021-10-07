package com.pkk.android.attendance.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.pkk.android.attendance.misc.Utils

@Entity(tableName = "class_table")
data class MeetingModel(
    @PrimaryKey
    val id: Long = 0,

    @ColumnInfo(name = "title")
    var title: String = "",

    @ColumnInfo(name = "details")
    var details: String = "",

    @ColumnInfo(name = "background")
    var background: Int = Utils.getBackgrounds()[0]
) {
    companion object {
        fun getInstance(title: String, details: String?, background: Int): MeetingModel {
            val meetingModel = MeetingModel()
            meetingModel.title = title
            if (!details.isNullOrBlank()) meetingModel.details = details
            meetingModel.background = background
            return meetingModel
        }
    }
}
