package com.bissam.kanban;

public class UserModel {
    public String uid;
    public String name;
    public String email;

    public UserModel() {}

    public UserModel(String uid, String name, String email) {
        this.uid = uid;
        this.name = name;
        this.email = email;
    }
}