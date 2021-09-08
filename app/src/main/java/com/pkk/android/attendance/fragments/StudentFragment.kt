package com.pkk.android.attendance.fragments

import android.Manifest
import android.app.AlertDialog
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.setPadding
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.pkk.android.attendance.R
import com.pkk.android.attendance.misc.CentralVariables
import com.pkk.android.attendance.misc.Utils
import com.pkk.android.attendance.models.MessageCodes
import com.pkk.android.attendance.models.MessageModel
import kotlinx.android.synthetic.main.fragment_student.*
import java.net.Inet4Address
import java.net.NetworkInterface
import java.net.SocketException

class StudentFragment : Fragment(), View.OnClickListener {

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
            CentralVariables.KEY_FRAGMENT_MESSAGE_KEY,
            this
        ) { _, bundle ->
            updateUI(bundle.getString("message"))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_student, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialise()
    }

    private fun initialise() {
        attendanceStatus!!.setOnClickListener(this)
        buttonSend.setOnClickListener(this)
        gson = Gson()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.buttonSend -> {
                val m = MessageModel(MessageCodes.CUSTOM, sendMessageEditText!!.text.toString())
                message = gson!!.toJson(m)
                requestPermission()
            }
            R.id.attendanceStatus -> {
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("Enter Your Roll No")

                // Set up the input
                val input = EditText(requireContext())
                input.inputType = InputType.TYPE_CLASS_NUMBER
                input.setPadding(20)
                builder.setView(input)

                // Set up the buttons
                builder.setPositiveButton("OK") { _, _ ->
                    rollNo = input.text.toString()
                    if (rollNo!!.isNotEmpty()) {
                        val model = MessageModel()
                        model.rollNo = rollNo!!.toInt()
                        model.isPresent = true
                        model.ip = localIpAddress
                        message = gson!!.toJson(model)
                        requestPermission()
                    } else Utils.showShortToast(
                        requireContext(),
                        "Enter Roll No to mark your attendance"
                    )
                }
                builder.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
                builder.show()
            }
        }
    }

    private fun requestPermission() {
        requestPermissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION))
    }

    private fun openFragment() {
        try {
            val ft = requireActivity().supportFragmentManager.beginTransaction()
            ft.setCustomAnimations(
                R.anim.slide_in_from_top_left,
                R.anim.slide_out_to_top_left,
                R.anim.slide_in_from_top_left,
                R.anim.slide_out_to_top_left
            )
            ft.replace(R.id.empty_layout, PulseLayoutFragment.newInstance(message))
                .addToBackStack(null).commit()
        } catch (e: Exception) {
            Log.e("error", "exception is $e")
        }
    }

    private fun updateUI(message: String?) {
        val model = gson!!.fromJson(message, MessageModel::class.java)
        when (model.messageCodes) {
            MessageCodes.NORMAL -> {
                attendanceStatus!!.text = getString(R.string.present_marked)
                attendanceStatus!!.isClickable = false
                attendanceStatus!!.setBackgroundResource(R.drawable.start_button_background)
            }
            MessageCodes.CUSTOM -> {
                val s = replyFromServerTextView!!.text.toString()
                if (s == "null")
                    return
                replyFromServerTextView!!.append(
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