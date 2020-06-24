package com.example.diana.serverapp.Model;


public class User {


    private String FullName, Email, Phone, IsStaff;


    public User() {

    }

    public User(String name, String email, String phone, String isStaff) {
        FullName = name;
        Email = email;
        Phone = phone;
        IsStaff = isStaff;
    }

    public User(User user){
        FullName=user.getFullName();
        Email=user.getEmail();
        Phone=user.getPhone();
        IsStaff=user.getIsStaff();
    }
    public String getFullName() {
        return FullName;
    }

    public void setFullName(String fullName) {
        FullName = fullName;
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

    public String getIsStaff() {
        return IsStaff;
    }

    public void setIsStaff(String isStaff) {
        IsStaff = isStaff;
    }
}
