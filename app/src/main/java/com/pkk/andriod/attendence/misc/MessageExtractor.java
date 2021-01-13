package com.pkk.andriod.attendence.misc;

import android.content.Context;
import android.graphics.Color;

import com.pkk.andriod.attendence.activity.TeacherActivity;

import java.util.ArrayList;
import java.util.List;

public class MessageExtractor {


    private static int noofstud;
    private static int start=0;
    private static int end=0;
    private static List<String> studip;
    private static List<Boolean> stud;
    public static List<Integer> roll;

    public MessageExtractor(int start, int end) {
        this.noofstud = end - start + 1;
        this.start = start;
        this.end = end;
        roll = new ArrayList<>();
        studip = new ArrayList<>();
        stud = new ArrayList<>();
        for (int x = 0; x < noofstud; x++) {
            stud.add(false);
            roll.add(this.start+x);
            studip.add("");
        }
    }

    public static boolean checkIP(String ip) {
        if (!studip.isEmpty())
            for (int x = 0; x < noofstud; x++) {
                if (!studip.get(x).isEmpty() && studip.get(x).equals(ip)) {
                    return false;
                }
            }
        return true;
    }

    public static boolean update(int rollno, String ip, Boolean present) {
        if (!(rollno >= start && rollno <= end)) {
            for (int i = end - start+1; i < noofstud; i++)
                if (roll.get(i) == rollno) {
                    studip.set(i, ip);
                    stud.set(i, present);
                    return true;
                }
        }
        else if(rollno >= start && rollno <= end){
            studip.set(rollno-start, ip);
            stud.set(rollno-start, present);
            return true;
        }
        return false;
    }

    public static List<Integer> getattendence() {
        return roll;
    }

    public static int getstud() {
        return noofstud;
    }

    public static List<Boolean> getstatus() {
        return stud;
    }

    public static int getcolor(Boolean bool) {
        if (bool)
            return Color.rgb(90, 205, 60);
        return Color.rgb(220, 20, 60);
    }

    public static String getattendence(Boolean bool) {
        if (bool)
            return "Present";
        return "Absent";
    }

    public void addStud(Context context, int rollno) {
        if (!(rollno >= start && rollno <= end)) {
            for (int i = end - start; i < noofstud; i++)
                if (roll.get(i) == rollno) {
                    Utils.showShortToast(context, rollno+" already present for evaluation.");
                    return;
                }
        }
        else if(rollno >= start && rollno <= end){
            Utils.showShortToast(context, rollno+" already present for evaluation.");
            return;
        }
        stud.add(false);
        roll.add(rollno);
        noofstud++;
    }

    static public void setStatus(int i, Boolean s){
        stud.set(i, s);
    }

    public int getStart() {
        return start;
    }
}
