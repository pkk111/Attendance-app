package com.pkk.android.attendance.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.google.gson.Gson
import com.pkk.android.attendance.R
import com.pkk.android.attendance.databinding.FragmentStudentBinding
import com.pkk.android.attendance.misc.CentralVariables
import com.pkk.android.attendance.misc.Utils
import com.pkk.android.attendance.models.MessageCodes
import com.pkk.android.attendance.models.MessageModel
import com.pkk.android.attendance.viewModels.StudentViewModel
import com.pkk.android.attendance.viewModels.ViewModelFactory
import java.net.Inet6Address
import java.net.NetworkInterface
import java.net.SocketException
import java.util.*

class StudentFragment : Fragment() {

    private var _binding: FragmentStudentBinding? = null
    private val binding get() = _binding!!
    private lateinit var gson: Gson
    private lateinit var message: String
    private lateinit var viewModelFactory: ViewModelFactory
    private lateinit var viewModel: StudentViewModel

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            var i = 0
            permissions.entries.forEach { if (it.value) i++ }
            if (i == permissions.size)
                openFragment()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().supportFragmentManager.setFragmentResultListener(
            CentralVariables.KEY_PULSE_FRAGMENT_MESSAGE_KEY,
            this
        ) { _, bundle ->
            updateUI(bundle.getString(CentralVariables.KEY_MESSAGE))
        }
        requireActivity().supportFragmentManager.setFragmentResultListener(
            CentralVariables.KEY_ENTER_ROLL_NUMBER_DIALOG_FRAGMENT_MESSAGE,
            this
        ) { _, bundle ->
            startDiscoveringTeacherDevice(bundle.getInt(CentralVariables.Key_ROLL_NO))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_student, container, false)

        viewModelFactory = ViewModelFactory()
        viewModel = ViewModelProvider(this, viewModelFactory).get(StudentViewModel::class.java)

        binding.studentViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        initialise()
        return binding.root
    }

    private fun initialise() {
        gson = Gson()

        binding.toolbar.setNavigationOnClickListener { closeCurrentFragment() }
        binding.attendanceStatus.setOnClickListener {
            NavHostFragment.findNavController(this).navigate(StudentFragmentDirections.actionStudentFragmentToDialogEnterRollNumberFragment())
//            DialogEnterRollNumberFragment().show(
//                childFragmentManager,
//                DialogEnterRollNumberFragment.TAG
//            )
        }
        binding.buttonSend.setOnClickListener {
            val m =
                MessageModel(
                    MessageCodes.CUSTOM,
                    binding.sendMessageEditText.text.toString()
                )
            message = gson.toJson(m)
            requestPermission()
        }
    }

    private fun startDiscoveringTeacherDevice(rollNo: Int) {
        val model = MessageModel()
        model.rollNo = rollNo
        model.isPresent = true
        model.ip = localIpAddress
        message = gson.toJson(model)
        requestPermission()
    }

    private fun requestPermission() {
        val perms = MutableList(0) { "" }
        for (permission in REQUIRED_PERMISSIONS)
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    permission
                ) == PackageManager.PERMISSION_DENIED && !shouldShowRequestPermissionRationale(
                    permission
                )
            )
                perms.add(permission)

        requestPermissionLauncher.launch(REQUIRED_PERMISSIONS)
    }

    private fun openFragment() {
        when {
            Utils.isHotspotOn(requireContext()) -> {
                Utils.showShortToast(requireContext(), "Turn off Hotspot")
            }
//            Utils.isWifiConnected(requireContext()) -> Utils.showShortToast(
//                requireContext(), "Please Turn off Wifi"
//            )
            Utils.isGPSLocationOff(requireContext()) -> {
                Utils.showShortToast(requireContext(), "Please Turn on GPS Location")
            }
            else -> {
                NavHostFragment.findNavController(this).navigate(
                    StudentFragmentDirections.actionStudentFragmentToPulseLayoutFragment(message)
                )
            }
        }
    }

    private fun updateUI(message: String?) {
        val model = gson.fromJson(message, MessageModel::class.java)
        when (model.messageCodes) {
            MessageCodes.NORMAL -> {
                viewModel.setAttendanceStatus(model.isPresent)
            }
            MessageCodes.CUSTOM -> {
                viewModel.addClientServerMessage(model.message)
            }
            else -> Utils.showShortToast(
                requireContext(), model.messageCodes.message
            )
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar?.hide()
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity).supportActionBar?.show()
    }

    private fun closeCurrentFragment() {
        requireActivity().onBackPressed()
    }

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.INTERNET
        )

        val localIpAddress: String?
            get() {
                try {
                    val interfaceList = Collections.list(NetworkInterface.getNetworkInterfaces())
                    for (netWorkInterface in interfaceList) {
                        val enumIpAddress = netWorkInterface.inetAddresses
                        for (inetAddress in enumIpAddress) {
                            if (!inetAddress.isLoopbackAddress && inetAddress is Inet6Address) {
                                return inetAddress.getHostAddress()
                            }
                        }
                    }
                } catch (ex: SocketException) {
                    ex.printStackTrace()
                }
                return null
            }
    }
}