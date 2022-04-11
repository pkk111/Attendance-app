package com.pkk.android.attendance.viewModels

import androidx.lifecycle.*
import com.pkk.android.attendance.database.SessionDao
import com.pkk.android.attendance.models.SessionModel
import kotlinx.coroutines.launch

class SessionsViewModel(private val database: SessionDao) : ViewModel() {
    private val _sessions = MutableLiveData<MutableList<SessionModel>>()
    val sessions: LiveData<List<SessionModel>> get() = Transformations.map(_sessions) { i -> i as List<SessionModel> }
    private var _positionRemoved: Int
    val positionRemoved: Int get() = _positionRemoved
    private val _shareMessage = MutableLiveData<String>()
    val shareMessage: LiveData<String> get() = _shareMessage

    init {
        _positionRemoved = -1
        _sessions.value = MutableList(0) { SessionModel() }
    }

    fun loadData(meetingId: Long) {
        viewModelScope.launch { loadSessions(meetingId) }
    }

    private suspend fun loadSessions(meetingId: Long) {
        _sessions.value = database.getAllByMeetingId(meetingId)
    }

    fun deleteItemAt(position: Int) {
        viewModelScope.launch { deleteAt(position) }
    }

    private suspend fun deleteAt(position: Int) {
        if (sessions.value != null) {
            database.delete(sessions.value!![position])
            _positionRemoved = position
            _sessions.value!!.removeAt(position)

        }
    }
}