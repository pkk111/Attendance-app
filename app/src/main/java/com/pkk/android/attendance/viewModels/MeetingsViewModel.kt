package com.pkk.android.attendance.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pkk.android.attendance.models.ClassDao
import com.pkk.android.attendance.models.MeetingModel
import kotlinx.coroutines.launch

class MeetingsViewModel(private val database: ClassDao) : ViewModel() {

    private var _classes = MutableLiveData<List<MeetingModel>>()
    val classes: LiveData<List<MeetingModel>> get() = _classes


    init {
        _classes.value = List(0) { MeetingModel() }
    }

    fun loadData() {
        viewModelScope.launch { loadClasses() }
    }

    private suspend fun loadClasses() {
        _classes.value = database.getAll()
    }
}