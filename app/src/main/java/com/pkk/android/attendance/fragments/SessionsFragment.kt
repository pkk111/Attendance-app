package com.pkk.android.attendance.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.pkk.android.attendance.R
import com.pkk.android.attendance.adapter.SessionRecyclerViewAdapter
import com.pkk.android.attendance.databinding.FragmentSessionBinding
import com.pkk.android.attendance.misc.CentralVariables
import com.pkk.android.attendance.database.AttendanceDatabase
import com.pkk.android.attendance.database.SessionDao
import com.pkk.android.attendance.models.SessionModel
import com.pkk.android.attendance.viewModels.SessionsViewModel
import com.pkk.android.attendance.viewModels.ViewModelFactory

class SessionsFragment : Fragment() {

    private var _binding: FragmentSessionBinding? = null
    private val binding get() = _binding!!
    private lateinit var table: SessionDao
    private lateinit var viewModelFactory: ViewModelFactory
    private lateinit var viewModel: SessionsViewModel
    private lateinit var adapter: SessionRecyclerViewAdapter

    private var meetingId: Long = -1L
    private val onClickListener = View.OnClickListener { v ->
        NavHostFragment.findNavController(this).navigate(
            SessionsFragmentDirections.actionSessionsFragmentToShowAttendanceFragment(
                v.getTag(
                    CentralVariables.KEY_SESSION_ID
                ) as Long
            )
        )
    }
    private val menuListener = View.OnClickListener {
        val position = it.getTag(CentralVariables.KEY_POSITION) as Int
        val menu = PopupMenu(context, it)
        menu.inflate(R.menu.session_item_menu)
        menu.setOnMenuItemClickListener { p0 ->
            when (p0?.itemId) {
                R.id.item_share -> {
                }
                R.id.item_delete -> {
                    viewModel.deleteItemAt(position)
                }
            }
            true
        }
        menu.show()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSessionBinding.inflate(inflater, container, false)

        val arguments = SessionsFragmentArgs.fromBundle(requireArguments())
        meetingId = arguments.meetingId

        table = AttendanceDatabase.getDatabase(requireContext()).sessionDao()
        viewModelFactory = ViewModelFactory(table)
        viewModel =
            ViewModelProvider(this, viewModelFactory).get(SessionsViewModel::class.java)

        viewModel.loadData(meetingId)
        viewModel.sessions.observe(viewLifecycleOwner) { list -> updateUI(list) }

        binding.sessionList.layoutManager = LinearLayoutManager(context)
        return binding.root
    }

    private fun updateUI(list: List<SessionModel>) {
        if (viewModel.positionRemoved != -1) {
            if (list.isEmpty())
                binding.fragmentMeetingTextView.visibility = View.VISIBLE
            else {
                binding.fragmentMeetingTextView.visibility = View.GONE
                adapter = SessionRecyclerViewAdapter(list, onClickListener, menuListener)
                binding.sessionList.adapter = adapter
            }
        } else {
            binding.sessionList.adapter!!.notifyItemRemoved(viewModel.positionRemoved)
        }
    }
}