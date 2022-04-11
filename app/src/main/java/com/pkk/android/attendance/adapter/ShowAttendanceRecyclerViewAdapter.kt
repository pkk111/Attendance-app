package com.pkk.android.attendance.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pkk.android.attendance.databinding.FragmentShowAttendanceListBinding
import com.pkk.android.attendance.models.StudentModel

class ShowAttendanceRecyclerViewAdapter(
    private val values: List<StudentModel>
) : RecyclerView.Adapter<ShowAttendanceRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = values.size

    class ViewHolder private constructor(val binding: FragmentShowAttendanceListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            item: StudentModel
        ) {
            binding.attendanceStatus = item
        }

        companion object {
            fun from(
                parent: ViewGroup
            ): ViewHolder {
                return ViewHolder(
                    FragmentShowAttendanceListBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }
}