package com.example.diana.dreamcakes.Model;


public class User {
    public User(String fullName, String email,  String phone) {
        FullName = fullName;
        Email = email;
        Phone = phone;
        IsStaff="false";
    }

    public User(String fullName, String email, String phone, String homeAddress) {
        FullName = fullName;
        Email = email;
        Phone = phone;
        HomeAddress = homeAddress;
        IsStaff="false";
    }

    private String FullName,Email,Phone,IsStaff,HomeAddress;

    public User(String fullName) {
        FullName = fullName;
    }
    public User() {

    }
    public User(User user){
        FullName=user.getFullName();
        Email=user.getEmail();
        Phone=user.getPhone();
        IsStaff=user.getIsStaff();
        HomeAddress=user.getHomeAddress();
    }

    public String getHomeAddress() {
        return HomeAddress;
    }

    public void setHomeAddress(String homeAddress) {
        HomeAddress = homeAddress;
    }

    public String getIsStaff() {
        return IsStaff;
    }

    public void setIsStaff(String isStaff) {
        IsStaff = isStaff;
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
