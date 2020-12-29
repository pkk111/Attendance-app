package com.pkk.andriod.attendence.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.pkk.andriod.attendence.R;
import com.pkk.andriod.attendence.misc.MessageExtractor;

import java.util.List;

public class TeacherAdapter extends RecyclerView.Adapter<TeacherAdapter.MyViewHolder>{


    private List<Integer> roll;
    private List<Boolean> att;
    private static LayoutInflater layoutinflator;

    public TeacherAdapter(Context context){
        layoutinflator=LayoutInflater.from(context);
    }

    public  void setattendence(List<Integer> roll, List<Boolean> att){
        this.roll=roll;
        this.att=att;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = layoutinflator.inflate(R.layout.attendencestatus, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, int i) {
        myViewHolder.aSwitch.setOnCheckedChangeListener(null);
        myViewHolder.background.setCardBackgroundColor(MessageExtractor.getcolor(att.get(i)));
        myViewHolder.rollno.setText(roll.get(i)+"");
        myViewHolder.status.setText(MessageExtractor.getattendence(att.get(i)));
        if(att.get(i))
            myViewHolder.aSwitch.setChecked(att.get(i));
        final int j = i;
        myViewHolder.aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    MessageExtractor.setStatus(j, !MessageExtractor.getstatus().get(j));
                    myViewHolder.background.setCardBackgroundColor(MessageExtractor.getcolor(MessageExtractor.getstatus().get(j)));
                    myViewHolder.status.setText(MessageExtractor.getattendence(MessageExtractor.getstatus().get(j)));

            }
        });
    }

    @Override
    public int getItemCount() {
        return roll.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView rollno;
        public CardView background;
        public TextView status;
        public Switch aSwitch;

        public MyViewHolder(@NonNull View v) {
            super(v);
            rollno = v.findViewById(R.id.rollno);
            background = v.findViewById(R.id.notification_background);
            status = v.findViewById(R.id.status);
            aSwitch = v.findViewById(R.id.status_switch);
        }
    }


}
