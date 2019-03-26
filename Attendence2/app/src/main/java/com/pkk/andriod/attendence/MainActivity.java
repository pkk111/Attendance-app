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

    private Button buttonReceiving;
    private Button refresh;
    private TextView textViewDataFromClient;

    private RecyclerView recyclerView;
    private attendenceadapter madapter;
    private boolean end = true;
    private boolean check=false;
    private boolean check2=true;
    private boolean check3=true;
    private boolean check4=true;
    private boolean checker=false;
    private MessageExtractor me;
    private String ip="";
    private int x=0;
    static String output="";
    private String noofstud="";
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

                    ServerSocket ss = new ServerSocket(9002);
                    while (end) {
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
                            end = true;
                            output.close();
                            s.close();
                            check3=true;
                            check4=true;
                            thread.stop();
                            buttonReceiving.setText("Start Reciving data");
                            break;
                        }
                        output.println(message);
                        output.flush();
                        output.close();
                        s.close();
                    }
                    ss.close();
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
                    if(rollno(stringdata)<=me.getstud()){
                        checker=false;
                        me.update(rollno(stringdata),ip,status(stringdata));
                        setRecyclerView();
                        if(me.getstatus()[rollno(stringdata)-1]){
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
                if(check4){
                check4=false;
                noofstud="";
                check=false;
                AlertDialog.Builder builder =new AlertDialog.Builder(this);
                builder.setTitle("Total no of Students?");

                // Set up the input
                final EditText input = new EditText(this);
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_NUMBER);
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
                                toast("Enter number of student correctly");
                                check2=false;
                            }
                        if(check && check2){
                            me=new MessageExtractor(Integer.parseInt(noofstud));
                            startServerSocket();
                            buttonReceiving.setText("STOP Reciving data");
                            refresh.setEnabled(true);}
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
                }
                else{
                check3=true;
                check4=true;
                thread.stop();
                buttonReceiving.setText("Start Reciving data");
                break;}
            case R.id.Refresh:
                madapter.notifyDataSetChanged();
                break;
        }
    }
}
