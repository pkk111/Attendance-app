package com.pkk.andriod.attendence.WifiConnectivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.pkk.andriod.attendence.R;
import com.pkk.andriod.attendence.activity.StudentActivity;
import com.pkk.andriod.attendence.activity.TeacherActivity;
import com.pkk.andriod.attendence.adapter.PeerListViewAdapter;
import com.pkk.andriod.attendence.fragment.PeerSelectorFragment;
import com.pkk.andriod.attendence.misc.Utils;

import java.util.ArrayList;

public class WifiRelatedBroadcastReciver extends BroadcastReceiver {

    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;
    private AppCompatActivity activity;
    private WifiP2pManager.PeerListListener listListener;

    public WifiRelatedBroadcastReciver(WifiP2pManager manager, WifiP2pManager.Channel channel, AppCompatActivity activity, WifiP2pManager.PeerListListener listListener) {
        super();
        this.manager = manager;
        this.channel = channel;
        this.activity = activity;
        this.listListener = listListener;
    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        final String action = intent.getAction();
        if(action.equals(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)){
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if(state==WifiP2pManager.WIFI_P2P_STATE_DISABLED){
                WifiManager wifiManager = (WifiManager) activity.getApplicationContext().getSystemService(activity.getApplicationContext().WIFI_SERVICE);
                wifiManager.setWifiEnabled(true);
            }
        }
        else if(action.equals(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)){
            if(manager!=null){
                manager.requestPeers(channel, listListener);
            }
        }
        else if(action.equals(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)){
            if (manager == null)
                return;
            NetworkInfo networkInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
            if (networkInfo.isConnected()) {
                //Connected to other device, requesting the connection info now
                manager.requestConnectionInfo(channel, new WifiP2pManager.ConnectionInfoListener() {
                    @Override
                    public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
                        String groupOwnerAddress = wifiP2pInfo.groupOwnerAddress.getHostAddress();

                        if (wifiP2pInfo.groupFormed && wifiP2pInfo.isGroupOwner) {
                            //Group owner code
                            Log.d("Connection formed", "The device is the group owner");
                        }
                        else if (wifiP2pInfo.groupFormed) {
                            // Client Side Code goes here
                            Intent intent = new Intent(context, StudentActivity.class);
                            Intent otherAppActivity = activity.getIntent();
                            if (otherAppActivity.getAction() != null) {
                                if (otherAppActivity.getAction().equals(Intent.ACTION_SEND)) {
                                    //intent.putExtra();
                                }
                            }
                            intent.putExtra("ServerIP", wifiP2pInfo.groupOwnerAddress.getHostAddress());
                            Utils.showShortToast(context, "GroupOwnerAddress: " + wifiP2pInfo.groupOwnerAddress.toString());
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            context.startActivity(intent);
                            activity.finish();
                        }
                    }
                });
            }
        }
        else if(action.equals(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)) {
            System.out.println("\nwifi p2p this device changed action\n");
        }
    }
}
