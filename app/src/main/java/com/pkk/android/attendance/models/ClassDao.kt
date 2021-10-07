package com.pkk.android.attendance.models

import androidx.room.*

@Dao
interface ClassDao {
    @Insert
    suspend fun insert(meetingModel: MeetingModel): Long

    @Update
    suspend fun update(meetingModel: MeetingModel)

    @Delete
    suspend fun delete(meetingModel: MeetingModel)

    @Query("SELECT * FROM class_table")
    suspend fun getAll(): List<MeetingModel>

    @Query("SELECT title FROM class_table")
    suspend fun getAllTitle(): List<String>

    @Query("SELECT * FROM class_table WHERE id = :id")
    suspend fun getById(id: Long): MeetingModel
}