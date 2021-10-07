package com.pkk.android.attendance.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pkk.android.attendance.models.SessionDao
import com.pkk.android.attendance.models.SessionModel
import kotlinx.coroutines.launch

class SessionsViewModel(private val database: SessionDao) : ViewModel() {
    private val _sessions = MutableLiveData<List<SessionModel>>()
    val sessions: LiveData<List<SessionModel>> get() = _sessions

    init {
        _sessions.value = List(0) { SessionModel() }
    }

    fun loadData(meetingId: Long) {
        viewModelScope.launch { loadSessions(meetingId) }
    }

    private suspend fun loadSessions(meetingId: Long) {
        _sessions.value = database.getAllByMeetingId(meetingId)
    }
}