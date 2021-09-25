package com.pkk.android.attendance.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class StudentViewModel: ViewModel() {

    private var _serverClientMessage = MutableLiveData<String>()
    val serverClientMessage: LiveData<String> get() = _serverClientMessage
    private var _isPresent = MutableLiveData<Boolean>()
    val isPresent: LiveData<Boolean> get() = _isPresent

    init {
        _serverClientMessage.value = ""
    }

    fun setAttendanceStatus(isPresent: Boolean){
        _isPresent.value = isPresent
    }

    fun addClientServerMessage(message: String){
        _serverClientMessage.value += message
    }

    fun clearClientServerConversation(){
        _serverClientMessage.value = ""
    }
}