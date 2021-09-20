package com.pkk.android.attendance.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.GridLayoutManager
import com.pkk.android.attendance.R
import com.pkk.android.attendance.adapter.MeetingRecyclerViewAdapter
import com.pkk.android.attendance.databinding.FragmentMeetingsBinding
import com.pkk.android.attendance.misc.CentralVariables
import com.pkk.android.attendance.models.MeetingModel

class MeetingsFragment : Fragment() {

    private var _binding: FragmentMeetingsBinding? = null
    private val binding get() = _binding!!

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
        val list = ArrayList<MeetingModel>()
        list.add(MeetingModel(0, "title", "details", 0))
        if (list.size > 0)
            with(binding.meetingList) {
                layoutManager = GridLayoutManager(context, 2)
                adapter = MeetingRecyclerViewAdapter(list, onClickListener)
            }
        else
            binding.fragmentMeetingTextView.visibility = View.VISIBLE
        return binding.root
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            MeetingsFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}