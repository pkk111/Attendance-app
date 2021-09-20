package com.pkk.android.attendance.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.switchmaterial.SwitchMaterial
import com.pkk.android.attendance.R
import com.pkk.android.attendance.adapter.TeacherAdapter.MyViewHolder
import com.pkk.android.attendance.misc.MessageExtractor
import com.pkk.android.attendance.models.StudentModel

class TeacherAdapter(private var context: Context) : RecyclerView.Adapter<MyViewHolder>() {

    private var students: List<StudentModel>? = null
    private var layoutInflater = LayoutInflater.from(context)

    fun setAttendance(students: List<StudentModel>) {
        this.students = students
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): MyViewHolder {
        val view = layoutInflater.inflate(R.layout.attendance_status, viewGroup, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(myViewHolder: MyViewHolder, i: Int) {
        myViewHolder.background.setCardBackgroundColor(
            MessageExtractor.getColour(
                context,
                students!![i].isPresent
            )
        )
        myViewHolder.rollno.text = students!![i].rollNo.toString()
        myViewHolder.status.text = MessageExtractor.getAttendance(students!![i].isPresent)
        myViewHolder.attendanceSwitch.isChecked = students!![i].isPresent
        myViewHolder.attendanceSwitch.setOnCheckedChangeListener { _, b ->
            MessageExtractor.setStatus(i, b)
            myViewHolder.background.setCardBackgroundColor(MessageExtractor.getColour(context, b))
            myViewHolder.status.text = MessageExtractor.getAttendance(b)
        }
    }

    override fun getItemCount(): Int {
        return students!!.size
    }

    class MyViewHolder(v: View) : RecyclerView.ViewHolder(v) {

        var rollno: TextView = v.findViewById(R.id.rollno)
        var background: CardView = v.findViewById(R.id.notification_background)
        var status: TextView = v.findViewById(R.id.status)
        var attendanceSwitch: SwitchMaterial = v.findViewById(R.id.status_switch)

    }

}