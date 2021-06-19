package com.pkk.andriod.attendence.adapter

import android.content.Context
import android.net.wifi.p2p.WifiP2pDevice
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.pkk.andriod.attendence.R
import java.util.*

class PeerListViewAdapter(context: Context?, resource: Int, private val deviceList: ArrayList<WifiP2pDevice>) : ArrayAdapter<WifiP2pDevice?>(context, resource, deviceList as List<WifiP2pDevice?>?) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var v = convertView
        if (v == null) {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            v = inflater.inflate(R.layout.layout_peer_selector_item, parent)
        }
        val device = deviceList[position]
        val name = v!!.findViewById<TextView>(R.id.peer_selector_item_name)
        val ip = v.findViewById<TextView>(R.id.peer_selector_item_ip)
        name.text = device.deviceName
        ip.text = device.deviceAddress

        return v
    }
}