package com.pkk.android.attendance.fragments

import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.pkk.android.attendance.R
import com.pkk.android.attendance.adapter.TeacherAdapter
import com.pkk.android.attendance.connectionSetup.Advertiser
import com.pkk.android.attendance.connectionSetup.PayloadHandler
import com.pkk.android.attendance.interfaces.PassDataListener
import com.pkk.android.attendance.interfaces.PayloadCallbackListener
import com.pkk.android.attendance.misc.AttendanceMarker
import com.pkk.android.attendance.misc.MessageExtractor
import com.pkk.android.attendance.misc.Utils
import kotlinx.android.synthetic.main.fragment_teacher.*

class TeacherFragment : Fragment(), View.OnClickListener, PayloadCallbackListener,
    PassDataListener {
    private var teacherAdapter: TeacherAdapter? = null
    private var extractor: MessageExtractor? = null
    private var start = 0
    private var isRunning = false
    private var advertiser: Advertiser? = null

    val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            permissions.entries.forEach {
                val permissionName = it.key
                val isGranted = it.value
                if (isGranted) {
                    showAlertDialogForRange()
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_teacher, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initialize()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun initialize() {
        teacherAdapter = TeacherAdapter(requireContext())
        buttonReceiving!!.setOnClickListener(this)
        refresh!!.setOnClickListener(this)
        addRollNumber!!.setOnClickListener(this)
    }

    private fun setRecyclerView() {
        recyclerView!!.adapter = teacherAdapter
        recyclerView!!.layoutManager = GridLayoutManager(requireContext(), 2)
        teacherAdapter!!.setAttendance(extractor!!.students)
        teacherAdapter!!.notifyDataSetChanged()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.buttonReceiving -> if (!isRunning) requestPermission() else stopTakingAttendance()
            R.id.refresh -> teacherAdapter!!.notifyDataSetChanged()
            R.id.addRollNumber -> {
                val builder = AlertDialog.Builder(requireContext())
                val inflater = LayoutInflater.from(requireContext())
                val entryView = inflater.inflate(R.layout.layout_alert_dialog_add_roll_no, null)
                val rollEditText =
                    entryView.findViewById<EditText>(R.id.alert_dialog_add_roll_edittext)
                builder.setView(entryView).setTitle("Roll Number to be Added ")

                // Set up the buttons
                builder.setPositiveButton("Start") { _: DialogInterface?, _: Int ->
                    val roll = rollEditText.text.toString().toInt()
                    extractor!!.addStud(requireContext(), roll)
                    teacherAdapter!!.notifyItemInserted(extractor!!.students.size - 1)
                }
                builder.setNegativeButton("Cancel") { dialog: DialogInterface, _: Int -> dialog.cancel() }
                builder.show()
            }
        }
    }

    private fun stopTakingAttendance() {
        isRunning = false
        advertiser!!.stopAdvertising()
        addRollNumber!!.visibility = View.INVISIBLE
        refresh!!.visibility = View.INVISIBLE
        buttonReceiving!!.setText(R.string.start)
        buttonReceiving!!.setBackgroundResource(R.drawable.start_button_background)
        Log.d("StopAttendance", "Stopped all tasks taking attendance")
    }

    private fun showAlertDialogForRange() {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = LayoutInflater.from(requireContext())
        val entryView = inflater.inflate(R.layout.alert_label_editor, null)
        val startRoll = entryView.findViewById<EditText>(R.id.start)
        val endRoll = entryView.findViewById<EditText>(R.id.end)
        builder.setView(entryView).setTitle("Enter RollNo Range ")

        // Set up the buttons
        builder.setPositiveButton("Start") { _: DialogInterface?, _: Int ->
            var end = 0
            if (startRoll.text.toString().isNotEmpty() && endRoll.text.toString()
                    .isNotEmpty()
            ) {
                try {
                    start = startRoll.text.toString().toInt()
                    end = endRoll.text.toString().toInt()
                } catch (e: Exception) {
                    toast("Enter RollNo correctly")
                }
                if (start <= end) {
                    extractor = MessageExtractor(start, end)
                    setRecyclerView()
                    advertiser = Advertiser(requireActivity())
                    advertiser!!.startAdvertising(this)
                    isRunning = true
                    refresh!!.isEnabled = true
                    buttonReceiving!!.setText(R.string.stop)
                    buttonReceiving!!.setBackgroundResource(R.drawable.stop_button_background)
                    addRollNumber!!.visibility = View.VISIBLE
                    refresh!!.visibility = View.VISIBLE
                } else toast("Enter the Starting and Ending RollNo correctly")
            } else toast("Enter both Starting and Ending RollNo")
        }
        builder.setNegativeButton("Cancel") { dialog: DialogInterface, _: Int -> dialog.cancel() }
        builder.show()
    }

    private fun requestPermission() {
        requestPermissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION))
    }

    private fun toast(message: String?) {
        Utils.showShortToast(requireContext(), message)
    }

    override fun onPayloadReceived(message: String?, endpointId: String?) {
        val marker = AttendanceMarker(requireContext(), extractor!!, teacherAdapter!!, this)
        val result = marker.markAttendance(message!!)
        PayloadHandler(requireContext()).sendString(endpointId!!, result)
        advertiser!!.disconnect(endpointId)
    }

    override fun passData(string: String?) {
        textFromClient!!.append(String.format("\nFrom Client: %s", string))
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            TeacherFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}