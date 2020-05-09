package com.example.diana.dreamcakes;

/**
 * Created by Diana on 5/9/2020.
 */

public class User {
    public User(String fullName, String username, String email,  String phone) {
        FullName = fullName;
        Username = username;
        Email = email;
        Phone = phone;
    }

    private String FullName,Username,Email,Phone;

    public User(String fullName) {
        FullName = fullName;
    }
    public User() {

    }

    public String getFullName() {
        return FullName;
    }

    public void setFullName(String fullName) {
        FullName = fullName;
    }
}
