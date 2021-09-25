package com.pkk.android.attendance.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pkk.android.attendance.misc.AttendanceMarker
import com.pkk.android.attendance.misc.MessageExtractor

class TeacherViewModel : ViewModel() {

    private lateinit var _extractor: MessageExtractor
    val extractor get() = _extractor
    private val _isRunning = MutableLiveData<Boolean>()
    val isRunning: LiveData<Boolean> get() = _isRunning
    private var _start: Int = 0
    val start get() = _start
    private var _end: Int = 0
    val end get() = _end

    init {
        _isRunning.value = false
    }

    fun startRunning(start: Int, end: Int) {
        this._start = start
        this._end = end
        _extractor = MessageExtractor(start, end)
        _isRunning.value = true
    }

    fun stopRunning() {
        _isRunning.value = false
    }

    fun addStudent(rollNo: Int): Boolean {
        return _extractor.addStud(rollNo)
    }

    fun getSize(): Int {
        return _extractor.students.size
    }

    fun markAttendance(message: String): Pair<Int, String> {
        val marker = AttendanceMarker(_extractor)
        return marker.markAttendance(message)
    }

    fun changeStatus(index: Int) {
        if (index < getSize()) {
            _extractor.setStatus(index, !_extractor.students[index].isPresent)
        }
    }
}