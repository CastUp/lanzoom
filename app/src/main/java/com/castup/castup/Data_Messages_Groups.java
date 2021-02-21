package com.castup.castup;

public class Data_Messages_Groups {

    private String  time , edit  , message , messageid , sender , receiver, date , type , namegroup ;

    public Data_Messages_Groups() {

    }

    public Data_Messages_Groups(String time, String edit, String message, String messageid, String sender, String receiver, String date, String type, String namegroup) {
        this.time = time;
        this.edit = edit;
        this.message = message;
        this.messageid = messageid;
        this.sender = sender;
        this.receiver = receiver;
        this.date = date;
        this.type = type;
        this.namegroup = namegroup;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getEdit() {
        return edit;
    }

    public void setEdit(String edit) {
        this.edit = edit;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessageid() {
        return messageid;
    }

    public void setMessageid(String messageid) {
        this.messageid = messageid;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNamegroup() {
        return namegroup;
    }

    public void setNamegroup(String namegroup) {
        this.namegroup = namegroup;
    }
}
