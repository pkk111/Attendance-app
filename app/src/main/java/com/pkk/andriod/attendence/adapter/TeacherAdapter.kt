package com.pkk.andriod.attendence.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Switch
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.pkk.andriod.attendence.R
import com.pkk.andriod.attendence.adapter.TeacherAdapter.MyViewHolder
import com.pkk.andriod.attendence.misc.MessageExtractor

class TeacherAdapter(context: Context?) : RecyclerView.Adapter<MyViewHolder>() {

    private var roll: List<Int>? = null
    private var attendance: List<Boolean>? = null
    private var layoutInflater = LayoutInflater.from(context)

    fun setAttendance(roll: List<Int>?, attendance: List<Boolean>?) {
        this.roll = roll
        this.attendance = attendance
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): MyViewHolder {
        val view = layoutInflater.inflate(R.layout.attendencestatus, viewGroup, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(myViewHolder: MyViewHolder, i: Int) {
        myViewHolder.background.setCardBackgroundColor(MessageExtractor.getcolor(attendance!![i]))
        myViewHolder.rollno.text = roll!![i].toString()
        myViewHolder.status.text = MessageExtractor.getAttendance(attendance!![i])
        myViewHolder.attendanceSwitch.isChecked = attendance!![i]
        myViewHolder.attendanceSwitch.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { _, b ->
            MessageExtractor.setStatus(i, b)
            myViewHolder.background.setCardBackgroundColor(MessageExtractor.getcolor(b))
            myViewHolder.status.text = MessageExtractor.getAttendance(b)
        })
    }

    override fun getItemCount(): Int {
        return roll!!.size
    }

    class MyViewHolder(v: View) : RecyclerView.ViewHolder(v) {

        var rollno: TextView = v.findViewById(R.id.rollno)
        var background: CardView = v.findViewById(R.id.notification_background)
        var status: TextView = v.findViewById(R.id.status)
        var attendanceSwitch: Switch = v.findViewById(R.id.status_switch)

    }

}