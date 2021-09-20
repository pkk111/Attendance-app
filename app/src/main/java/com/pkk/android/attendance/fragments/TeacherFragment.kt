package com.pkk.android.attendance.fragments

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.pkk.android.attendance.R
import com.pkk.android.attendance.adapter.TeacherAdapter
import com.pkk.android.attendance.connectionSetup.Advertiser
import com.pkk.android.attendance.connectionSetup.PayloadHandler
import com.pkk.android.attendance.databinding.FragmentTeacherBinding
import com.pkk.android.attendance.dialogFragment.DialogAttendanceRangeFragment
import com.pkk.android.attendance.dialogFragment.DialogEnterRollNumberFragment
import com.pkk.android.attendance.dialogFragment.DialogSaveAttendanceFragment
import com.pkk.android.attendance.interfaces.PassDataListener
import com.pkk.android.attendance.interfaces.PayloadCallbackListener
import com.pkk.android.attendance.misc.AttendanceMarker
import com.pkk.android.attendance.misc.CentralVariables
import com.pkk.android.attendance.misc.MessageExtractor

class TeacherFragment : Fragment(), View.OnClickListener, PayloadCallbackListener,
    PassDataListener {

    private var _binding: FragmentTeacherBinding? = null
    private val binding get() = _binding!!
    private var teacherAdapter: TeacherAdapter? = null
    private var extractor: MessageExtractor? = null
    private var isRunning = false
    private var advertiser: Advertiser? = null

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            permissions.entries.forEach {
                val isGranted = it.value
                if (isGranted) {
                    DialogAttendanceRangeFragment().show(
                        childFragmentManager,
                        DialogAttendanceRangeFragment.TAG
                    )
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().supportFragmentManager.setFragmentResultListener(
            CentralVariables.KEY_SAVE_ATTENDANCE_DIALOG_FRAGMENT_MESSAGE,
            this
        ) { _, bundle ->
            stopTakingAttendance(bundle.getBoolean(CentralVariables.KEY_CANCELLED, false))
        }
        requireActivity().supportFragmentManager.setFragmentResultListener(
            CentralVariables.KEY_START_ATTENDANCE_DIALOG_FRAGMENT_MESSAGE,
            this
        ) { _, bundle ->
            startTakingAttendance(
                bundle.getBoolean(CentralVariables.KEY_CANCELLED),
                bundle.getInt(CentralVariables.KEY_START),
                bundle.getInt(CentralVariables.KEY_END)
            )
        }
        requireActivity().supportFragmentManager.setFragmentResultListener(
            CentralVariables.KEY_ENTER_ROLL_NUMBER_DIALOG_FRAGMENT_MESSAGE,
            this
        ) { _, bundle ->
            addRollNumber(bundle.getInt(CentralVariables.Key_ROLL_NO))
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTeacherBinding.inflate(inflater, container, false)
        initialize()
        return binding.root
    }

    private fun initialize() {
        teacherAdapter = TeacherAdapter(requireContext())
        binding.buttonReceiving.setOnClickListener(this)
        binding.refresh.setOnClickListener(this)
        binding.addRollNumber.setOnClickListener(this)
    }

    private fun setRecyclerView() {
        binding.recyclerView.adapter = teacherAdapter
        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        teacherAdapter!!.setAttendance(extractor!!.students)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.buttonReceiving -> if (!isRunning) requestPermission() else
                DialogSaveAttendanceFragment().show(
                    childFragmentManager,
                    DialogSaveAttendanceFragment.TAG
                )
            R.id.refresh -> teacherAdapter!!.notifyDataSetChanged()
            R.id.addRollNumber -> DialogEnterRollNumberFragment().show(
                childFragmentManager,
                DialogEnterRollNumberFragment.TAG
            )
        }
    }

    private fun startTakingAttendance(isCancelled: Boolean, start: Int, end: Int) {
        if (!isCancelled) {
            extractor = MessageExtractor(start, end)
            setRecyclerView()
            advertiser = Advertiser(requireContext(), CentralVariables.P2P_STRATEGY)
            advertiser!!.startAdvertising(this)
            isRunning = true
            binding.refresh.isEnabled = true
            binding.buttonReceiving.setText(R.string.stop)
            binding.buttonReceiving.setBackgroundResource(R.drawable.stop_button_background)
            binding.addRollNumber.visibility = View.VISIBLE
            binding.refresh.visibility = View.VISIBLE
        }
    }

    private fun stopTakingAttendance(isCancelled: Boolean) {
        if (!isCancelled) {
            isRunning = false
            advertiser?.stopAdvertising()
            binding.addRollNumber.visibility = View.INVISIBLE
            binding.refresh.visibility = View.INVISIBLE
            binding.buttonReceiving.setText(R.string.start)
            binding.buttonReceiving.setBackgroundResource(R.drawable.start_button_background)
            teacherAdapter?.notifyItemRangeRemoved(0, extractor?.students!!.size)
        }
    }

    private fun requestPermission() {
        requestPermissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION))
    }

    private fun addRollNumber(roll: Int) {
        extractor!!.addStud(requireContext(), roll)
        teacherAdapter!!.notifyItemInserted(extractor!!.students.size - 1)
    }

    override fun onPayloadReceived(message: String?, endpointId: String?) {
        val marker = AttendanceMarker(requireContext(), extractor!!, teacherAdapter!!, this)
        val result = marker.markAttendance(message!!)
        PayloadHandler(requireContext()).sendString(endpointId!!, result)
        advertiser!!.disconnect(endpointId)
    }

    override fun passData(string: String?) {
        binding.textFromClient.append(String.format("\nFrom Client: %s", string))
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar?.hide()
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity).supportActionBar?.show()
    }
}