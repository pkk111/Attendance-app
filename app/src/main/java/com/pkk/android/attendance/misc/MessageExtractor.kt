package com.pkk.android.attendance.misc

import android.content.Context
import androidx.core.content.ContextCompat
import com.pkk.android.attendance.R
import com.pkk.android.attendance.models.DeviceModel
import com.pkk.android.attendance.models.StudentModel
import java.util.*

class MessageExtractor(start: Int, end: Int) {

    private var _students: MutableList<StudentModel> = ArrayList()
    val students: List<StudentModel>
        get() = _students

    fun isIPAddressUnique(ip: String): Boolean {
        if (_students.isNotEmpty()) for (x in _students.indices) {
            if (!_students[x].ipAddress.isNullOrEmpty() && _students[x].ipAddress == ip) {
                return false
            }
        }
        return true
    }

    fun validateRollNo(rollNo: Int): Boolean {
        for (x in _students.indices)
            if (_students[x].rollNo == rollNo)
                return true
        return false
    }

    fun updateRollNoDetails(rollNo: Int, ip: String?, present: Boolean, device: DeviceModel): Int {
        for (x in _students.indices) {
            val student = _students[x]
            if (student.rollNo == rollNo) {
                _students[x] = StudentModel.getInstance(
                    rollNo,
                    present,
                    ip,
                    device.deviceName,
                    device.deviceInfo,
                    0
                )
                return x
            }
        }
        return -1
    }

    fun addStud(rollNo: Int): Boolean {
        for (student in _students) {
            if (student.rollNo == rollNo) {
                return false
            }
        }
        _students.add(StudentModel.getInstance(rollNo))
        return true
    }

    fun getStatus(roll_no: Int): Boolean {
        for (student in students) if (student.rollNo == roll_no) return student.isPresent
        return false
    }

    fun setStatus(index: Int, status: Boolean) {
        val oldStudent = _students[index]
        _students[index] = StudentModel(oldStudent.id, oldStudent.rollNo, status, null)
    }

    companion object {

        fun getColour(context: Context, bool: Boolean): Int {
            return if (bool) ContextCompat.getColor(
                context,
                R.color.present_color
            ) else ContextCompat.getColor(context, R.color.absent_color)
        }

        fun getAttendance(bool: Boolean): String {
            return if (bool) "Present" else "Absent"
        }
    }

    fun clear() {
        _students.clear()
    }

    fun setSessionId(sessionId: Long) {
        for (element in _students) {
            element.sessionId = sessionId
        }
    }

    init {
        _students.clear()
        for (x in start..end) {
            _students.add(StudentModel.getInstance(x))
        }
    }
}