package com.example.chatbot;

public class MsgModel {
    private String cnt;

    public MsgModel(String cnt) {
        this.cnt = cnt;
    }
    public MsgModel()
    {

    }

    public String getCnt() {
        return cnt;
    }

    public void setCnt(String cnt) {
        this.cnt = cnt;
    }
}
