package com.pkk.andriod.attendence;

import android.content.res.ColorStateList;
import android.graphics.Color;

public class MessageExtractor {

    private static int noofstud;
    private static String[] studip;
    private static Boolean[] stud;
    public static int roll[];

      MessageExtractor(int l){
        this.noofstud = l;
        stud = new Boolean[l];
        studip = new String[l];
        roll = new int[l];
        for(int x=0;x<l;x++){
            stud[x] = false;
            roll[x] = x+1;
            studip[x]=Integer.toString(x);
        }
    }

    static void update(int rollno,String ip,Boolean present){
        Boolean check=true;
        for(int x=0;x<noofstud;x++){
            if(studip!=null)
                if(studip[x].equals(ip)){
                    check=false;
                    break;
                }
        }
        if(check){
            studip[rollno]=ip;
            stud[rollno]=present;
        }
    }

    static int[] getattendence(){
        return roll;
    }

    static int getstud(){return noofstud;}

    static Boolean[] getstatus(){
        return stud;
    }

    static int getcolor(Boolean bool){
        if(bool)
            return Color.rgb(50,205,50);
        return Color.rgb(220,20,60);
    }

    static String getattendence(Boolean bool){
        if(bool)
            return "Present";
        return "Absent";
    }

}
