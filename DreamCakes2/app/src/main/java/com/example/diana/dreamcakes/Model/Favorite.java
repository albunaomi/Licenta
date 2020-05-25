package com.example.diana.dreamcakes.Model;


public class Favorite {
    private String CakeId;
    private String Name;
    private String CakeImage;
    private String Price;

    public Favorite(String cakeId, String cakeName, String cakeImage, String price) {
        CakeId = cakeId;
        Name = cakeName;
        CakeImage = cakeImage;
        Price = price;
    }

    public Favorite() {
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
