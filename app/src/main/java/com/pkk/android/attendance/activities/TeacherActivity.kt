package com.pkk.android.attendance.activities

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.nearby.Nearby
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.pkk.android.attendance.R
import com.pkk.android.attendance.adapter.TeacherAdapter
import com.pkk.android.attendance.connectionSetup.Advertiser
import com.pkk.android.attendance.connectionSetup.PayloadHandler
import com.pkk.android.attendance.interfaces.PassDataListener
import com.pkk.android.attendance.interfaces.PayloadCallbackListener
import com.pkk.android.attendance.misc.AttendanceMarker
import com.pkk.android.attendance.misc.MessageExtractor
import com.pkk.android.attendance.misc.Utils.Companion.showShortToast

class TeacherActivity : AppCompatActivity(), View.OnClickListener, PayloadCallbackListener,
    PassDataListener {
    private var refresh: FloatingActionButton? = null
    private var add: FloatingActionButton? = null
    private var buttonReceiving: Button? = null
    var textViewDataFromClient: TextView? = null
    private var recyclerView: RecyclerView? = null
    private var teacherAdapter: TeacherAdapter? = null
    private var extractor: MessageExtractor? = null
    private var start = 0
    private var isRunning = false
    private var advertiser: Advertiser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_teacher)
        initialize()
    }

    private fun initialize() {
        buttonReceiving = findViewById(R.id.btn_receiving)
        refresh = findViewById(R.id.refresh)
        add = findViewById(R.id.add_roll_no)
        textViewDataFromClient = findViewById(R.id.clientmess)
        recyclerView = findViewById(R.id.recycler_view)
        teacherAdapter = TeacherAdapter(this)
        buttonReceiving!!.setOnClickListener(this)
        refresh!!.setOnClickListener(this)
        add!!.setOnClickListener(this)
    }

    private fun setRecyclerView() {
        recyclerView!!.adapter = teacherAdapter
        recyclerView!!.layoutManager = GridLayoutManager(this, 2)
        teacherAdapter!!.setAttendance(extractor!!.students)
        teacherAdapter!!.notifyDataSetChanged()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_receiving -> if (!isRunning) {
                val builder = AlertDialog.Builder(this)
                val inflater = LayoutInflater.from(this)
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
                            advertiser = Advertiser(this)
                            advertiser!!.startAdvertising(this)
                            isRunning = true
                            refresh!!.isEnabled = true
                            buttonReceiving!!.setText(R.string.stop)
                            buttonReceiving!!.setBackgroundResource(R.drawable.stop_button_background)
                            add!!.visibility = View.VISIBLE
                            refresh!!.visibility = View.VISIBLE
                        } else toast("Enter the Starting and Ending RollNo correctly")
                    } else toast("Enter both Starting and Ending RollNo")
                }
                builder.setNegativeButton("Cancel") { dialog: DialogInterface, _: Int -> dialog.cancel() }
                builder.show()
            } else {
                isRunning = false
                advertiser!!.stopAdvertising()
                buttonReceiving!!.setText(R.string.start)
                buttonReceiving!!.setBackgroundResource(R.drawable.start_button_background)
                Log.d("StopAttendance", "Stopped all activity helping to take attendance")
            }
            R.id.refresh -> teacherAdapter!!.notifyDataSetChanged()
            R.id.add_roll_no -> {
                val builder = AlertDialog.Builder(this)
                val inflater = LayoutInflater.from(this)
                val entryView = inflater.inflate(R.layout.layout_alert_dialog_add_roll_no, null)
                val rollEditText =
                    entryView.findViewById<EditText>(R.id.alert_dialog_add_roll_edittext)
                builder.setView(entryView).setTitle("Roll Number to be Added ")

                // Set up the buttons
                builder.setPositiveButton("Start") { _: DialogInterface?, _: Int ->
                    val roll = rollEditText.text.toString().toInt()
                    extractor!!.addStud(this@TeacherActivity, roll)
                    teacherAdapter!!.notifyItemInserted(extractor!!.students.size - 1)
                }
                builder.setNegativeButton("Cancel") { dialog: DialogInterface, _: Int -> dialog.cancel() }
                builder.show()
            }
        }
    }

    private fun toast(message: String?) {
        showShortToast(this, message)
    }

    override fun onDestroy() {
        Nearby.getConnectionsClient(this).stopAdvertising()
        super.onDestroy()
    }

    override fun onPayloadReceived(message: String?, endpointId: String?) {
        val marker = AttendanceMarker(this, extractor!!, teacherAdapter!!, this)
        val result = marker.markAttendance(message!!)
        PayloadHandler(this).sendString(endpointId!!, result)
    }

    override fun passData(string: String?) {
        textViewDataFromClient!!.append(String.format("\nFrom Client: %s", string))
    }

    override fun onBackPressed() {
        startActivity(Intent(this, LoginActivity::class.java))
        super.onBackPressed()
    }
}