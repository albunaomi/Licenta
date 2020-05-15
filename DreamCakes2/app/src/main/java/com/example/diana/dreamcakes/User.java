package com.example.diana.dreamcakes;

/**
 * Created by Diana on 5/9/2020.
 */

public class User {
    public User(String fullName, String email,  String phone) {
        FullName = fullName;
        Email = email;
        Phone = phone;
    }

    private String FullName,Email,Phone;

    public User(String fullName) {
        FullName = fullName;
    }
    public User() {

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
