package com.pkk.android.attendance.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.IllegalArgumentException

class ViewModelFactory(vararg params: Any) : ViewModelProvider.NewInstanceFactory() {
    private val param: Array<out Any> = params

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TeacherViewModel::class.java))
            return TeacherViewModel() as T
        if (modelClass.isAssignableFrom(PulseLayoutViewModel::class.java))
            return PulseLayoutViewModel() as T
        if (modelClass.isAssignableFrom(StudentViewModel::class.java))
            return StudentViewModel() as T
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}