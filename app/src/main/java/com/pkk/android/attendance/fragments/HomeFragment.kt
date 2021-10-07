package com.pkk.android.attendance.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.pkk.android.attendance.R
import com.pkk.android.attendance.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        initialize()
        return binding.root
    }

    private fun initialize() {
        //Make both teacher and student button circular
        binding.studentLogin.setOnClickListener { studentLogin() }
        binding.teacherLogin.setOnClickListener { teacherLogin() }
        binding.homeMenu.setOnClickListener { (requireActivity().findViewById(R.id.drawerLayout) as DrawerLayout).open() }
    }

    private fun studentLogin() {
        NavHostFragment.findNavController(this)
            .navigate(R.id.action_homeFragment_to_studentFragment)
    }

    private fun teacherLogin() {
        NavHostFragment.findNavController(this)
            .navigate(R.id.action_homeFragment_to_teacherFragment)
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar?.hide()
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity).supportActionBar?.show()
    }

}