package com.pkk.android.attendance.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pkk.android.attendance.R
import com.pkk.android.attendance.adapter.ShowAttendanceRecyclerViewAdapter
import com.pkk.android.attendance.models.StudentModel

class ShowAttendanceFragment : Fragment() {

    private var columnCount = 4

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_show_attendance, container, false)
        val list = ArrayList<StudentModel>()
        for (i in 1..10)
            list.add(StudentModel(i, i % 2 == 0, i.toString()))
        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                layoutManager = GridLayoutManager(context, columnCount)
                adapter = ShowAttendanceRecyclerViewAdapter(requireContext(), list)
            }
        }
        return view
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            ShowAttendanceFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}