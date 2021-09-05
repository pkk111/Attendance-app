package com.pkk.android.attendance.fragments

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.pkk.android.attendance.R
import com.pkk.android.attendance.connectionSetup.Discoverer
import com.pkk.android.attendance.interfaces.*
import com.pkk.android.attendance.misc.CentralVariables
import com.pkk.android.attendance.misc.SharedPref
import com.pkk.android.attendance.misc.Utils
import com.pkk.android.attendance.models.DeviceModel
import kotlinx.android.synthetic.main.fragment_pulse_layout.*

class PulseLayoutFragment : Fragment(), PayloadCallbackListener, ConnectionEstablishedListener,
    DeviceSelectedListener, ConnectionCallbackListener {

    private var discoverer: Discoverer? = null
    private var message: String? = null
    private var passDataListener: PassDataListener? = null
    private var card: CardView? = null
    private var height: Int = 0
    private var width: Int = 0
    private var radius: Float = 0f

    override fun onAttach(context: Context) {
        passDataListener = context as PassDataListener
        super.onAttach(context)
    }

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

        toolbar.setNavigationOnClickListener { requireActivity().supportFragmentManager.popBackStack() }

        setBackgroundCard()
        searchNearbyDevices()
        setName(SharedPref.getString(requireContext(), CentralVariables.KEY_HOST_NAME))
        //Start Animation
        startPulse()
    }

    private fun setBackgroundCard() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            height = resources.displayMetrics.heightPixels
            width = resources.displayMetrics.widthPixels
            radius = width.coerceAtMost(height) * 0.5f

            val cardHeight = (height / 2 + radius * 3).toInt()
            val cardWidth = (width + radius).toInt()

            height = resources.configuration.screenHeightDp
            width = resources.configuration.screenWidthDp


            card = CardView(requireContext())
            card!!.layoutParams = LinearLayout.LayoutParams(cardWidth, cardHeight)
            val margins = card!!.layoutParams as ViewGroup.MarginLayoutParams
            margins.topMargin = (-1.5 * radius).toInt()
            margins.leftMargin = (-1 * radius).toInt()
            card!!.requestLayout()
            card!!.layoutParams.height = cardHeight
            card!!.layoutParams.width = cardWidth
            card!!.elevation = 0f

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
        pulsator!!.setAvatars(getAvatars())
        pulsator!!.setListener(this)
        discoverer = Discoverer(requireActivity(), this, this)
        discoverer!!.startDiscovering(this)
    }

    private fun getAvatars(): ArrayList<Int> {
        return ArrayList(
            listOf(
                R.drawable.icon1,
                R.drawable.icon2,
                R.drawable.icon3,
                R.drawable.icon4,
                R.drawable.icon5,
                R.drawable.icon6,
                R.drawable.icon7,
                R.drawable.icon8,
                R.drawable.icon9,
                R.drawable.icon10,
                R.drawable.icon11,
                R.drawable.icon12
            )
        )
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
        if (passDataListener != null) {
            passDataListener!!.passData(message)
            requireActivity().supportFragmentManager.popBackStack()
        }
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

    fun Int.toDp(context: Context): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            this.toFloat(),
            context.resources.displayMetrics
        )
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