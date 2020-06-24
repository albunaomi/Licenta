package com.example.diana.dreamcakes.Model;


import java.util.List;

public class Request {
    private String phone;
    private String name;
    private String status;
    private String address;
    private String total;
    private String paymentMethod;
    private String date;
    private String paymentState;
   private List<CartItem> order;

    public Request() {
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }


    public Request(String phone, String name, String address, String total, String paymentMethod, String date, String paymentState, List<CartItem> order) {
        this.phone = phone;
        this.name = name;
        this.status = "0";
        this.address = address;
        this.total = total;
        this.paymentMethod = paymentMethod;
        this.date = date;
        this.paymentState = paymentState;
        this.order = order;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentState() {
        return paymentState;
    }

    public void setPaymentState(String paymentState) {
        this.paymentState = paymentState;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
