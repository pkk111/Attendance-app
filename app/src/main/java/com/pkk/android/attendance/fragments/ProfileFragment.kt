package com.pkk.android.attendance.fragments

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import com.pkk.android.attendance.R
import com.pkk.android.attendance.adapter.AvatarAdapter
import com.pkk.android.attendance.databinding.FragmentProfileBinding
import com.pkk.android.attendance.misc.CentralVariables
import com.pkk.android.attendance.misc.SharedPref
import com.pkk.android.attendance.misc.Utils

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private var avatarIndex: Int = -1
    private lateinit var username: String
    private lateinit var avatars: ArrayList<Int>
    private val onClickListener = View.OnClickListener { v ->
        updateAvatar(v.getTag(CentralVariables.KEY_POSITION) as Int)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        initialise()
        return binding.root
    }

    private fun initialise() {
        //get the data that needs to be displayed
        avatarIndex = SharedPref.getInt(requireContext(), CentralVariables.KEY_PROFILE_PIC, 0)
        username = SharedPref.getString(
            requireContext(),
            CentralVariables.KEY_USERNAME,
            Build.MANUFACTURER
        )!!
        avatars = Utils.getAvatars()
        binding.avatarImageView.setImageResource(avatars[avatarIndex])
        //display data
        binding.usernameEditText.setText(username)
        binding.avatarImageView.setImageResource(avatars[avatarIndex])
        binding.gridView.adapter = AvatarAdapter(requireContext(), avatars, onClickListener)
    }

    private fun updateAvatar(index: Int) {
        avatarIndex = index
        binding.avatarImageView.setImageResource(avatars[index])
    }

    private fun saveDetails() {
        val username = binding.usernameEditText.text.toString()
        SharedPref.setString(
            requireContext(),
            CentralVariables.KEY_USERNAME,
            username
        )
        SharedPref.setInt(requireContext(), CentralVariables.KEY_PROFILE_PIC, avatarIndex)

        val headerView =
            requireActivity().findViewById<NavigationView>(R.id.nav_view).getHeaderView(0)
        headerView.findViewById<ImageView>(R.id.nav_header_profile_pic)
            .setImageResource(Utils.getAvatars()[avatarIndex])
        headerView.findViewById<TextView>(R.id.nav_header_username).text = username
    }

    override fun onDestroyView() {
        saveDetails()
        super.onDestroyView()
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            FeedbackFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}