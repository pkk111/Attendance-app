package com.pkk.android.attendance.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pkk.android.attendance.databinding.FragmentSessionListItemBinding
import com.pkk.android.attendance.misc.CentralVariables
import com.pkk.android.attendance.misc.Utils
import com.pkk.android.attendance.models.SessionModel
import java.util.*

class SessionRecyclerViewAdapter(
    private val values: List<SessionModel>, private val listener: View.OnClickListener,
    private val menulistener: View.OnClickListener?
) : RecyclerView.Adapter<SessionRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            FragmentSessionListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        val start = Calendar.getInstance()
        val end = Calendar.getInstance()
        start.time = item.startTime
        end.time = item.endTime
        //checking for same day
        if ((start.get(Calendar.YEAR) == end.get(Calendar.YEAR)) && (start.get(Calendar.DAY_OF_YEAR) == end.get(
                Calendar.DAY_OF_YEAR
            ))
        )
            holder.startDate.text = "${Utils.getNumberSuffix(start.get(Calendar.DAY_OF_MONTH))}, ${
                start.getDisplayName(
                    Calendar.MONTH,
                    Calendar.LONG,
                    Locale.getDefault()
                )
            }"
        else {
            holder.startDate.text = "${Utils.getNumberSuffix(start.get(Calendar.DAY_OF_MONTH))}, ${
                start.getDisplayName(
                    Calendar.MONTH,
                    Calendar.LONG,
                    Locale.getDefault()
                )
            }"
            holder.endDate.text = "${Utils.getNumberSuffix(end.get(Calendar.DAY_OF_MONTH))}, ${
                end.getDisplayName(
                    Calendar.MONTH,
                    Calendar.LONG,
                    Locale.getDefault()
                )
            }"
        }

        //setting time
        holder.startTime.text =
            "${start.get(Calendar.HOUR)}:${start.get(Calendar.MINUTE)}"
        holder.endTime.text =
            "${end.get(Calendar.HOUR)}:${end.get(Calendar.MINUTE)}"

        if (start.get(Calendar.AM_PM) == Calendar.AM)
            holder.startAmPm.text = "AM"
        else
            holder.startAmPm.text = "PM"

        if (end.get(Calendar.AM_PM) == Calendar.AM)
            holder.endAmPm.text = "AM"
        else
            holder.endAmPm.text = "PM"

        //setting other details
        holder.noOfPresent.text = "${item.noOfPresets}P"
        holder.noOfAbsent.text = "${item.noOfAbsent}A"

        holder.cardBackground.setBackgroundResource(item.res)
        holder.cardBackground.setTag(CentralVariables.KEY_SESSION_ID, item.id)
        holder.cardBackground.setOnClickListener(listener)

        if (menulistener != null) {
            holder.menu.visibility = View.VISIBLE
            holder.menu.setTag(CentralVariables.KEY_POSITION, position)
            holder.menu.setOnClickListener(menulistener)
        }
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentSessionListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val cardBackground = binding.sessionCardBackground
        val startDate = binding.sessionStartDate
        val endDate = binding.sessionEndDate
        val startTime = binding.sessionStartTime
        val endTime = binding.sessionEndTime
        val startAmPm = binding.sessionStartTimeAmPm
        val endAmPm = binding.sessionEndTimeAmPm
        val noOfPresent = binding.sessionPresentCount
        val noOfAbsent = binding.sessionAbsentCount
        val menu = binding.sessionCardMenu
    }

}