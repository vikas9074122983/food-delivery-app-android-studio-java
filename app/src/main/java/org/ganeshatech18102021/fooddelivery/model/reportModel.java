package org.ganeshatech18102021.fooddelivery.model;

public class reportModel {
    private String date,paymode,time,price;

    public reportModel() {
    }

    public reportModel(String date, String paymode, String time, String price) {
        this.date = date;
        this.paymode = paymode;
        this.time = time;
        this.price = price;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPaymode() {
        return paymode;
    }

    public void setPaymode(String paymode) {
        this.paymode = paymode;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
