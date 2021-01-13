package com.pkk.andriod.attendence.adapter;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.pkk.andriod.attendence.R;

import java.util.ArrayList;

public class PeerListViewAdapter extends ArrayAdapter<WifiP2pDevice> {

    private ArrayList<WifiP2pDevice> deviceList;

    public PeerListViewAdapter(Context context, int resource, ArrayList<WifiP2pDevice> wifiP2pDevices) {
        super(context, resource, wifiP2pDevices);
        deviceList = wifiP2pDevices;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = convertView;
        if(v==null){
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.layout_peer_selector_item, null);
        }

        WifiP2pDevice device = deviceList.get(position);
        if(device!=null){
            TextView name = v.findViewById(R.id.peer_selector_item_name);
            TextView ip = v.findViewById(R.id.peer_selector_item_ip);
            name.setText(device.deviceName);
            ip.setText(device.deviceAddress);
        }
        return v;
    }
}