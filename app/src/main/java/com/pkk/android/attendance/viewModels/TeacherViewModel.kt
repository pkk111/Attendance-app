package com.pkk.android.attendance.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pkk.android.attendance.misc.AttendanceMarker
import com.pkk.android.attendance.misc.MessageExtractor
import com.pkk.android.attendance.misc.Utils
import com.pkk.android.attendance.models.AttendanceDao
import com.pkk.android.attendance.models.DeviceModel
import com.pkk.android.attendance.models.SessionDao
import com.pkk.android.attendance.models.SessionModel
import kotlinx.coroutines.launch
import java.util.*

class TeacherViewModel(
    private val sessionDao: SessionDao,
    private val attendanceDao: AttendanceDao
) :
    ViewModel() {

    private lateinit var _extractor: MessageExtractor
    val extractor get() = _extractor
    private val _isRunning = MutableLiveData<Boolean>()
    val isRunning: LiveData<Boolean> get() = _isRunning
    private var _start: Int = 0
    val start get() = _start
    private var _end: Int = 0
    val end get() = _end
    private var present = 0
    private var startTime: Date

    init {
        _isRunning.value = false
        startTime = Calendar.getInstance().time
    }

    fun startRunning(start: Int, end: Int) {
        this._start = start
        this._end = end
        _extractor = MessageExtractor(start, end)
        startTime = Calendar.getInstance().time
        _isRunning.value = true
    }

    fun stopRunningWithoutSaving() {
        _isRunning.value = false
    }

    fun addStudent(rollNo: Int): Boolean {
        return _extractor.addStud(rollNo)
    }

    fun getSize(): Int {
        return _extractor.students.size
    }

    fun markAttendance(message: String, device: DeviceModel): Pair<Int, String> {
        val marker = AttendanceMarker(_extractor)
        val result = marker.markAttendance(message, device)
        if (result.first != -1) present++
        return result
    }

    fun changeStatus(index: Int) {
        if (index < getSize()) {
            val isPresent = extractor.students[index].isPresent
            if (isPresent) present-- else present++
            _extractor.setStatus(index, !isPresent)
        }
    }

    fun saveAttendanceAt(meetingId: Long) {
        val session = SessionModel.getInstance(
            startTime,
            Calendar.getInstance().time,
            present,
            extractor.students.size - present,
            Utils.getRandomBackground(),
            meetingId
        )
        viewModelScope.launch { saveAttendanceAndStopRunning(session) }
    }

    private suspend fun saveAttendanceAndStopRunning(sessionModel: SessionModel) {
        val sessionId = sessionDao.insert(sessionModel)
        extractor.setSessionId(sessionId)
        attendanceDao.insert(extractor.students)
        stopRunning()
    }

    fun stopRunning() {
        extractor.clear()
        _isRunning.value = false
    }
}