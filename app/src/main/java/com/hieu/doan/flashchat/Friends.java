package com.hieu.doan.flashchat;

import java.util.ArrayList;

public class Friends {
    ArrayList<String> listFriends;


    public ArrayList<String> getListFriends() {
        return listFriends;
    }

    public Friends(ArrayList<String> listFriends) {
        this.listFriends = listFriends;
    }

    public Friends() {
    }

    public void setListFriends(ArrayList<String> listFriends) {
        this.listFriends = listFriends;
    }
}
