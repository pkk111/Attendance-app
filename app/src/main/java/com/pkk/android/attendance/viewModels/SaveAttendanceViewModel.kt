package com.pkk.android.attendance.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pkk.android.attendance.misc.Utils
import com.pkk.android.attendance.models.ClassDao
import com.pkk.android.attendance.models.MeetingModel
import kotlinx.coroutines.launch

class SaveAttendanceViewModel(private val database: ClassDao) : ViewModel() {

    private lateinit var classes: List<MeetingModel>
    private var _classNames = MutableLiveData<List<String>>()
    val className: LiveData<List<String>> get() = _classNames
    private var _saveIndex = MutableLiveData<Long>()
    val saveIndex: LiveData<Long> get() = _saveIndex

    init {
        _saveIndex.value = -1L
        viewModelScope.launch {
            classes = loadClasses()
            _classNames.value = getClassNames(classes)
        }
    }

    private suspend fun loadClasses(): List<MeetingModel> {
        val list = MutableList(0) { MeetingModel() }
        list.addAll(database.getAll())
        return list
    }

    private fun getClassNames(classes: List<MeetingModel>): List<String> {
        val list = MutableList(1) { "Create New Class" }
        for (i in classes)
            list.add(i.title)
        return list
    }

    fun setMeetIdToSaveAttendance(index: Long, name: String) {
        if (index == 0L) {
            viewModelScope.launch {
                insert(name)
            }
        }
        _saveIndex.value = index - 1
    }

    suspend fun insert(name: String) {
        _saveIndex.value = database.insert(
            MeetingModel.getInstance(
                name,
                null,
                Utils.getRandomBackground()
            )
        )
    }
}