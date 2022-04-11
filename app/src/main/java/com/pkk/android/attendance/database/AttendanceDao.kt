package com.pkk.android.attendance.database

import androidx.room.*
import com.pkk.android.attendance.models.StudentModel

@Dao
interface AttendanceDao {

    @Query("SELECT * FROM student_attendance WHERE session_id = :sessionId")
    suspend fun getAllWithSessionId(sessionId: Long): List<StudentModel>

    @Insert
    suspend fun insert(studentModel: StudentModel)

    @Insert
    suspend fun insert(students: List<StudentModel>)

    @Delete
    suspend fun delete(studentModel: StudentModel)

    @Update
    suspend fun update(studentModel: StudentModel)

    @Update
    suspend fun update(list: List<StudentModel>)

}