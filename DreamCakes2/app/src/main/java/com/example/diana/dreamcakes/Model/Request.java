package com.example.diana.dreamcakes.Model;


import java.util.List;

public class Request {
    private String phone;
    private String name;
    private String address;
    private String total;
   private List<CartItem> order;

    public Request() {
    }

    public Request(String phone, String name, String address, String total, List<CartItem> order) {
        this.phone = phone;
        this.name = name;
        this.address = address;
        this.total = total;
        this.order = order;
    }

    public String getPhone() {
        return phone;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public List<CartItem> getOrder() {
        return order;
    }

    public void setOrder(List<CartItem> order) {
        this.order = order;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
