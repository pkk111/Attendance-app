package com.pkk.android.attendance.activities

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.setPadding
import com.google.gson.Gson
import com.pkk.android.attendance.R
import com.pkk.android.attendance.fragments.PulseLayoutFragment
import com.pkk.android.attendance.interfaces.PassDataListener
import com.pkk.android.attendance.misc.Utils.Companion.showShortToast
import com.pkk.android.attendance.models.MessageCodes
import com.pkk.android.attendance.models.MessageModel
import kotlinx.android.synthetic.main.activity_student.*
import java.net.Inet4Address
import java.net.NetworkInterface
import java.net.SocketException

class StudentActivity : AppCompatActivity(), View.OnClickListener, PassDataListener {
    private var rollNo: String? = null
    private var gson: Gson? = null
    private var message: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student)
        initialise()
    }

    private fun initialise() {
        status!!.setOnClickListener(this)
        buttonSend.setOnClickListener(this)
        gson = Gson()

    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.buttonSend -> {
                val m = MessageModel(MessageCodes.CUSTOM, sendMessageEditText!!.text.toString())
                message = gson!!.toJson(m)
                openFragment()
            }
            R.id.status -> {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Enter Your Roll No")

                // Set up the input
                val input = EditText(this)
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
                        openFragment()
                    } else showShortToast(this, "Enter Roll No to mark your attendance")
                }
                builder.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
                builder.show()
            }
        }
    }

    private fun openFragment() {
        try {
            supportFragmentManager.beginTransaction()
                .replace(R.id.empty_layout, PulseLayoutFragment.newInstance(message))
                .addToBackStack(null).commit()
        } catch (e: Exception) {
            Log.e("error", "exception is $e")
        }
    }

    private fun updateUI(message: String?) {
        val model = gson!!.fromJson(message, MessageModel::class.java)
        when (model.messageCodes) {
            MessageCodes.NORMAL -> {
                status!!.text = getString(R.string.present_marked)
                status!!.isClickable = false
                status!!.setBackgroundResource(R.drawable.start_button_background)
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
            else -> showShortToast(
                this, model.messageCodes.message
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
    }

    override fun passData(string: String?) {
        updateUI(string)
    }

    override fun onBackPressed() {
        if (supportFragmentManager.findFragmentById(R.id.empty_layout) == null)
            startActivity(Intent(this, HomeActivity::class.java))
        super.onBackPressed()
    }

}