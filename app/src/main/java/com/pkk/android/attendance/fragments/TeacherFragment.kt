package com.pkk.android.attendance.fragments

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.pkk.android.attendance.R
import com.pkk.android.attendance.adapter.TeacherAdapter
import com.pkk.android.attendance.connectionSetup.Advertiser
import com.pkk.android.attendance.connectionSetup.PayloadHandler
import com.pkk.android.attendance.database.AttendanceDao
import com.pkk.android.attendance.database.AttendanceDatabase
import com.pkk.android.attendance.database.SessionDao
import com.pkk.android.attendance.databinding.FragmentTeacherBinding
import com.pkk.android.attendance.dialogFragment.DialogAttendanceRangeFragment
import com.pkk.android.attendance.dialogFragment.DialogEnterRollNumberFragment
import com.pkk.android.attendance.dialogFragment.DialogSaveAttendanceFragment
import com.pkk.android.attendance.interfaces.ChangeAttendanceStatusListener
import com.pkk.android.attendance.interfaces.PayloadCallbackListener
import com.pkk.android.attendance.misc.CentralVariables
import com.pkk.android.attendance.misc.Utils
import com.pkk.android.attendance.viewModels.TeacherViewModel
import com.pkk.android.attendance.viewModels.ViewModelFactory

class TeacherFragment : Fragment(), View.OnClickListener, PayloadCallbackListener,
    ChangeAttendanceStatusListener {

    private var _binding: FragmentTeacherBinding? = null
    private val binding get() = _binding!!
    private var teacherAdapter: TeacherAdapter? = null
    private var advertiser: Advertiser? = null
    private lateinit var viewModelFactory: ViewModelFactory
    private lateinit var viewModel: TeacherViewModel
    private lateinit var attendanceTable: AttendanceDao
    private lateinit var sessionTable: SessionDao

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            var count = 0
            permissions.entries.forEach {
                val isGranted = it.value
                if (isGranted) {
                    count++
                }
            }
            if(count == permissions.size)
                openDialog()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().supportFragmentManager.setFragmentResultListener(
            CentralVariables.KEY_SAVE_ATTENDANCE_DIALOG_FRAGMENT_MESSAGE,
            this
        ) { _, bundle ->
            stopTakingAttendance(
                bundle.getBoolean(CentralVariables.KEY_CANCELLED, false),
                bundle.getLong(CentralVariables.KEY_MEETING_ID, -1L)
            )
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
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_teacher, container, false)

        sessionTable = AttendanceDatabase.getDatabase(requireContext()).sessionDao()
        attendanceTable = AttendanceDatabase.getDatabase(requireContext()).attendanceDao()
        viewModelFactory = ViewModelFactory(sessionTable, attendanceTable)
        viewModel = ViewModelProvider(this, viewModelFactory).get(TeacherViewModel::class.java)

        binding.teacherViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        initialize()
        return binding.root
    }

    private fun initialize() {
        binding.toolbar.setNavigationOnClickListener { closeCurrentFragment() }
        binding.buttonReceiving.setOnClickListener(this)
        binding.refresh.setOnClickListener(this)
        binding.addRollNumber.setOnClickListener(this)
        viewModel.isRunning.observe(viewLifecycleOwner) { if (!it!!) stopAllProcesses() }
        if (viewModel.isRunning.value!!)
            startTakingAttendance(false, viewModel.start, viewModel.end)
    }

    private fun setRecyclerView() {
        teacherAdapter = TeacherAdapter(this)
        teacherAdapter!!.submitList(viewModel.extractor.students)
        binding.recyclerView.adapter = teacherAdapter
        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.buttonReceiving -> if (!viewModel.isRunning.value!!) requestPermission() else
                DialogSaveAttendanceFragment().show(
                    childFragmentManager,
                    DialogSaveAttendanceFragment.TAG
                )
            R.id.refresh -> teacherAdapter!!.submitList(viewModel.extractor.students)
            R.id.addRollNumber -> DialogEnterRollNumberFragment().show(
                childFragmentManager,
                DialogEnterRollNumberFragment.TAG
            )
        }
    }

    private fun startTakingAttendance(isCancelled: Boolean, start: Int, end: Int) {
        if (!isCancelled) {
            if (!viewModel.isRunning.value!!) {
                viewModel.startRunning(start, end)
                advertiser = Advertiser(requireContext(), CentralVariables.STAR_STRATEGY)
                advertiser!!.startAdvertising(this)
            }
            setRecyclerView()
        }
    }

    private fun stopTakingAttendance(isCancelled: Boolean, id: Long) {
        if (!isCancelled) {
            if (id != -1L) {
                viewModel.saveAttendanceAt(id)
            } else viewModel.stopRunning()
        }
    }

    private fun requestPermission() {
        requestPermissionLauncher.launch(REQUIRED_PERMISSIONS)
    }

    private fun openDialog() {
        when {
            Utils.isHotspotOn(requireContext()) -> {
                Utils.showShortToast(
                    requireContext(),
                    resources.getString(R.string.turn_off_hotspot)
                )
            }
//            Utils.isWifiConnected(requireContext()) -> Utils.showShortToast(
//                requireContext(),
//                resources.getString(R.string.turn_off_wifi)
//            )
            Utils.isGPSLocationOff(requireContext()) -> {
                Utils.showShortToast(requireContext(), "Please Turn on GPS Location")
            }
            else -> {
                DialogAttendanceRangeFragment().show(
                    childFragmentManager,
                    DialogAttendanceRangeFragment.TAG
                )
            }
        }
    }

    private fun addRollNumber(roll: Int) {
        if (viewModel.addStudent(roll))
            teacherAdapter!!.notifyItemInserted(viewModel.getSize() - 1)
        else
            Utils.showShortToast(requireContext(), "$roll already present for evaluation.")
    }

    override fun onPayloadReceived(message: String, endpointId: String) {
        val result = viewModel.markAttendance(message, advertiser!!.getDeviceMap(endpointId))
        if (result.first == -1)
            binding.textFromClient.append(String.format("\nFrom Client: %s", result.second))
        else
            teacherAdapter?.notifyItemChanged(result.first)
        PayloadHandler(requireContext()).sendString(endpointId, result.second)
        advertiser!!.disconnect(endpointId)
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar?.hide()
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity).supportActionBar?.show()
    }

    override fun changeStatusOf(index: Int) {
        viewModel.changeStatus(index)
        teacherAdapter!!.notifyItemChanged(index)
    }

    private fun stopAllProcesses() {
        advertiser?.stopAdvertising()
        teacherAdapter?.notifyItemRangeRemoved(0, viewModel.getSize())
    }

    private fun closeCurrentFragment() {
        requireActivity().onBackPressed()
    }

    override fun onDestroyView() {
        viewModel.stopRunningWithoutSaving()
        advertiser?.stopAdvertising()
        super.onDestroyView()
    }

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }
}