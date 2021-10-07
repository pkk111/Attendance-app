package com.pkk.android.attendance.fragments

import android.Manifest
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.pkk.android.attendance.R
import com.pkk.android.attendance.databinding.FragmentStudentBinding
import com.pkk.android.attendance.dialogFragment.DialogEnterRollNumberFragment
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
    private var message: String? = null
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
        gson = GsonBuilder().enableComplexMapKeySerialization().create()

        binding.toolbar.setNavigationOnClickListener { closeCurrentFragment() }
        binding.attendanceStatus.setOnClickListener {

            DialogEnterRollNumberFragment().show(
                childFragmentManager,
                DialogEnterRollNumberFragment.TAG
            )
        }
        binding.buttonSend.setOnClickListener {
            val m =
                MessageModel(
                    MessageCodes.CUSTOM,
                    binding.sendMessageEditText.text.toString()
                )
            message = gson.toJson(m)
            Log.d("json = ", message!!)
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
        requestPermissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION))
    }

    private fun openFragment() {
        NavHostFragment.findNavController(this).navigate(
            R.id.action_studentFragment_to_pulseLayoutFragment, bundleOf(
                CentralVariables.KEY_MESSAGE to message
            )
            )
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
        val localIpAddress: String?
            get() {
                try {
                    val en = Collections.list(NetworkInterface.getNetworkInterfaces())
                    for (intf in en) {
                        val enumIpAddr = intf.inetAddresses
                        for (inetAddress in enumIpAddr) {
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