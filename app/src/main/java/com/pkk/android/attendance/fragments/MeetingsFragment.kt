package com.pkk.android.attendance.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.GridLayoutManager
import com.pkk.android.attendance.R
import com.pkk.android.attendance.adapter.MeetingRecyclerViewAdapter
import com.pkk.android.attendance.databinding.FragmentMeetingsBinding
import com.pkk.android.attendance.misc.CentralVariables
import com.pkk.android.attendance.models.AttendanceDatabase
import com.pkk.android.attendance.models.ClassDao
import com.pkk.android.attendance.models.MeetingModel
import com.pkk.android.attendance.viewModels.MeetingsViewModel
import com.pkk.android.attendance.viewModels.ViewModelFactory

class MeetingsFragment : Fragment() {

    private var _binding: FragmentMeetingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var table: ClassDao
    private lateinit var viewModelFactory: ViewModelFactory
    private lateinit var viewModel: MeetingsViewModel
    private lateinit var adapter: MeetingRecyclerViewAdapter

    private val onClickListener = View.OnClickListener { v ->
        NavHostFragment.findNavController(this).navigate(
            R.id.action_nav_meetings_to_sessionsFragment,
            bundleOf(CentralVariables.KEY_ID to v.getTag(CentralVariables.KEY_SESSION_ID))
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMeetingsBinding.inflate(inflater, container, false)

        table = AttendanceDatabase.getDatabase(requireContext()).classDao()
        viewModelFactory = ViewModelFactory(table)
        viewModel =
            ViewModelProvider(this, viewModelFactory).get(MeetingsViewModel::class.java)

        binding.meetingViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        viewModel.loadData()
        viewModel.classes.observe(viewLifecycleOwner) { list -> updateUI(list) }

        binding.meetingList.layoutManager = GridLayoutManager(context, 2)

        return binding.root
    }

    private fun updateUI(list: List<MeetingModel>) {
        if (list.isEmpty())
            binding.fragmentMeetingTextView.visibility = View.VISIBLE
        else {
            binding.fragmentMeetingTextView.visibility = View.GONE
            adapter = MeetingRecyclerViewAdapter(list, onClickListener)
            binding.meetingList.adapter = adapter
        }
    }

}