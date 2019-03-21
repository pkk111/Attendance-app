package com.pkk.andriod.attendence;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.Socket;



public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView mTextViewReplyFromServer;
    private EditText mEditTextSendMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button buttonSend =findViewById(R.id.btn_send);

        mEditTextSendMessage = findViewById(R.id.edt_send_message);
        mTextViewReplyFromServer = findViewById(R.id.tv_reply_from_server);

        buttonSend.setOnClickListener(this);
    }
   @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.btn_send:
                sendMessage(mEditTextSendMessage.getText().toString());
                break;
        }
    }

    private void sendMessage(final String msg) {

        final Handler handler = new Handler();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    //Replace below IP with the IP of that device in which server socket open.
                    //If you change port then change the port number in the server side code also.

                    toast("Device Pairing starting");
                    Socket s = new Socket(Inet4Address.getLocalHost().getHostAddress(), 9002);
                    toast("Socket is success");

                    OutputStream out = s.getOutputStream();
                    toast("OutputStream is success");

                    PrintWriter output = new PrintWriter(out);
                    toast("PrintWriter is success");

                    output.println(msg);
                    output.flush();

                    toast("Message is send");
                    //BufferedReader input = new BufferedReader(new InputStreamReader(s.getInputStream()));
                    //final String st = input.readLine();

                    handler.post(new Runnable() {
                        @Override
                        public void run() {

                            String s = mTextViewReplyFromServer.getText().toString();
                            //if (st.trim().length() != 0)
                                mTextViewReplyFromServer.setText(s + "\n From Server : "/* + st*/);

                        }
                    });

                    output.close();
                    out.close();
                    s.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
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
}
