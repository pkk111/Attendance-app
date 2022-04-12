package com.pkk.android.attendance.adapter

import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.databinding.BindingAdapter
import com.pkk.android.attendance.R
import com.pkk.android.attendance.misc.Utils
import com.pkk.android.attendance.models.StudentModel

@BindingAdapter("backgroundColor")
fun CardView.setBackground(presentStatus: Boolean) {
    setCardBackgroundColor(
        Utils.getColorFromResource(
            context,
            if (presentStatus) R.color.present_color else R.color.absent_color,
            null
        )
    )
}

@BindingAdapter("setText")
fun TextView.isPresent(presentStatus: Boolean) {
    val id = if (presentStatus) R.string.present else R.string.absent
    text = context.resources.getString(id)
}