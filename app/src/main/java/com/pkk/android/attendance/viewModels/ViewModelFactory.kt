package com.pkk.android.attendance.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.pkk.android.attendance.database.AttendanceDao
import com.pkk.android.attendance.database.ClassDao
import com.pkk.android.attendance.database.SessionDao

class ViewModelFactory(vararg params: Any) : ViewModelProvider.NewInstanceFactory() {
    private val param: Array<out Any> = params

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TeacherViewModel::class.java) && param.size == 2)
            return TeacherViewModel(param[0] as SessionDao, param[1] as AttendanceDao) as T
        if (modelClass.isAssignableFrom(PulseLayoutViewModel::class.java))
            return PulseLayoutViewModel() as T
        if (modelClass.isAssignableFrom(StudentViewModel::class.java))
            return StudentViewModel() as T
        if (modelClass.isAssignableFrom(SaveAttendanceViewModel::class.java) && param.isNotEmpty())
            return SaveAttendanceViewModel(param[0] as ClassDao) as T
        if (modelClass.isAssignableFrom(MeetingsViewModel::class.java) && param.isNotEmpty())
            return MeetingsViewModel(param[0] as ClassDao) as T
        if (modelClass.isAssignableFrom(SessionsViewModel::class.java) && param.isNotEmpty())
            return SessionsViewModel(param[0] as SessionDao) as T
        if (modelClass.isAssignableFrom(ShowAttendanceViewModel::class.java) && param.isNotEmpty())
            return ShowAttendanceViewModel(param[0] as AttendanceDao) as T
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}