package com.pkk.android.attendance.fragments

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.pkk.android.attendance.R
import com.pkk.android.attendance.connectionSetup.Discoverer
import com.pkk.android.attendance.databinding.FragmentPulseLayoutBinding
import com.pkk.android.attendance.interfaces.ConnectionCallbackListener
import com.pkk.android.attendance.interfaces.ConnectionEstablishedListener
import com.pkk.android.attendance.interfaces.DeviceSelectedListener
import com.pkk.android.attendance.interfaces.PayloadCallbackListener
import com.pkk.android.attendance.misc.CentralVariables
import com.pkk.android.attendance.misc.SharedPref
import com.pkk.android.attendance.misc.Utils
import com.pkk.android.attendance.misc.Utils.Companion.toDp
import com.pkk.android.attendance.models.DeviceModel
import com.pkk.android.attendance.viewModels.PulseLayoutViewModel
import com.pkk.android.attendance.viewModels.ViewModelFactory

class PulseLayoutFragment : Fragment(), PayloadCallbackListener, ConnectionEstablishedListener,
    DeviceSelectedListener, ConnectionCallbackListener {

    private var _binding: FragmentPulseLayoutBinding? = null
    private val binding get() = _binding!!
    private var discoverer: Discoverer? = null
    private var message: String? = null
    private var card: CardView? = null
    private var height: Int = 0
    private var width: Int = 0
    private var radius: Float = 0f
    private lateinit var viewModelFactory: ViewModelFactory
    private lateinit var viewModel: PulseLayoutViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            message = it.getString(CentralVariables.KEY_MESSAGE, "")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPulseLayoutBinding.inflate(inflater, container, false)
        viewModelFactory = ViewModelFactory()
        viewModel = ViewModelProvider(this, viewModelFactory).get(PulseLayoutViewModel::class.java)

        binding.toolbar.setNavigationOnClickListener { closeCurrentFragment() }
        setBackgroundCard()
        if (!viewModel.isRunning)
            searchNearbyDevices()
        setName(SharedPref.getString(requireContext(), CentralVariables.KEY_USERNAME))
        val profilePic = SharedPref.getInt(requireContext(), CentralVariables.KEY_PROFILE_PIC)
        if (profilePic != -1) setPic(profilePic)
        //Start Animation
        startPulse()
        return binding.root
    }

    private fun setBackgroundCard() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            height = resources.displayMetrics.heightPixels
            width = resources.displayMetrics.widthPixels
            radius = width.coerceAtMost(height) * 0.5f

            val cardHeight = (height / 2 + radius * 2.75).toInt()
            val cardWidth = (width + radius).toInt()

            card = CardView(requireContext())
            card!!.layoutParams = LinearLayout.LayoutParams(cardWidth, cardHeight)
            val margins = card!!.layoutParams as ViewGroup.MarginLayoutParams
            margins.topMargin = (-1.5 * radius).toInt()
            margins.leftMargin = (-1 * radius).toInt()
            card!!.requestLayout()
            card!!.layoutParams.height = cardHeight
            card!!.layoutParams.width = cardWidth
            card!!.elevation = 0f

            height = resources.configuration.screenHeightDp
            width = resources.configuration.screenWidthDp
            radius = width.coerceAtMost(height) * 0.5f

            card!!.radius = (radius * 1.5f).toInt().toDp(requireContext())
            card!!.setCardBackgroundColor(Color.BLUE)
            binding.pulseLayoutFragment.addView(card, 0)
            binding.pulseLayoutFragment.setBackgroundColor(
                Utils.getColorFromResource(
                    requireContext(),
                    R.color.transparent
                )
            )
        } else {
            binding.pulseLayoutFragment.setBackgroundColor(
                Utils.getColorFromResource(
                    requireContext(),
                    R.color.pulselayout_backgroundcolor
                )
            )
        }
    }

    private fun searchNearbyDevices() {
        discoverer = Discoverer(requireActivity(), CentralVariables.STAR_STRATEGY, this, this)
        discoverer!!.startDiscovering(this)
//        viewModel.setRunning(true)
    }

    private fun closeCurrentFragment() {
        requireActivity().onBackPressed()
    }

    override fun onDeviceDetected(device: DeviceModel) {
        binding.pulsator.addDetectedDevice(device)
    }

    override fun onDeviceLost(endpoint: String) {
        binding.pulsator.removeDetectedDevice(endpoint)
    }

    override fun onDeviceSelected(device: DeviceModel) {
        discoverer!!.requestConnection(device)
    }

    override fun onConnectionEstablished() {
        discoverer!!.sendMessage(message!!)
    }

    override fun onPayloadReceived(message: String, endpointId: String) {
        requireActivity().supportFragmentManager.setFragmentResult(
            CentralVariables.KEY_PULSE_FRAGMENT_MESSAGE_KEY,
            bundleOf(
                CentralVariables.KEY_MESSAGE to message
            )
        )
        closeCurrentFragment()
    }

    override fun onDestroyView() {
//        viewModel.setRunning(false)
        if (discoverer != null) {
            discoverer!!.stopDiscovering()
        }
        super.onDestroyView()
    }

    private fun startPulse() {
        binding.pulsator.setColor(0)
        binding.pulsator.setAvatars(Utils.getAvatars())
        binding.pulsator.setListener(this)
        binding.pulsator.post {
            binding.pulsator.start()
        }
    }

    private fun setName(displayName: String?) {
        requireActivity().runOnUiThread { binding.name.text = displayName }
    }

    private fun setPic(displayPic: Int) {
        if (displayPic >= 0)
            requireActivity().runOnUiThread {
                binding.pic.setImageResource(Utils.getAvatars()[displayPic])
            }
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