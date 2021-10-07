package com.pkk.android.attendance.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.pkk.android.attendance.adapter.ShowAttendanceRecyclerViewAdapter
import com.pkk.android.attendance.databinding.FragmentShowAttendanceBinding
import com.pkk.android.attendance.misc.CentralVariables
import com.pkk.android.attendance.models.AttendanceDao
import com.pkk.android.attendance.models.AttendanceDatabase
import com.pkk.android.attendance.models.StudentModel
import com.pkk.android.attendance.viewModels.ShowAttendanceViewModel
import com.pkk.android.attendance.viewModels.ViewModelFactory

class ShowAttendanceFragment : Fragment() {

    private var _binding: FragmentShowAttendanceBinding? = null
    private val binding get() = _binding!!
    private lateinit var table: AttendanceDao
    private lateinit var viewModelFactory: ViewModelFactory
    private lateinit var viewModel: ShowAttendanceViewModel
    private lateinit var adapter: ShowAttendanceRecyclerViewAdapter
    private var columnCount = 4
    private var sessionId = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            sessionId = it.getLong(CentralVariables.KEY_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentShowAttendanceBinding.inflate(inflater, container, false)
        table = AttendanceDatabase.getDatabase(requireContext()).attendanceDao()
        viewModelFactory = ViewModelFactory(table, sessionId)
        viewModel =
            ViewModelProvider(this, viewModelFactory).get(ShowAttendanceViewModel::class.java)

        viewModel.loadData(sessionId)
        viewModel.attendance.observe(viewLifecycleOwner) { list -> updateUI(list) }
        binding.list.layoutManager = GridLayoutManager(context, columnCount)

        return binding.root
    }

    private fun updateUI(list: List<StudentModel>) {
        if (list.isEmpty())
//            binding.fragmentMeetingTextView.visibility = View.VISIBLE
        else {
//            binding.fragmentMeetingTextView.visibility = View.GONE
            adapter = ShowAttendanceRecyclerViewAdapter(requireContext(), list)
            binding.list.adapter = adapter
        }
    }
}