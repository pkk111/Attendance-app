package com.pkk.andriod.attendence;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    final Handler handler = new Handler();

    private Button buttonStartReceiving;
    private Button buttonStopReceiving;
    private TextView textViewDataFromClient;

    private RecyclerView recyclerView;
    private attendenceadapter madapter;
    private TextView rollno;
    private CardView cardview;
    private boolean end = false;
    private boolean check=false;
    private boolean check2=true;
    private boolean check3=true;
    private MessageExtractor me;
    private String ip="";
    private String noofstud="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initalize();
        madapter = new attendenceadapter(this);

        buttonStartReceiving.setOnClickListener(this);
        buttonStopReceiving.setOnClickListener(this);
    }

    private void startServerSocket() {

        Thread thread = new Thread(new Runnable() {

            private String stringData = null;

            @Override
            public void run() {

                try {
                    if(!end){

                    ServerSocket ss = new ServerSocket(9002);

                    while (end) {
                        //Server is waiting for client here, if needed
                        Socket s = ss.accept();
                        BufferedReader input = new BufferedReader(new InputStreamReader(s.getInputStream()));
                        PrintWriter output = new PrintWriter(s.getOutputStream());

                        stringData = input.readLine();
                        output.println("FROM SERVER - " + stringData.toUpperCase());
                        output.flush();

                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        updateUI(stringData);
                        if (stringData.equalsIgnoreCase("STOP")) {
                            end = true;
                            output.close();
                            s.close();
                            break;
                        }
                        output.close();
                        s.close();
                    }
                    ss.close();
                }
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

    private void initalize(){
        buttonStartReceiving = findViewById(R.id.btn_start_receiving);
        buttonStopReceiving = findViewById(R.id.btn_stop_receiving);
        textViewDataFromClient = findViewById(R.id.clientmess);
        recyclerView = findViewById(R.id.recycler_view);
        rollno = findViewById(R.id.rollno);
        cardview = findViewById(R.id.notification_background);
    }

    private void setRecyclerView(){
        recyclerView.setAdapter(madapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        madapter.setattendence(me.getattendence(),me.getstatus());
        madapter.notifyDataSetChanged();
    }

    private void updateUI(final String stringdata) {

        handler.post(new Runnable() {
            @Override
            public void run() {
                int l=stringdata.length();
                if(l>15){
                    if(ipcheck(stringdata))
                    try {
                        ip=stringdata.substring(0,15);
                        InetAddress ipadd=InetAddress.getByName(ip);
                        ip=ipadd.toString();
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    }
                    me.update(rollno(stringdata),ip,status(stringdata));

                }
                String s = textViewDataFromClient.getText().toString();
                if (stringdata.trim().length() != 0)
                    textViewDataFromClient.setText(s + "\n" + "From Client : " + stringdata);
            }
        });
    }

    boolean ipcheck(String s){
        Character c1=s.charAt(3);
        Character c2=s.charAt(6);
        Character c3=s.charAt(9);
        if(c1.equals('.') && c2.equals('.') && c3.equals('.'))
            return true;
        return false;
    }

    boolean status(String s){
        int n1=((int)Math.log(me.getstud()))+1+16+1;
        if(s.substring(n1).equals("true"))
            return true;
        return false;
    }

    int rollno(String s){
        return Integer.parseInt(s.substring(16));
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_start_receiving:
                noofstud="";
                check=false;
                AlertDialog.Builder builder =new AlertDialog.Builder(this);
                builder.setTitle("Total no of Students?");

                // Set up the input
                final EditText input = new EditText(this);
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(check3){
                        noofstud=input.getText().toString().trim();
                        if(!noofstud.isEmpty()){
                            check=true;
                            check2=true;
                            try{int noroll=Integer.parseInt(noofstud);}
                            catch (Exception e){
                                toast("Enter number of student correctly");
                                check2=false;
                            }}
                        if(check && check2){
                            me=new MessageExtractor(Integer.parseInt(noofstud));
                            startServerSocket();
                            setRecyclerView();
                            buttonStartReceiving.setEnabled(false);
                            buttonStopReceiving.setEnabled(true);
                            end=true;
                            check=false;
                            check3=false;}
                    }}
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
                break;

            case R.id.btn_stop_receiving:

                //stopping server socket logic you can add yourself
                buttonStartReceiving.setEnabled(true);
                buttonStopReceiving.setEnabled(false);
                end=false;
                break;
            case R.id.Refresh:
                setRecyclerView();
                break;
        }
    }
}
