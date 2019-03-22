package com.pkk.andriod.attendence;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    final Handler handler = new Handler();

    private Button buttonStartReceiving;
    private Button buttonStopReceiving;
    private TextView textViewDataFromClient;
    private boolean end = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonStartReceiving = findViewById(R.id.btn_start_receiving);
        buttonStopReceiving = findViewById(R.id.btn_stop_receiving);
        textViewDataFromClient = findViewById(R.id.clientmess);

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

    private void updateUI(final String stringData) {

        handler.post(new Runnable() {
            @Override
            public void run() {

                String s = textViewDataFromClient.getText().toString();
                if (stringData.trim().length() != 0)
                    textViewDataFromClient.setText(s + "\n" + "From Client : " + stringData);
            }
        });
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_start_receiving:

                startServerSocket();
                buttonStartReceiving.setEnabled(false);
                buttonStopReceiving.setEnabled(true);
                end=true;
                break;

            case R.id.btn_stop_receiving:

                //stopping server socket logic you can add yourself
                buttonStartReceiving.setEnabled(true);
                buttonStopReceiving.setEnabled(false);
                end=false;
                break;
        }
    }
}
