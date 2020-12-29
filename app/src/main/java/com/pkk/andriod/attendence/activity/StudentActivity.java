package com.pkk.andriod.attendence.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.format.Formatter;
import android.widget.TextView;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.pkk.andriod.attendence.R;
import com.pkk.andriod.attendence.misc.MessageModel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;


public class StudentActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView mTextViewReplyFromServer;
    private EditText mEditTextSendMessage;
    private Button status;
    private WifiManager wm;
    private String rollno;
    private Boolean check=true;
    private Boolean checksend=true;
    private MessageModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);

        mEditTextSendMessage = findViewById(R.id.edt_send_message);
        mTextViewReplyFromServer = findViewById(R.id.tv_reply_from_server);
        Button buttonSend = findViewById(R.id.btn_send);
        status = findViewById(R.id.status);

        status.setOnClickListener(this);
        buttonSend.setOnClickListener(this);
    }
   @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_send:
                MessageModel m = new MessageModel(1, mEditTextSendMessage.getText().toString());
                sendMessage(m);
                checksend=true;
                break;

            case R.id.status:
                wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
                checksend=false;
                if(wm.isWifiEnabled()) {
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
                        rollno = input.getText().toString();
                        if(!rollno.isEmpty())
                        {
                            if(check){

                                    String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
                                    ip = iphandeler(ip);
                                    sendMessage(new MessageModel(ip, Integer.parseInt(rollno), true));
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
                    else
                    toast("Please turn on wifi and try again.");
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

    private void sendMessage(MessageModel message) {

        final Gson g = new Gson();
        final String msg = g.toJson(message);

        final Handler handler = new Handler();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    //Replace below IP with the IP of that device in which server socket open.
                    //If you change port then change the port number in the server side code also.

                    toast("Serching for server");

                    InetAddress addr = InetAddress.getByName("192.168.43.15");
                    Socket s = new Socket(addr, 9002);

                    OutputStream out = s.getOutputStream();

                    PrintWriter output = new PrintWriter(out,true);

                    output.println(msg);
                    output.flush();

                    toast("Message is send");
                    BufferedReader input = new BufferedReader(new InputStreamReader(s.getInputStream()));
                    final String reply = input.readLine();
                    final MessageModel m = g.fromJson(reply, MessageModel.class);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (m.getError_code()==0){
                                status.setText("Present \nmarked");
                                status.setEnabled(false);
                                status.setBackground(getResources().getDrawable(R.drawable.start_button_background));
                            }
                            else if (m.getError_code()==2)
                                toast(m.getMessage());
                            else if (m.getError_code()==3)
                                toast(m.getMessage());
                            else{
                                String s = mTextViewReplyFromServer.getText().toString();
                                if (m.getError_code()==1)
                                    mTextViewReplyFromServer.setText(s + "\n From Server : " + m.getMessage());
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
