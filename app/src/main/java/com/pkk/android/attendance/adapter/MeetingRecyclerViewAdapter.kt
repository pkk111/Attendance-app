package com.pkk.android.attendance.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pkk.android.attendance.databinding.FragmentMeetingsListItemBinding
import com.pkk.android.attendance.misc.CentralVariables
import com.pkk.android.attendance.misc.Utils
import com.pkk.android.attendance.models.MeetingModel

class MeetingRecyclerViewAdapter(
    private val values: List<MeetingModel>, private val listener: View.OnClickListener
) : RecyclerView.Adapter<MeetingRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            FragmentMeetingsListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.title.text = item.title
        holder.details.text = item.details
        holder.card.setBackgroundResource(Utils.getBackgrounds()[item.background])
        holder.card.setTag(CentralVariables.KEY_SESSION_ID, item.id)
        holder.card.setOnClickListener(listener)
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentMeetingsListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val card: LinearLayout = binding.meetingCardBackground
        val title: TextView = binding.meetingTitle
        val details: TextView = binding.meetingDetails
    }

}