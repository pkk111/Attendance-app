package com.pkk.andriod.attendence.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.pkk.andriod.attendence.R;
import com.pkk.andriod.attendence.AsyncTasks.ServerAsyncSocket;
import com.pkk.andriod.attendence.WifiConnectivity.WifiRelatedBroadcastReciver;
import com.pkk.andriod.attendence.adapter.TeacherAdapter;
import com.pkk.andriod.attendence.misc.MessageExtractor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class TeacherActivity extends AppCompatActivity implements View.OnClickListener {

    final Handler handler = new Handler();
    TeacherActivity that = this;

    private FloatingActionButton refresh;
    private FloatingActionButton add;
    private Button buttonReceiving;
    public TextView textViewDataFromClient;
    private RecyclerView recyclerView;
    public TeacherAdapter madapter;
    private MessageExtractor me;
    private int start = 0;
    private boolean isRunning = false;
    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;
    private IntentFilter intentFilter;
    private WifiRelatedBroadcastReciver broadCastReciver;
    private ArrayList<WifiP2pDevice> peers = new ArrayList<>();
    private ServerAsyncSocket socket1;
    private ServerAsyncSocket socket2;

    private WifiP2pManager.PeerListListener peerListListener = new WifiP2pManager.PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList wifiP2pDeviceList) {
            ArrayList<WifiP2pDevice> refreshPeerList = new ArrayList<>(wifiP2pDeviceList.getDeviceList());
            if (!refreshPeerList.equals(peers)) {
                peers.clear();
                peers.addAll(refreshPeerList);
            }
            if (peers.size() == 0)
                Log.d("PeerListener", "No device found");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher);

        initalize();
        madapter = new TeacherAdapter(this);
        buttonReceiving.setOnClickListener(this);
        refresh.setOnClickListener(this);
        add.setOnClickListener(this);
    }

    private void setUpWifi() {
        manager = (WifiP2pManager) getSystemService(WIFI_P2P_SERVICE);
        channel = manager.initialize(this, getMainLooper(), null);
        broadCastReciver = new WifiRelatedBroadcastReciver(manager, channel, this, peerListListener);

        try {
            Method m = manager.getClass().getMethod("setDeviceName",
                    WifiP2pManager.Channel.class, String.class, WifiP2pManager.ActionListener.class);
            m.invoke(manager, channel, "Group 1", new WifiP2pManager.ActionListener() {
                public void onSuccess() {
                    //Code for Success in changing name
                }

                public void onFailure(int reason) {
                    //Code to be done while name change Fails
                }
            });
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        discoverPeersTillSuccess();

        manager.createGroup(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d("Creating group", "Success");
            }

            @Override
            public void onFailure(int i) {
                Log.d("Creating group", "Success");
            }
        });
    }

    public void toast(final String message) {
        Handler handler = new Handler(Looper.getMainLooper());

        handler.post(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void initalize() {
        buttonReceiving = findViewById(R.id.btn_receiving);
        refresh = findViewById(R.id.refresh);
        add = findViewById(R.id.add_rollno);
        textViewDataFromClient = findViewById(R.id.clientmess);
        recyclerView = findViewById(R.id.recycler_view);

        intentFilter = new IntentFilter();
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
    }

    private void setRecyclerView() {
        recyclerView.setAdapter(madapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        madapter.setAttendance(me.getAttendance(), me.getstatus());
        madapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_receiving:
                if (!isRunning) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);

                    LayoutInflater inflater = LayoutInflater.from(this);

                    final View entryview = inflater.inflate(R.layout.alert_label_editor, null);
                    final EditText startroll = entryview.findViewById(R.id.start);
                    final EditText endroll = entryview.findViewById(R.id.end);

                    builder.setView(entryview).setTitle("Enter RollNo Range ");

                    // Set up the buttons
                    builder.setPositiveButton("Start", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            int end = 0;
                            if (!startroll.getText().toString().isEmpty() && !endroll.getText().toString().isEmpty()) {
                                try {
                                    start = Integer.parseInt(startroll.getText().toString());
                                    end = Integer.parseInt(endroll.getText().toString());
                                } catch (Exception e) {
                                    toast("Enter rollno correctly");
                                }
                                if (start <= end) {
                                    me = new MessageExtractor(start, end);
                                    setRecyclerView();

                                    socket1 = new ServerAsyncSocket(TeacherActivity.this, me);
                                    socket2 = new ServerAsyncSocket(TeacherActivity.this, me);
                                    socket2.cancel(true);

                                    isRunning = true;
                                    refresh.setEnabled(true);
                                    buttonReceiving.setText("Stop");
                                    buttonReceiving.setBackgroundResource(R.drawable.stop_button_background);
                                    add.setVisibility(View.VISIBLE);
                                    refresh.setVisibility(View.VISIBLE);
                                    setUpWifi();
                                    registerReceiver(broadCastReciver, intentFilter);
                                } else
                                    toast("Enter the Starting and Ending RollNo correctly");
                            } else
                                toast("Enter both Starting and Ending RollNo");
                        }

                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();
                } else {
                    isRunning = false;
                    unregisterReceiver(broadCastReciver);
                    buttonReceiving.setText("Start");
                    buttonReceiving.setBackgroundResource(R.drawable.start_button_background);
                    socket1.cancel(true);
                    socket2.cancel(true);
                    manager.removeGroup(channel, new WifiP2pManager.ActionListener() {
                        @Override
                        public void onSuccess() {
                            Log.d("Removing group", "Success");
                        }

                        @Override
                        public void onFailure(int i) {
                            Log.d("Removing group", "Failed");
                        }
                    });
                    Log.d("StopAttendance", "Stopped all activity helping to take attendance");
                }
                break;
            case R.id.refresh:
                madapter.notifyDataSetChanged();
                break;
            case R.id.add_rollno:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                LayoutInflater inflater = LayoutInflater.from(this);

                final View entryview = inflater.inflate(R.layout.layout_alert_dialog_add_roll_no, null);
                final EditText rollEditText = entryview.findViewById(R.id.alert_dialog_add_roll_edittext);

                builder.setView(entryview).setTitle("Roll Number to be Added ");

                // Set up the buttons
                builder.setPositiveButton("Start", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int roll = Integer.parseInt(rollEditText.getText().toString());
                        me.addStud(TeacherActivity.this, roll);
                        madapter.notifyDataSetChanged();
                    }

                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
        }
    }

    private void discoverPeersTillSuccess() {
        manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
//                Toast.makeText(getApplicationContext(), "discSuccess", Toast.LENGTH_SHORT).show();
                System.out.println("discSuccess");
            }

            @Override
            public void onFailure(int i) {
//                System.out.println("discFailed " + i);
//                Toast.makeText(getApplicationContext(), "discFailed " + i, Toast.LENGTH_SHORT).show();
                discoverPeersTillSuccess();
            }
        });
    }

    public void startListening() {
        if (socket1.isCancelled()) {
            socket1 = new ServerAsyncSocket(TeacherActivity.this, me);
            socket1.execute();
            me = socket1.getUpdatedExtractor();
        } else if (socket2.isCancelled()) {
            socket2 = new ServerAsyncSocket(TeacherActivity.this, me);
            socket2.execute();
            me = socket2.getUpdatedExtractor();
        }
    }
}
