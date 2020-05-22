package com.example.diana.dreamcakes.Model;


public class CartItem {
    private String CakeId;
    private String CakeName;
    private String CakeImage;
    private String Price;
    private String Quantity;

    public CartItem() {
    }

    public CartItem(String cakeId, String cakeName, String cakeImage, String price, String quantity) {
        CakeId = cakeId;
        CakeName = cakeName;
        CakeImage = cakeImage;
        Price = price;
        Quantity = quantity;
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
