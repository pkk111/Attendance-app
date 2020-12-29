package com.pkk.andriod.attendence.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.pkk.andriod.attendence.R;
import com.pkk.andriod.attendence.adapter.TeacherAdapter;
import com.pkk.andriod.attendence.misc.MessageExtractor;
import com.pkk.andriod.attendence.misc.MessageModel;
import com.pkk.andriod.attendence.misc.Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class TeacherActivity extends AppCompatActivity implements View.OnClickListener {

    final Handler handler = new Handler();

    private FloatingActionButton refresh;
    private FloatingActionButton add;
    private Button buttonReceiving;
    private TextView textViewDataFromClient;
    private RecyclerView recyclerView;
    private TeacherAdapter madapter;
    private boolean check = true;
    private boolean checker = false;
    private boolean stop = true;
    private MessageExtractor me;
    private String ip = "";
    private int x = 0;
    private int start = 0;
    private Thread thread;

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

    private void startServerSocket() {

        thread = new Thread(new Runnable() {

            private String stringData = null;

            @Override
            public void run() {

                try {
                    if (stop) {
                        ServerSocket ss = new ServerSocket(9002);
                        //Server is waiting for client here, if needed
                        Socket s = ss.accept();
                        BufferedReader input = new BufferedReader(new InputStreamReader(s.getInputStream()));
                        PrintWriter output = new PrintWriter(s.getOutputStream());
                        stringData = input.readLine();
                        Log.e("reply",""+stringData);
                        Gson g = new Gson();
                        MessageModel model = g.fromJson(stringData, MessageModel.class);
                        MessageModel m = updateUI(model);
                        String message = g.toJson(m);
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
    }

    private void setRecyclerView() {
        recyclerView.setAdapter(madapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        madapter.setattendence(me.getattendence(), me.getstatus());
        madapter.notifyDataSetChanged();
    }

    private MessageModel updateUI(final MessageModel m) {
        MessageModel output = new MessageModel();

        final MessageModel finalOutput = output;
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (m.getError_code() == 1) {
                    textViewDataFromClient.setText(textViewDataFromClient.getText().toString() + "From Client: " + m.getMessage() + "\n");
                    checker = false;
                } else if (m.getError_code() == 0) {
                    checker = true;
                        ip = m.getIp();
                    if (!me.checkIP(m.getIp())) {
                        finalOutput.setError_code(2);
                        finalOutput.setMessage("You cannot mark more than one attendance in this session");
                    } else if (!me.update(m.getRoll_no(), m.getIp(), m.isPresent())) {
                        finalOutput.setError_code(3);
                        finalOutput.setError_msg("Enter a valid roll number");
                    } else {
                        madapter.notifyDataSetChanged();
                        if (me.getstatus().get(m.getRoll_no() - start)) {
                            finalOutput.setError_code(0);
                            finalOutput.setPresent(true);
                        }
                    }


                }
            }
        });
        if (m.isPresent()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return finalOutput;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_receiving:
                if (check) {
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
                                    stop = true;
                                    setRecyclerView();
                                    startServerSocket();
                                    check = false;
                                    refresh.setEnabled(true);
                                    buttonReceiving.setText("Stop");
                                    buttonReceiving.setBackgroundResource(R.drawable.stop_button_background);
                                    add.setVisibility(View.VISIBLE);
                                    refresh.setVisibility(View.VISIBLE);
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
                    break;
                } else {
                    check = true;
                    stop = false;
                    buttonReceiving.setText("Start");
                    buttonReceiving.setBackgroundResource(R.drawable.start_button_background);
                    break;
                }
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
}
