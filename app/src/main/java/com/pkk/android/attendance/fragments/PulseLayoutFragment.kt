package com.pkk.android.attendance.fragments

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.cardview.widget.CardView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.pkk.android.attendance.R
import com.pkk.android.attendance.connectionSetup.Discoverer
import com.pkk.android.attendance.interfaces.ConnectionCallbackListener
import com.pkk.android.attendance.interfaces.ConnectionEstablishedListener
import com.pkk.android.attendance.interfaces.DeviceSelectedListener
import com.pkk.android.attendance.interfaces.PayloadCallbackListener
import com.pkk.android.attendance.misc.CentralVariables
import com.pkk.android.attendance.misc.SharedPref
import com.pkk.android.attendance.misc.Utils
import com.pkk.android.attendance.misc.Utils.Companion.toDp
import com.pkk.android.attendance.models.DeviceModel
import kotlinx.android.synthetic.main.fragment_pulse_layout.*

class PulseLayoutFragment : Fragment(), PayloadCallbackListener, ConnectionEstablishedListener,
    DeviceSelectedListener, ConnectionCallbackListener {

    private var discoverer: Discoverer? = null
    private var message: String? = null
    private var card: CardView? = null
    private var height: Int = 0
    private var width: Int = 0
    private var radius: Float = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            message = it.getString("message", "")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_pulse_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar.setNavigationOnClickListener { closeCurrentFragment() }

        setBackgroundCard()
        searchNearbyDevices()
        setName(SharedPref.getString(requireContext(), CentralVariables.KEY_HOST_NAME))
        setPic(SharedPref.getInt(requireContext(), CentralVariables.KEY_PROFILE_PIC))
        //Start Animation
        startPulse()
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
            pulse_layout_fragment.addView(card, 0)
            pulse_layout_fragment.setBackgroundColor(
                Utils.getColorFromResource(
                    requireContext(),
                    R.color.transparent
                )
            )
        } else {
            pulse_layout_fragment.setBackgroundColor(
                Utils.getColorFromResource(
                    requireContext(),
                    R.color.pulselayout_backgroundcolor
                )
            )
        }
    }

    private fun searchNearbyDevices() {
        pulsator!!.start()
        pulsator!!.setColor(0)
        pulsator!!.setAvatars(Utils.getAvatars())
        pulsator!!.setListener(this)
        discoverer = Discoverer(requireActivity(), this, this)
        discoverer!!.startDiscovering(this)
    }

    private fun closeCurrentFragment() {
        requireActivity().supportFragmentManager.popBackStack()
    }

    override fun onDeviceDetected(device: DeviceModel) {
        pulsator!!.addDetectedDevice(device)
    }

    override fun onDeviceLost(endpoint: String) {
        pulsator!!.removeDetectedDevice(endpoint)
    }

    override fun onDeviceSelected(device: DeviceModel) {
        discoverer!!.requestConnection(device)
    }

    override fun onConnectionEstablished() {
        discoverer!!.sendMessage(message!!)
    }

    override fun onPayloadReceived(message: String?, endpointId: String?) {
        requireActivity().supportFragmentManager.setFragmentResult(
            CentralVariables.KEY_FRAGMENT_MESSAGE_KEY,
            bundleOf("message" to message)
        )
        closeCurrentFragment()
    }

    override fun onDestroyView() {
        if (discoverer != null) {
            discoverer!!.stopDiscovering()
        }
        super.onDestroyView()
    }

    private fun startPulse() {
        pulsator!!.post {
            pulsator!!.setListener(this)
            pulsator!!.start()
        }
    }

    private fun setName(displayName: String?) {
        requireActivity().runOnUiThread { name.text = displayName }
    }


    private fun setPic(displayPic: Int) {
        if (displayPic >= 0)
            requireActivity().runOnUiThread {
                pic.background =
                    Utils.getDrawableFromResource(requireContext(), Utils.getAvatars()[displayPic])
            }
    }

    companion object {
        @JvmStatic
        fun newInstance(message: String?) =
            PulseLayoutFragment().apply {
                arguments = Bundle().apply {
                    putString("message", message)
                }
            }
    }


}