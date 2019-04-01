package com.pkk.andriod.attendence;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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

    private Button buttonReceiving;
    private Button refresh;
    private TextView textViewDataFromClient;

    private RecyclerView recyclerView;
    private attendenceadapter madapter;
    private boolean check=true;
    private boolean checker=false;
    private boolean stop=true;
    private MessageExtractor me;
    private String ip="";
    private int x=0;
    private int start=0;
    static String output="";
    private Thread thread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initalize();
        madapter = new attendenceadapter(this);

        buttonReceiving.setOnClickListener(this);
        refresh.setOnClickListener(this);
        refresh.setEnabled(false);
    }

    private void startServerSocket() {

         thread = new Thread(new Runnable() {

            private String stringData = null;

            @Override
            public void run() {

                try {
                    if(stop){
                    ServerSocket ss = new ServerSocket(9002);
                        //Server is waiting for client here, if needed
                        Socket s = ss.accept();
                        BufferedReader input = new BufferedReader(new InputStreamReader(s.getInputStream()));
                        PrintWriter output = new PrintWriter(s.getOutputStream());

                        stringData = input.readLine();
                        String message=updateUI(stringData);
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (stringData.equalsIgnoreCase("STOP")) {
                            output.close();
                            s.close();
                            ss.close();
                            buttonReceiving.callOnClick();
                        }
                        output.println(message);
                        output.flush();
                        output.close();
                        s.close();
                    ss.close();}
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
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void initalize(){
        buttonReceiving = findViewById(R.id.btn_receiving);
        refresh = findViewById(R.id.Refresh);
        textViewDataFromClient = findViewById(R.id.clientmess);
        recyclerView = findViewById(R.id.recycler_view);
    }

    private void setRecyclerView(){
        recyclerView.setAdapter(madapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        madapter.setattendence(me.getattendence(),me.getstatus());
        madapter.notifyDataSetChanged();
    }

    private String updateUI(final String stringdata) {

        handler.post(new Runnable() {
            @Override
            public void run() {
                int l=stringdata.length();
                 checker=true;
                if(l>15){
                    if(ipcheck(stringdata))
                    {try {
                        ip=stringdata.substring(0,15);
                        InetAddress ipadd=InetAddress.getByName(ip);
                        ip=ipadd.toString();
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                        toast("Error name: "+e);
                    }
                    if((rollno(stringdata)-start)<=me.getstud()){
                        checker=false;
                        me.update(rollno(stringdata),ip,status(stringdata));
                        setRecyclerView();
                        if(me.getstatus()[rollno(stringdata)-start]){
                            output="true";
                        }
                    }
                    }
                }
                if(checker){
                    String s = textViewDataFromClient.getText().toString();
                    if (stringdata.trim().length() != 0)
                        textViewDataFromClient.setText(s + "\n" + "From Client : " + stringdata);
                    output="FROM SERVER - " + stringdata.toUpperCase();
                    checker=false;}
            }
        });
        if(!checker) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return output;
    }

    boolean ipcheck(String s){
        Character c1=s.charAt(3);
        Character c2=s.charAt(7);
        Character c3=s.charAt(11);
        if(c1.equals('.') && c2.equals('.') && c3.equals('.'))
            return true;
        return false;
    }

    boolean status(String s){

        if(s.substring(x+1).equals("true"))
            return true;
        return false;
    }

    int rollno(String s){
        x=16;
        while(x<s.length()){
            char c=s.charAt(x);
            if(c==' ')
                break;
            x++;
        }
        return Integer.parseInt(s.substring(16,x));
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_receiving:
                if(check){
                AlertDialog.Builder builder =new AlertDialog.Builder(this);

                    LayoutInflater inflater=LayoutInflater.from(this);

                    final View entryview = inflater.inflate(R.layout.alert_label_editor,null);
                    final EditText startroll= entryview.findViewById(R.id.start);
                    final EditText endroll = entryview.findViewById(R.id.end);

                    builder.setView(entryview).setTitle("Enter RollNo Range! ");

                // Set up the buttons
                builder.setPositiveButton("Start", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(!startroll.getText().toString().isEmpty() && !endroll.getText().toString().isEmpty())
                        {start=Integer.parseInt(startroll.getText().toString());
                        int end = Integer.parseInt(endroll.getText().toString());
                        if(start<=end){
                            me=new MessageExtractor(start,end);
                            stop=true;
                            setRecyclerView();
                            startServerSocket();
                            check=false;
                            refresh.setEnabled(true);
                            buttonReceiving.setText("Stop Reciving Data");
                        }
                        else
                            toast("Enter the Starting and Ending RollNo correctly");
                    }
                    else
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
                break;
                }
                else{
                check=true;
                stop=false;
                buttonReceiving.setText("Start Reciving data");
                break;}
            case R.id.Refresh:
                madapter.notifyDataSetChanged();
                break;
        }
    }
}
