package com.pkk.android.attendance.dialogFragment

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.pkk.android.attendance.databinding.FragmentDialogSaveAttendanceBinding
import com.pkk.android.attendance.misc.CentralVariables
import com.pkk.android.attendance.misc.Utils
import com.pkk.android.attendance.models.MeetingModel
import com.pkk.android.attendance.models.StudentModel

class DialogSaveAttendanceFragment : DialogFragment(), AdapterView.OnItemSelectedListener {

    private var _binding: FragmentDialogSaveAttendanceBinding? = null
    private val binding get() = _binding!!
    private val list = ArrayList<MeetingModel>()
    private val spinnerList = ArrayList<String>()
    private var attendance = ArrayList<StudentModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            attendance =
                it.getSerializable(CentralVariables.KEY_MEETING_ID) as ArrayList<StudentModel>
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDialogSaveAttendanceBinding.inflate(LayoutInflater.from(context))
        //set dialog window
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)

        initialize()
        return binding.root
    }

    private fun initialize() {
        binding.fragmentSaveAttendanceClassesSpinner.onItemSelectedListener = this
        //add sample data
        list.add(MeetingModel(1, "Sample", "details", Utils.getBackgrounds()[0]))

        spinnerList.add("Create New Class")
        for (i in 0 until list.size)
            spinnerList.add(i + 1, list[i].title)

        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, spinnerList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding.fragmentSaveAttendanceClassesSpinner.adapter = adapter

        //setting default selected item for better UX
        if (spinnerList.size > 1)
            binding.fragmentSaveAttendanceClassesSpinner.setSelection(1)
        else {
            binding.fragmentSaveAttendanceClassesSpinner.setSelection(0)
            binding.fragmentSaveAttendanceClassName.visibility = View.VISIBLE
        }

        binding.fragmentSaveAttendanceSave.setOnClickListener { saveAttendance() }
        binding.fragmentSaveAttendanceDontSave.setOnClickListener { closeDialog(false) }
        binding.fragmentSaveAttendanceCancel.setOnClickListener { closeDialog(true) }
    }

    private fun closeDialog(isCanceled: Boolean) {
        requireActivity().supportFragmentManager.setFragmentResult(
            CentralVariables.KEY_SAVE_ATTENDANCE_DIALOG_FRAGMENT_MESSAGE,
            bundleOf(CentralVariables.KEY_CANCELLED to isCanceled)
        )
        this.dismiss()
    }

    private fun saveAttendance() {
        closeDialog(false)
        //TODO save the attendance
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        if (p2 == 0)
            binding.fragmentSaveAttendanceClassName.visibility = View.VISIBLE
        else
            binding.fragmentSaveAttendanceClassName.visibility = View.GONE
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        )
    }

    companion object {
        const val TAG = "SaveAttendanceDialog"
    }
}