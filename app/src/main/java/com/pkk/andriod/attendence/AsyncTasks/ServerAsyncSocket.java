package com.pkk.andriod.attendence.AsyncTasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.pkk.andriod.attendence.activity.TeacherActivity;
import com.pkk.andriod.attendence.misc.MessageExtractor;
import com.pkk.andriod.attendence.misc.MessageModel;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerAsyncSocket extends AsyncTask<Object, Long, Integer> {

    private Context context;
    private int SERVER_PORT = 9002;
    private Gson gson;
    private String message = "";
    private MessageModel inputModel;
    private MessageExtractor extractor;

    public ServerAsyncSocket(Context context, MessageExtractor messageExtractor) {

        /**
         * Call getUpdatedMessageExtractor after executing
         */

        this.context = context;
        this.extractor = messageExtractor;
        gson = new Gson();
    }

    @Override
    protected Integer doInBackground(Object... objects) {
        try {
            ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
            serverSocket.setReuseAddress(true);
            Socket socket = serverSocket.accept();

            //Starting another ServerSocket asynchronously to listen to other requests
            ((TeacherActivity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((TeacherActivity) context).startListening();
                }
            });

            //Handling the current request
            if (!this.isCancelled()) {
                InputStream stream = socket.getInputStream();
                InputStreamReader streamReader = new InputStreamReader(stream);
                BufferedReader reader = new BufferedReader(streamReader);
                //Getting the input and converting it from json to the model format
                String input = reader.readLine();
                try {
                    inputModel = gson.fromJson(input, MessageModel.class);
                } catch (Exception e) {
                    Log.e("ERROR", "Error converting input json to the model format");
                    this.cancel(true);
                }
            }
            //Json to Model conversion was successful, we can do further validation and can send appropriate reply
            MessageModel reply = updateUI(inputModel);
            message = gson.toJson(reply);

            if (!this.isCancelled()) {
                OutputStream outputStream = socket.getOutputStream();
                PrintWriter writer = new PrintWriter(outputStream, true);

                writer.println(message);
                writer.flush();
                writer.close();
                outputStream.close();
                socket.close();
                serverSocket.close();
            }

            //Closing the async task now
            this.cancel(true);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            Log.e("ServerSocket", "Error Occured " + e);
        }
        return null;
    }

    private MessageModel updateUI(final MessageModel m) {
        MessageModel output = new MessageModel();
        if (m.getError_code() == 1) {
            ((TeacherActivity) context).textViewDataFromClient.append(String.format("\nFrom Client: %s", m.getMessage()));
        } else if (m.getError_code() == 0) {
            String ip = m.getIp();
            if (!extractor.checkIP(ip)) {
                output.setError_code(2);
                output.setMessage("You cannot mark more than one attendance in this session");
            } else if (!extractor.update(m.getRoll_no(), ip, m.isPresent())) {
                output.setError_code(3);
                output.setError_msg("Enter a valid roll number");
            } else {
                if (extractor.getstatus().get(m.getRoll_no() - extractor.getStart())) {
                    output.setError_code(0);
                    output.setPresent(true);
                }
            }
        }
        ((TeacherActivity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((TeacherActivity) context).madapter.notifyDataSetChanged();
            }
        });
        return output;
    }

    public MessageExtractor getUpdatedExtractor() {
        return extractor;
    }
}
