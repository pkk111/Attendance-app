package com.pkk.andriod.attendence.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.pkk.andriod.attendence.R;
import com.pkk.andriod.attendence.WifiConnectivity.WifiRelatedBroadcastReciver;
import com.pkk.andriod.attendence.activity.LoginActivity;
import com.pkk.andriod.attendence.adapter.PeerListViewAdapter;

import java.util.ArrayList;
import java.util.List;


public class PeerSelectorFragment extends Fragment {

    public ListView listView;
    private Button cancel;
    public PeerListViewAdapter adapter;
    private List list;
    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;
    private IntentFilter intentFilter;
    private WifiRelatedBroadcastReciver broadCastReciver;
    private ArrayList<WifiP2pDevice> peers = new ArrayList<>();
    private PeerFragmentToStudentActivity mCallback;

    private WifiP2pManager.PeerListListener peerListListener = new WifiP2pManager.PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList wifiP2pDeviceList) {
            ArrayList<WifiP2pDevice> refreshPeerList = new ArrayList<>(wifiP2pDeviceList.getDeviceList());
            if (!refreshPeerList.equals(peers)) {
                peers.clear();
                peers.addAll(refreshPeerList);
                adapter.notifyDataSetChanged();
            }
            if (peers.size() == 0)
                Log.d("PeerListener", "No device found");
        }
    };


    public PeerSelectorFragment() {
        // Required empty public constructor
    }


    public static PeerSelectorFragment newInstance(Context context) {
        PeerSelectorFragment fragment = new PeerSelectorFragment();
        Bundle args = new Bundle();
//        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //get the passed arguments
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_peer_selector, container, false);
        inialize(v);
        getPeerList();
        return v;
    }

    private void getPeerList() {
        broadCastReciver = new WifiRelatedBroadcastReciver(manager, channel, (AppCompatActivity) (this.getContext()), peerListListener);

        intentFilter = new IntentFilter();
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        discoverPeers();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                WifiP2pConfig config = new WifiP2pConfig();
                config.deviceAddress = adapter.getItem(i).deviceAddress;
                config.wps.setup = WpsInfo.PBC;

                manager.connect(channel, config, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        Log.d("connection tries", "Connected");
                        mCallback.sendObjects(manager, channel);
                    }

                    @Override
                    public void onFailure(int i) {
                        Log.d("connection tries", "Not Connected");
                    }
                });
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                getActivity().finish();
            }
        });
    }

    private void discoverPeers() {
        if (manager != null)
            manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                    Log.d("dsicovering peers", "Success");
                }

                @Override
                public void onFailure(int i) {
                    Log.d("dsicovering peers", "Failure");
                }
            });
    }


    private void inialize(View v) {
        cancel = v.findViewById(R.id.cancle_peer_selector_fragment);
        listView = v.findViewById(R.id.peer_list_view);
        adapter = new PeerListViewAdapter(getContext().getApplicationContext(), R.layout.layout_peer_selector_item, peers);
        listView.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        getContext().registerReceiver(broadCastReciver, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        getContext().unregisterReceiver(broadCastReciver);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            mCallback = (PeerFragmentToStudentActivity) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement PeerFragmentToStudentActivity");
        }
    }

    public void setManager(WifiP2pManager manager, WifiP2pManager.Channel channel) {
        this.manager = manager;
        this.channel = channel;
    }

    public interface PeerFragmentToStudentActivity {
        void sendObjects(WifiP2pManager manager, WifiP2pManager.Channel cha);
    }
}