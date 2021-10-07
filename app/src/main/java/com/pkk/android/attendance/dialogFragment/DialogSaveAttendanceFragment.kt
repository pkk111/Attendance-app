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
import androidx.lifecycle.ViewModelProvider
import com.pkk.android.attendance.databinding.FragmentDialogSaveAttendanceBinding
import com.pkk.android.attendance.misc.CentralVariables
import com.pkk.android.attendance.models.AttendanceDatabase
import com.pkk.android.attendance.models.ClassDao
import com.pkk.android.attendance.models.MeetingModel
import com.pkk.android.attendance.viewModels.SaveAttendanceViewModel
import com.pkk.android.attendance.viewModels.ViewModelFactory

class DialogSaveAttendanceFragment : DialogFragment(), AdapterView.OnItemSelectedListener {

    private var _binding: FragmentDialogSaveAttendanceBinding? = null
    private val binding get() = _binding!!
    private val list = ArrayList<MeetingModel>()
    private val spinnerList = ArrayList<String>()
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var table: ClassDao
    private lateinit var viewModelFactory: ViewModelFactory
    private lateinit var viewModel: SaveAttendanceViewModel
//    private var attendance = ArrayList<StudentModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
//            attendance =
//                it.getSerializable(CentralVariables.KEY_MEETING_ID) as ArrayList<StudentModel>
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDialogSaveAttendanceBinding.inflate(LayoutInflater.from(context))

        table = AttendanceDatabase.getDatabase(requireContext()).classDao()
        viewModelFactory = ViewModelFactory(table)
        viewModel =
            ViewModelProvider(this, viewModelFactory).get(SaveAttendanceViewModel::class.java)

        binding.saveAttendanceViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        viewModel.className.observe(this) { list -> setUpAdapter(list) }

        //set dialog window
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)

        initialize()
        return binding.root
    }

    private fun initialize() {
        binding.fragmentSaveAttendanceClassesSpinner.onItemSelectedListener = this

        adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, spinnerList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.fragmentSaveAttendanceClassesSpinner.adapter = adapter

        for (i in 0 until list.size)
            spinnerList.add(i + 1, list[i].title)

        binding.fragmentSaveAttendanceSave.setOnClickListener {
            close(
                false,
                binding.fragmentSaveAttendanceClassesSpinner.selectedItemId
            )
        }
        binding.fragmentSaveAttendanceDontSave.setOnClickListener { close(false) }
        binding.fragmentSaveAttendanceCancel.setOnClickListener { close(true) }
    }

    private fun setUpAdapter(list: List<String>) {

        adapter.addAll(list)

        //setting default selected item for better UX
        if (spinnerList.size > 1)
            binding.fragmentSaveAttendanceClassesSpinner.setSelection(1)
        else {
            binding.fragmentSaveAttendanceClassesSpinner.setSelection(0)
            binding.fragmentSaveAttendanceClassName.visibility = View.VISIBLE
        }
    }

    private fun close(isCanceled: Boolean, selectedId: Long? = null) {
        val bundle = bundleOf(CentralVariables.KEY_CANCELLED to isCanceled)
        if (isCanceled || selectedId == null) {
            closeDialog(bundle)
            return
        }
        viewModel.saveIndex.observe(this) {
            if (it != -1L) {
                bundle.putLong(CentralVariables.KEY_MEETING_ID, it!!)
                closeDialog(bundle)
                this.dismiss()
            }
        }
        if (binding.fragmentSaveAttendanceClassName.text.toString().isNotEmpty()) {
            viewModel.setMeetIdToSaveAttendance(
                selectedId,
                binding.fragmentSaveAttendanceClassName.text.toString()
            )
        } else {
            binding.fragmentSaveAttendanceClassName.error = "Empty Class Name"
        }
    }

    private fun closeDialog(bundle: Bundle) {
        requireActivity().supportFragmentManager.setFragmentResult(
            CentralVariables.KEY_SAVE_ATTENDANCE_DIALOG_FRAGMENT_MESSAGE,
            bundle
        )
        this.dismiss()
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