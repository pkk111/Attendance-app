package com.pkk.android.attendance.adapter

import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.databinding.BindingAdapter
import com.pkk.android.attendance.R
import com.pkk.android.attendance.misc.Utils
import com.pkk.android.attendance.models.StudentModel

@BindingAdapter("setbackgroundColor")
fun CardView.setBackground(studentModel: StudentModel) {
    setBackgroundColor(
        Utils.getColorFromResource(
            context,
            if (studentModel.isPresent) R.color.present_color else R.color.absent_color,
            null
        )
    )
}

@BindingAdapter("setText")
fun TextView.isPresent(studentModel: StudentModel) {
    val id = if (studentModel.isPresent) R.string.present else R.string.absent
    text = context.resources.getString(id)
}