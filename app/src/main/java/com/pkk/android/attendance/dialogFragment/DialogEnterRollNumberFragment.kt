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
import com.pkk.android.attendance.databinding.FragmentDialogEnterRollNumberBinding
import com.pkk.android.attendance.misc.CentralVariables
import com.pkk.android.attendance.misc.Utils

class DialogEnterRollNumberFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentDialogEnterRollNumberBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDialogEnterRollNumberBinding.inflate(LayoutInflater.from(context))

        //set dialog window
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)

        initialize()
        return binding.root
    }

    private fun initialize() {
        binding.fragmentDialogEnterRollNumberOk.setOnClickListener {
            val roll = binding.dialogFragmentAddRollEdittext.text.toString()
            if (roll.isEmpty())
                Utils.showShortToast(
                    requireContext(),
                    "Please Enter Roll Number"
                )
            else
                closeDialog(false, roll.toInt())
        }
        binding.fragmentDialogEnterRollNumberCancel.setOnClickListener { closeDialog(true, 0) }
    }

    private fun closeDialog(isCanceled: Boolean, roll: Int) {
        if (!isCanceled) {
            requireActivity().supportFragmentManager.setFragmentResult(
                CentralVariables.KEY_ENTER_ROLL_NUMBER_DIALOG_FRAGMENT_MESSAGE,
                bundleOf(
                    CentralVariables.KEY_CANCELLED to isCanceled,
                    CentralVariables.Key_ROLL_NO to roll
                )
            )
        }
        this.dismiss()
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        )
    }

    companion object {
        const val TAG = "DialogFragmentRollNumber"
    }
}