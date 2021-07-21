package com.example.connect.Model;

public class ModelChat {
    private String message;
    private String sender;
    private String reciever;
    private String timeStamp;
    private String messagetype;
    private boolean isSeen;


    public ModelChat(){}

    public ModelChat(String message,String sender,String reciever,String timeStamp,String messgaetype,boolean isSeen){
        this.message=message;
        this.sender=sender;
        this.reciever=reciever;
        this.timeStamp=timeStamp;
        this.messagetype=messgaetype;
        this.isSeen=isSeen;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setReciever(String reciever) {
        this.reciever = reciever;
    }

    public void setMessagetype(String messagetype) {
        this.messagetype = messagetype;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getReciever() {
        return reciever;
    }

    public String getSender() {
        return sender;
    }

    public String getMessagetype() {
        return messagetype;
    }

    public String getMessage() {
        return message;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public boolean isSeen() {
        return isSeen;
    }

    public void setSeen(boolean seen) {
        isSeen = seen;
    }
}
