package com.pkk.andriod.attendence;

public class MessageExtractor {

    private int noofstud;
    private String[] studip;
    private Boolean[] stud;
    private int rollno[];

    void MessageExtractor(int l){
        this.noofstud = l;
      stud = new Boolean[l];
      studip = new String[l];
      rollno = new int[l];
    }

    void update(int rollno,String ip,Boolean present){
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

    Boolean[] requestattendence(){
        return stud;
    }
}
