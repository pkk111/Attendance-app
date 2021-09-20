package com.pkk.android.attendance.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pkk.android.attendance.R
import com.pkk.android.attendance.databinding.FragmentShowAttendanceListBinding
import com.pkk.android.attendance.misc.Utils
import com.pkk.android.attendance.models.StudentModel

class ShowAttendanceRecyclerViewAdapter(
    private val context: Context,
    private val values: List<StudentModel>
) : RecyclerView.Adapter<ShowAttendanceRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            FragmentShowAttendanceListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.rollNo.text = item.rollNo.toString()
        if (item.isPresent) {
            holder.cardBackground.setBackgroundColor(
                Utils.getColorFromResource(
                    context,
                    R.color.present_color
                )
            )
            holder.status.text = context.resources.getString(R.string.present)
        } else {
            holder.cardBackground.setBackgroundColor(
                Utils.getColorFromResource(
                    context,
                    R.color.absent_color
                )
            )
            holder.status.text = context.resources.getString(R.string.absent)
        }
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentShowAttendanceListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val rollNo = binding.rollNo
        val status = binding.showAttendanceStatus
        val cardBackground = binding.showAttendanceItemBackground
    }

}