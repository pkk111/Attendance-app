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
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.google.gson.Gson
import com.pkk.android.attendance.R
import com.pkk.android.attendance.databinding.FragmentStudentBinding
import com.pkk.android.attendance.dialogFragment.DialogEnterRollNumberFragment
import com.pkk.android.attendance.misc.CentralVariables
import com.pkk.android.attendance.misc.Utils
import com.pkk.android.attendance.models.MessageCodes
import com.pkk.android.attendance.models.MessageModel
import java.net.Inet4Address
import java.net.NetworkInterface
import java.net.SocketException

class StudentFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentStudentBinding? = null
    private val binding get() = _binding!!
    private var rollNo: String? = null
    private var gson: Gson? = null
    private var message: String? = null

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            permissions.entries.forEach {
                val isGranted = it.value
                if (isGranted) {
                    openFragment()
                }
            }
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
        _binding = FragmentStudentBinding.inflate(inflater, container, false)
        initialise()
        return binding.root
    }

    private fun initialise() {
        binding.attendanceStatus.setOnClickListener(this)
        binding.buttonSend.setOnClickListener(this)
        gson = Gson()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.buttonSend -> {
                val m =
                    MessageModel(MessageCodes.CUSTOM, binding.sendMessageEditText.text.toString())
                message = gson!!.toJson(m)
                requestPermission()
            }
            R.id.attendanceStatus -> {
                DialogEnterRollNumberFragment().show(
                    childFragmentManager,
                    DialogEnterRollNumberFragment.TAG
                )
            }
        }
    }

    private fun requestPermission() {
        requestPermissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION))
    }

    private fun openFragment() {
        try {
            NavHostFragment.findNavController(this).navigate(
                R.id.action_studentFragment_to_pulseLayoutFragment, bundleOf(
                    CentralVariables.KEY_MESSAGE to message
                )
            )
        } catch (e: Exception) {
            Log.e("error", "exception is $e")
        }
    }

    private fun updateUI(message: String?) {
        val model = gson!!.fromJson(message, MessageModel::class.java)
        when (model.messageCodes) {
            MessageCodes.NORMAL -> {
                binding.attendanceStatus.text = getString(R.string.present_marked)
                binding.attendanceStatus.isClickable = false
                binding.attendanceStatus.setBackgroundResource(R.drawable.start_button_background)
            }
            MessageCodes.CUSTOM -> {
                val s = binding.replyFromServerTextView.text.toString()
                if (s == "null")
                    return
                binding.replyFromServerTextView.append(
                    String.format(
                        "$s\n From Server : %s",
                        model.message
                    )
                )
            }
            else -> Utils.showShortToast(
                requireContext(), model.messageCodes.message
            )
        }
    }

    private fun startDiscoveringTeacherDevice(rollNo: Int) {
        val model = MessageModel()
        model.rollNo = rollNo
        model.isPresent = true
        model.ip = localIpAddress
        message = gson!!.toJson(model)
        requestPermission()
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar?.hide()
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity).supportActionBar?.show()
    }

    companion object {

        val localIpAddress: String?
            get() {
                try {
                    val en = NetworkInterface.getNetworkInterfaces()
                    while (en.hasMoreElements()) {
                        val intf = en.nextElement()
                        val enumIpAddr = intf.inetAddresses
                        while (enumIpAddr.hasMoreElements()) {
                            val inetAddress = enumIpAddr.nextElement()
                            if (!inetAddress.isLoopbackAddress && inetAddress is Inet4Address) {
                                return inetAddress.getHostAddress()
                            }
                        }
                    }
                } catch (ex: SocketException) {
                    ex.printStackTrace()
                }
                return null
            }

        @JvmStatic
        fun newInstance() =
            StudentFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}