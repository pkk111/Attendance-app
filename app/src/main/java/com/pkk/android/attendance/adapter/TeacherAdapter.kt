package com.pkk.android.attendance.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.pkk.android.attendance.databinding.AttendanceStatusBinding
import com.pkk.android.attendance.interfaces.ChangeAttendanceStatusListener
import com.pkk.android.attendance.models.StudentModel

class TeacherAdapter(
    private var listener: ChangeAttendanceStatusListener
) : ListAdapter<StudentModel, TeacherAdapter.ViewHolder>(AttendanceListDiffCallback()) {


    override fun onCreateViewHolder(parent: ViewGroup, i: Int): ViewHolder {
        return ViewHolder.from(parent, listener)
    }

    override fun onBindViewHolder(ViewHolder: ViewHolder, position: Int) {
        val item = getItem(position)
        ViewHolder.bind(item, position)
    }

    class ViewHolder private constructor(
        val binding: AttendanceStatusBinding,
        private val listener: ChangeAttendanceStatusListener
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            item: StudentModel,
            position: Int
        ) {
            binding.attendance = item

            binding.statusSwitch.setOnCheckedChangeListener(null)
            binding.statusSwitch.isChecked = item.isPresent
            binding.statusSwitch.setOnCheckedChangeListener { _, _ ->
                listener.changeStatusOf(position)
//                background.setCardBackgroundColor(MessageExtractor.getColour(view.context, b))
//                status.text = MessageExtractor.getAttendance(b)
            }
        }

        companion object {
            fun from(viewGroup: ViewGroup, listener: ChangeAttendanceStatusListener): ViewHolder {
                val layoutInflater = LayoutInflater.from(viewGroup.context)
                val binding = AttendanceStatusBinding.inflate(layoutInflater, viewGroup, false)
                return ViewHolder(binding, listener)
            }
        }
    }

    class AttendanceListDiffCallback : DiffUtil.ItemCallback<StudentModel>() {
        override fun areItemsTheSame(oldItem: StudentModel, newItem: StudentModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: StudentModel, newItem: StudentModel): Boolean {
            return oldItem == newItem
        }
    }


}
