package com.pkk.android.attendance.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pkk.android.attendance.databinding.FragmentMeetingsListItemBinding
import com.pkk.android.attendance.misc.CentralVariables
import com.pkk.android.attendance.misc.Utils
import com.pkk.android.attendance.models.MeetingModel

class MeetingRecyclerViewAdapter(
    private val values: List<MeetingModel>, private val listener: View.OnClickListener
) : RecyclerView.Adapter<MeetingRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent, listener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.bind(item)
    }


    override fun getItemCount(): Int = values.size

    class ViewHolder private constructor(
        val binding: FragmentMeetingsListItemBinding,
        private val listener: View.OnClickListener
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            item: MeetingModel
        ) {
            binding.meetingData = item
            binding.meetingCardBackground.setBackgroundResource(Utils.getBackgrounds()[item.background])
            binding.meetingCardBackground.setTag(CentralVariables.KEY_SESSION_ID, item.id)
            binding.meetingCardBackground.setOnClickListener(listener)
        }

        companion object {
            fun from(parent: ViewGroup, listener: View.OnClickListener) =
                ViewHolder(
                    FragmentMeetingsListItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ), listener
                )
        }
    }
}