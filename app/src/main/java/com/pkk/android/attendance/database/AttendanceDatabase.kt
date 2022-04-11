package com.pkk.android.attendance.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.pkk.android.attendance.misc.Converters
import com.pkk.android.attendance.models.MeetingModel
import com.pkk.android.attendance.models.SessionModel
import com.pkk.android.attendance.models.StudentModel

@Database(
    entities = [StudentModel::class, MeetingModel::class, SessionModel::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AttendanceDatabase : RoomDatabase() {

    abstract fun classDao(): ClassDao
    abstract fun sessionDao(): SessionDao
    abstract fun attendanceDao(): AttendanceDao

    companion object {
        @Volatile
        private var INSTANCE: AttendanceDatabase? = null

        fun getDatabase(context: Context): AttendanceDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null)
                return tempInstance
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context,
                    AttendanceDatabase::class.java,
                    "attendance_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }

}