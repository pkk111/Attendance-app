package com.pkk.andriod.attendence.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.widget.TextView;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.pkk.andriod.attendence.R;
import com.pkk.andriod.attendence.AsyncTasks.ClientAsyncSocket;
import com.pkk.andriod.attendence.fragment.PeerSelectorFragment;
import com.pkk.andriod.attendence.misc.MessageModel;


public class StudentActivity extends AppCompatActivity implements View.OnClickListener, PeerSelectorFragment.PeerFragmentToStudentActivity {

    public TextView mTextViewReplyFromServer;
    private EditText mEditTextSendMessage;
    public Button status;
    private String rollNo;
    private Boolean check = true;
    private String serverIP;
    private CardView coverCard;
    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);

        coverCard = findViewById(R.id.student_activity);
        Intent intent = getIntent();
        if (intent != null)
            serverIP = intent.getStringExtra("ServerIP");

        if (manager == null && serverIP == null) {
            manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
            channel = manager.initialize(this, Looper.getMainLooper(), null);
        } else if (serverIP == null) {
            manager.requestConnectionInfo(channel, new WifiP2pManager.ConnectionInfoListener() {
                @Override
                public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
                    serverIP = wifiP2pInfo.groupOwnerAddress.getHostAddress();
                }
            });
        }
        if (serverIP == null || serverIP.isEmpty()) {
            coverCard.setVisibility(View.VISIBLE);
            PeerSelectorFragment fragment = new PeerSelectorFragment();
            fragment.setManager(manager, channel);
            getSupportFragmentManager().beginTransaction().replace(R.id.student_activity, fragment).addToBackStack(null).commit();
        } else {
            coverCard.setVisibility(View.GONE);
            Log.d("Serverip: ", serverIP);
            mEditTextSendMessage = findViewById(R.id.edt_send_message);
            mTextViewReplyFromServer = findViewById(R.id.tv_reply_from_server);
            Button buttonSend = findViewById(R.id.btn_send);
            status = findViewById(R.id.status);

            status.setOnClickListener(this);
            buttonSend.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_send:
                MessageModel m = new MessageModel(1, mEditTextSendMessage.getText().toString());
                new ClientAsyncSocket(StudentActivity.this, serverIP, m).execute();
                break;

            case R.id.status:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Enter Your Roll No");
                // Set up the input
                final EditText input = new EditText(this);
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                builder.setView(input);
                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        rollNo = input.getText().toString();
                        if (!rollNo.isEmpty()) {
                            if (check) {
                                MessageModel model = new MessageModel();
                                model.setRoll_no(Integer.parseInt(rollNo));
                                model.setPresent(true);
                                new ClientAsyncSocket(StudentActivity.this, serverIP, model).execute();
                            }
                        } else
                            toast("Enter Roll No to mark your attendence");

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

    public void toast(final String message) {
        Handler handler = new Handler(Looper.getMainLooper());

        handler.post(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void sendObjects(WifiP2pManager manager, WifiP2pManager.Channel channel) {
        this.manager = manager;
        this.channel = channel;
    }
}
