package com.pkk.andriod.attendence;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class attendenceadapter extends RecyclerView.Adapter<attendenceadapter.MyViewHolder> {

    public  int[] roll;
    private Boolean[] att;
    private static LayoutInflater layoutinflator;

    public attendenceadapter(Context context){
        layoutinflator=LayoutInflater.from(context);
    }

    public  void setattendence(int[] roll,Boolean[] att){
        this.roll=roll;
        this.att=att;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = layoutinflator.inflate(R.layout.attadencestatus, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        myViewHolder.background.setCardBackgroundColor(MessageExtractor.getcolor(att[i]));
        myViewHolder.rollno.setText("Roll No."+roll[i]);
        myViewHolder.status.setText(MessageExtractor.getattendence(att[i]));
    }

    @Override
    public int getItemCount() {
        return MessageExtractor.getstud();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView rollno;
        public CardView background;
        public TextView status;

        public MyViewHolder(@NonNull View v) {
            super(v);
            rollno = v.findViewById(R.id.rollno);
            background = v.findViewById(R.id.notification_background);
            status = v.findViewById(R.id.status);
        }
    }

}