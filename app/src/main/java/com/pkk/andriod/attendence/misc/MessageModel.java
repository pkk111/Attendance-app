package com.pkk.andriod.attendence.misc;

public class MessageModel {

    private String ip;
    private int roll_no;
    private boolean present;
    private String error_msg;
    private int error_code;
    private String Message;

    public MessageModel(int error_code, String message) {
        this.error_code = error_code;
        Message = message;
    }

    public MessageModel() {
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getRoll_no() {
        return roll_no;
    }

    public void setRoll_no(int roll_no) {
        this.roll_no = roll_no;
    }

    public boolean isPresent() {
        return present;
    }

    public void setPresent(boolean present) {
        this.present = present;
    }

    public String getError_msg() {
        return error_msg;
    }

    public void setError_msg(String error_msg) {
        this.error_msg = error_msg;
    }

    public int getError_code() {
        return error_code;
    }

    public void setError_code(int error_code) {
        this.error_code = error_code;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public MessageModel(String ip, int roll_no, boolean present) {
        this.ip = ip;
        this.roll_no = roll_no;
        this.present = present;
    }
}
