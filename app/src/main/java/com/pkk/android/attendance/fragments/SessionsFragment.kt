package com.pkk.android.attendance.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.pkk.android.attendance.R
import com.pkk.android.attendance.adapter.SessionRecyclerViewAdapter
import com.pkk.android.attendance.databinding.FragmentSessionBinding
import com.pkk.android.attendance.misc.CentralVariables
import com.pkk.android.attendance.misc.Utils
import com.pkk.android.attendance.models.SessionModel
import java.util.*
import kotlin.collections.ArrayList

class SessionsFragment : Fragment() {
    private var _binding: FragmentSessionBinding? = null
    private val binding get() = _binding!!

    private val onClickListener = View.OnClickListener { v ->
        NavHostFragment.findNavController(this).navigate(
            R.id.action_sessionsFragment_to_showAttendanceFragment,
            bundleOf(CentralVariables.KEY_ID to v.getTag(CentralVariables.KEY_SESSION_ID))
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
                    binding.sessionList.adapter!!.notifyItemRemoved(position)
                }
            }
            true
        }
        menu.show()
    }

    private var meetingId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            meetingId = it.getInt(CentralVariables.KEY_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSessionBinding.inflate(inflater, container, false)
        val sessionList = ArrayList<SessionModel>()
        sessionList.add(
            SessionModel(
                0,
                Calendar.getInstance().time,
                Calendar.getInstance().time,
                50,
                5,
                Utils.getBackgrounds()[0]
            )
        )

        // Set the adapter
        if (sessionList.size > 0)
            with(binding.sessionList) {
                layoutManager = LinearLayoutManager(context)
                adapter = SessionRecyclerViewAdapter(sessionList, onClickListener, menuListener)
            }
        else
            binding.fragmentMeetingTextView.visibility = View.VISIBLE
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            SessionsFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}