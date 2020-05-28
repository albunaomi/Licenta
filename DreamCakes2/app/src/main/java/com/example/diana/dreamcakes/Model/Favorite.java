package com.example.diana.dreamcakes.Model;


public class Favorite {
    private int ID;
    private String UserPhone;
    private String CakeId;
    private String Name;
    private String CakeImage;
    private String Price;


    public Favorite(int ID, String userPhone, String cakeId, String name, String cakeImage, String price) {
        this.ID = ID;
        UserPhone = userPhone;
        CakeId = cakeId;
        Name = name;
        CakeImage = cakeImage;
        Price = price;
    }

    public Favorite(String userPhone, String cakeId, String name, String cakeImage, String price) {
        CakeId = cakeId;
        Name = name;
        CakeImage = cakeImage;
        Price = price;
        UserPhone = userPhone;
    }

    public Favorite() {
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getUserPhone() {
        return UserPhone;
    }

    public void setUserPhone(String userPhone) {
        UserPhone = userPhone;
    }

    public String getCakeId() {
        return CakeId;
    }

    public void setCakeId(String cakeId) {
        CakeId = cakeId;
    }

    public String getCakeName() {
        return Name;
    }

    public void setCakeName(String cakeName) {
        Name = cakeName;
    }

    public String getCakeImage() {
        return CakeImage;
    }

    public void setCakeImage(String cakeImage) {
        CakeImage = cakeImage;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }
}
