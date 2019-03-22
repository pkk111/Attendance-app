package com.pkk.andriod.attendence;

public class MessageExtractor {

    private static int noofstud;
    private static String[] studip;
    private static Boolean[] stud;
    private static int rollno[];

      MessageExtractor(int l){
        this.noofstud = l;
        stud = new Boolean[l];
        studip = new String[l];
        rollno = new int[l];
    }

    static void update(int rollno,String ip,Boolean present){
        Boolean check=false;
        for(int x=0;x<noofstud;x++){
            if(studip!=null)
                if(studip[x].equals(ip)){
                    check=true;
                    break;
                }
        }
        if(!check){
            studip[rollno]=ip;
            stud[rollno]=present;
        }
    }

    static int[] getattendence(){
        return rollno;
    }

    static int getstud(){return noofstud;}

    static Boolean[] getstatus(){
        return stud;
    }

    static int getcolor(Boolean bool){
        if(bool)
            return 4;
        return 5;
    }

    static String getattendence(Boolean bool){
        if(bool)
            return "Present";
        return "Absent";
    }

}
