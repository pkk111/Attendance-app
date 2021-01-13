package com.pkk.andriod.attendence.AsyncTasks;

import android.content.Context;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.pkk.andriod.attendence.R;
import com.pkk.andriod.attendence.activity.StudentActivity;
import com.pkk.andriod.attendence.misc.MessageModel;
import com.pkk.andriod.attendence.misc.Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;

public class ClientAsyncSocket extends AsyncTask<Object, Long, Integer> {

    private Context context;
    private String SERVER_IP;
    private int SERVER_PORT = 9002;
    private String message;
    private Gson gson;

    public ClientAsyncSocket(Context context, String SERVER_IP, MessageModel model) {
        this.context = context;
        this.SERVER_IP = SERVER_IP;
        gson = new Gson();
        message = gson.toJson(model);
    }

    @Override
    protected Integer doInBackground(Object... objects) {
        try {
            Socket socket = new Socket();
            socket.bind(null);
            socket.setReuseAddress(true);
            socket.connect(new InetSocketAddress(SERVER_IP, SERVER_PORT), 5000);

            OutputStream outputStream = socket.getOutputStream();
            PrintWriter output = new PrintWriter(outputStream,true);

            output.println(message);
            output.flush();

            InputStream inputStream = socket.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader input = new BufferedReader(inputStreamReader);
            String reply = input.readLine();
            updateUI(reply);

            output.close();
            outputStream.close();
            socket.close();

        } catch (SocketException e) {
            e.printStackTrace();
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    private void updateUI(String message){
        MessageModel model = gson.fromJson(message, MessageModel.class);
        if (model.getError_code()==0){
            ((StudentActivity)context).status.setText("Present \nmarked");
            ((StudentActivity)context).status.setClickable(false);
            ((StudentActivity)context).status.setBackground(((StudentActivity)context).getResources().getDrawable(R.drawable.start_button_background));
        }
        else if (model.getError_code()==2)
            Utils.showShortToast(context, model.getMessage());
        else if (model.getError_code()==3)
            Utils.showShortToast(context, model.getMessage());
        else{
            String s = ((StudentActivity)context).mTextViewReplyFromServer.getText().toString();
            if (model.getError_code()==1)
                ((StudentActivity)context).mTextViewReplyFromServer.append(String.format("\n From Server : %s",model.getMessage()));
        }
    }
}
