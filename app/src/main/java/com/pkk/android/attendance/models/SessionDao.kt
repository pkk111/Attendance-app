package com.pkk.android.attendance.models

import androidx.room.*

@Dao
interface SessionDao {

    @Insert
    suspend fun insert(sessionModel: SessionModel): Long

    @Delete
    suspend fun delete(sessionModel: SessionModel)

    @Update
    suspend fun update(sessionModel: SessionModel)

    @Query("SELECT * FROM sessions_table WHERE classes = :id")
    suspend fun getAllByMeetingId(id: Long): List<SessionModel>
}