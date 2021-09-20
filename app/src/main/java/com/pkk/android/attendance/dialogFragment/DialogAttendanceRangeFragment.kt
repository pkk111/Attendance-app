package com.pkk.android.attendance.dialogFragment

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.pkk.android.attendance.databinding.FragmentDialogAttendanceRangeBinding
import com.pkk.android.attendance.misc.CentralVariables
import com.pkk.android.attendance.misc.Utils

class DialogAttendanceRangeFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentDialogAttendanceRangeBinding? = null
    private val binding get() = _binding!!
    private var start = 0
    private var end = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDialogAttendanceRangeBinding.inflate(LayoutInflater.from(context))

        //set dialog window
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)

        initialize()
        return binding.root
    }

    private fun initialize() {
        binding.cancel.setOnClickListener { closeDialog(true) }
        binding.submit.setOnClickListener {
            val startRoll = binding.start.text.toString()
            val endRoll = binding.end.text.toString()
            if (startRoll.isNotEmpty() && endRoll.isNotEmpty()) {
                try {
                    start = startRoll.toInt()
                    end = endRoll.toInt()
                } catch (e: Exception) {
                    toast("Enter RollNo correctly")
                }
                if (start <= end) {
                    closeDialog(false)
                } else toast("Enter the Starting and Ending RollNo correctly")
            } else toast("Enter both Starting and Ending RollNo")
        }
    }

    private fun closeDialog(isCanceled: Boolean) {
        requireActivity().supportFragmentManager.setFragmentResult(
            CentralVariables.KEY_START_ATTENDANCE_DIALOG_FRAGMENT_MESSAGE,
            bundleOf(
                CentralVariables.KEY_CANCELLED to isCanceled,
                CentralVariables.KEY_START to start,
                CentralVariables.KEY_END to end
            )
        )
        this.dismiss()
    }

    private fun toast(message: String?) {
        Utils.showShortToast(requireContext(), message)
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        )
    }

    companion object {
        const val TAG = "DialogRangeAttendanceFragment"
    }
}