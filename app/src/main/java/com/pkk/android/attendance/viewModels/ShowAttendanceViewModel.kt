package com.pkk.android.attendance.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pkk.android.attendance.models.AttendanceDao
import com.pkk.android.attendance.models.StudentModel
import kotlinx.coroutines.launch

class ShowAttendanceViewModel(private val database: AttendanceDao) : ViewModel() {
    private val _attendance = MutableLiveData<List<StudentModel>>()
    val attendance: LiveData<List<StudentModel>> get() = _attendance

    init {
        _attendance.value = List(0) { StudentModel() }
    }

    fun loadData(sessionId: Long) {
        viewModelScope.launch { loadAttendance(sessionId) }
    }

    private suspend fun loadAttendance(sessionId: Long) {
        val list = database.getAllWithSessionId(sessionId)
        _attendance.value = list
    }
}