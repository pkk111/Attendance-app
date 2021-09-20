package com.pkk.android.attendance.misc

import android.content.Context
import androidx.core.content.ContextCompat
import com.pkk.android.attendance.R
import com.pkk.android.attendance.misc.Utils.Companion.showShortToast
import com.pkk.android.attendance.models.StudentModel
import java.util.*

class MessageExtractor(start: Int, end: Int) {

    fun isIPAddressUnique(ip: String): Boolean {
        if (Companion.students.isNotEmpty()) for (x in Companion.students.indices) {
            if (Companion.students[x].ipAddress.isNotEmpty() && Companion.students[x].ipAddress == ip) {
                return false
            }
        }
        return true
    }

    fun validateRollNo(rollNo: Int): Boolean {
        for (x in Companion.students.indices)
            if (Companion.students[x].rollNo == rollNo)
                return true
        return false
    }

    fun updateRollNoDetails(rollNo: Int, ip: String?, present: Boolean): Int {
        for (x in Companion.students.indices) {
            val student = Companion.students[x]
            if (student.rollNo == rollNo) {
                Companion.students[x] = StudentModel(rollNo, present, ip)
                return x
            }
        }
        return -1
    }

    fun addStud(context: Context, rollNo: Int) {
        for (student in Companion.students) {
            if (student.rollNo == rollNo) {
                showShortToast(context, "$rollNo already present for evaluation.")
                return
            }
        }
        Companion.students.add(StudentModel(rollNo))
    }

    fun getStatus(roll_no: Int): Boolean {
        for (student in Companion.students) if (student.rollNo == roll_no) return student.isPresent
        return false
    }

    val students: List<StudentModel>
        get() = Companion.students

    companion object {

        private var students: MutableList<StudentModel> = ArrayList()

        fun getColour(context: Context, bool: Boolean): Int {
            return if (bool) ContextCompat.getColor(
                context,
                R.color.present_color
            ) else ContextCompat.getColor(context, R.color.absent_color)
        }

        fun getAttendance(bool: Boolean): String {
            return if (bool) "Present" else "Absent"
        }

        fun setStatus(index: Int, status: Boolean) {
            val oldStudent = students[index]
            students[index] = StudentModel(oldStudent.rollNo, status, null)
        }
    }

    init {
        Companion.students.clear()
        for (x in start..end) {
            Companion.students.add(StudentModel(x))
        }
    }
}