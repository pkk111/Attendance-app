package com.pkk.android.attendance.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.pkk.android.attendance.R
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initialize()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun initialize() {
        //Make both teacher and student button circular
        studentLogin.setOnClickListener { studentLogin() }
        teacherLogin.setOnClickListener { teacherLogin() }
    }

    private fun studentLogin() {
        val ft = requireActivity().supportFragmentManager.beginTransaction()
        ft.setCustomAnimations(
            R.anim.slide_from_left_to_right,
            R.anim.slide_from_right_to_left,
            R.anim.slide_from_left_to_right,
            R.anim.slide_from_right_to_left
        )
        ft.addToBackStack("home").add(R.id.blank_layout, StudentFragment.newInstance()).commit()
    }

    private fun teacherLogin() {
        val ft = requireActivity().supportFragmentManager.beginTransaction()
        ft.setCustomAnimations(
            R.anim.slide_from_left_to_right,
            R.anim.slide_from_right_to_left,
            R.anim.slide_from_left_to_right,
            R.anim.slide_from_right_to_left
        )
        ft.addToBackStack("home").add(R.id.blank_layout, TeacherFragment.newInstance()).commit()
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            HomeFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}