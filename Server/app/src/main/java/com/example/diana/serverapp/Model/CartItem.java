package com.example.diana.serverapp.Model;


public class CartItem {
    private int ID;
    private String CakeId;
    private String CakeName;
    private String CakeImage;
    private String Price;
    private String Quantity;
    private String UserPhone;

    public CartItem() {
    }

    public CartItem(String userPhone, String cakeId, String cakeName, String cakeImage, String price, String quantity ) {
        CakeId = cakeId;
        CakeName = cakeName;
        CakeImage = cakeImage;
        Price = price;
        Quantity = quantity;
        UserPhone = userPhone;
    }

    public CartItem(int ID, String userPhone, String cakeId, String cakeName, String cakeImage, String price, String quantity) {
        this.ID = ID;
        CakeId = cakeId;
        CakeName = cakeName;
        CakeImage = cakeImage;
        Price = price;
        Quantity = quantity;
        UserPhone = userPhone;
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
        return CakeName;
    }

    public void setCakeName(String cakeName) {
        CakeName = cakeName;
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

    public String getQuantity() {
        return Quantity;
    }

    public void setQuantity(String quantity) {
        Quantity = quantity;
    }
}
