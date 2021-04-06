package com.hieu.doan.flashchat.Models;

public class Message {
    private String msgID, msg, senderID;
    private long  timestamp;
    private int felling;

    public Message(){

    }

    public Message(String msg, String senderID, long timestamp) {
        this.msg = msg;
        this.senderID = senderID;
        this.timestamp = timestamp;
    }

    public String getMsgID() {
        return msgID;
    }

    public void setMsgID(String msgID) {
        this.msgID = msgID;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getFelling() {
        return felling;
    }

    public void setFelling(int felling) {
        this.felling = felling;
    }
}
