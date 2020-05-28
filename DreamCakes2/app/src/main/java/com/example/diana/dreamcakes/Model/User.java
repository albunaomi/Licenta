package com.example.diana.dreamcakes.Model;


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
    public User(User user){
        FullName=user.getFullName();
        Email=user.getEmail();
        Phone=user.getPhone();
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
