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

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getFullName() {
        return FullName;
    }

    public void setFullName(String fullName) {
        FullName = fullName;
    }
}
