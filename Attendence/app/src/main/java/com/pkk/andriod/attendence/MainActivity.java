package com.pkk.andriod.attendence;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.format.Formatter;
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
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView mTextViewReplyFromServer;
    private EditText mEditTextSendMessage;
    private Button status;
    private WifiManager wm;
    private String rollno;
    private Boolean check=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button buttonSend =findViewById(R.id.btn_send);

        mEditTextSendMessage = findViewById(R.id.edt_send_message);
        mTextViewReplyFromServer = findViewById(R.id.tv_reply_from_server);
        status = findViewById(R.id.status);
        status.setBackgroundColor(Color.rgb(220,20,60));

        buttonSend.setOnClickListener(this);
        status.setOnClickListener(this);
    }
   @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.btn_send:
                sendMessage(mEditTextSendMessage.getText().toString());
                break;
            case R.id.status:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Enter Your Roll No");

                // Set up the input
                final EditText input = new EditText(this);
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        rollno = input.getText().toString();

                        if(!rollno.isEmpty())
                        {try{int roll=Integer.parseInt(rollno);}
                         catch (Exception e)
                         {check=false;
                         toast("Enter Roll No correctly");}
                            if(check){
                                WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
                                String message = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
                                message=iphandeler(message);
                                message+=" "+rollno+" true";
                                sendMessage(message);

                            }
                        }
                        else
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

    String iphandeler(String ip){
        int l=ip.length();
        if(l<15){
            while(l>0) {
                l--;
                if (ip.charAt(3) != '.')
                    ip = "0" + ip;
                else if(ip.charAt(7)!='.')
                    ip=ip.substring(0,4)+"0"+ip.substring(4);
                else if(ip.charAt(11)!='.')
                    ip=ip.substring(0,8)+"0"+ip.substring(8);
                else if(ip.length()!=15)
                    ip=ip.substring(0,11)+"0"+ip.substring(11);
                else
                    break;
            }
        }
        return ip;
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
                    InetAddress addr = InetAddress.getByName("192.168.43.1");
                    Socket s = new Socket(addr, 9002);

                    OutputStream out = s.getOutputStream();

                    PrintWriter output = new PrintWriter(out,true);

                    output.println(msg);
                    output.flush();

                    toast("Message is send");
                    BufferedReader input = new BufferedReader(new InputStreamReader(s.getInputStream()));
                    final String st = input.readLine();

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if(st.equals("true")){
                                status.setText("Present has been marked");
                                status.setEnabled(false);
                                status.setBackgroundColor(Color.rgb(50,205,50));
                            }
                            else if(st.equals("You cannot mark more than one attendence"))
                                toast("You cannot mark more than one attendence");
                            else{
                                String s = mTextViewReplyFromServer.getText().toString();
                                if (st.trim().length() != 0)
                                    mTextViewReplyFromServer.setText(s + "\n From Server : " + st);
                            }
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
