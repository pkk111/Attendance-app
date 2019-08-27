package com.pkk.andriod.attendence.misc;

import android.content.Context;
import android.graphics.Color;

import com.pkk.andriod.attendence.activity.TeacherActivity;

public class MessageExtractor {


    private static int noofstud;
    private static int start;
    private static String[] studip;
    private static Boolean[] stud;
    public static int roll[];

    public MessageExtractor(int start, int end){
        this.noofstud = end-start+1;
        this.start=start;
        stud = new Boolean[noofstud];
        studip = new String[noofstud];
        roll = new int[noofstud];
        for(int x=0;x<noofstud;x++){
            stud[x] = false;
            roll[x] = start+x;
            studip[x]=Integer.toString(x);
        }
    }

    public static void update(Context context, int rollno, String ip, Boolean present){
        Boolean check=true;
        rollno-=start;
        for(int x=0;x<noofstud;x++){
            if(studip!=null)
                if(studip[x].equals(ip)){
                    Utils.showShortToast(context,"You cannot mark more than one attendence");
                    check=false;
                    break;
                }
        }
        if(check){
            studip[rollno]=ip;
            stud[rollno]=present;
        }
    }

    public static int[] getattendence(){
        return roll;
    }

    public static int getstud(){return noofstud;}

    public static Boolean[] getstatus(){
        return stud;
    }

    public static int getcolor(Boolean bool){
        if(bool)
            return Color.rgb(50,205,50);
        return Color.rgb(220,20,60);
    }

    public static String getattendence(Boolean bool){
        if(bool)
            return "Present";
        return "Absent";
    }

}
