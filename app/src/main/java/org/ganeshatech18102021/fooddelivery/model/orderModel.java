package org.ganeshatech18102021.fooddelivery.model;

public class orderModel {
    private String address,date,id,itemdetail,itemtotal,mail,name,orderID,payID,paymode,phone,restid,status,time;

    public orderModel() {
    }

    public orderModel(String address, String date, String id, String itemdetail, String itemtotal, String mail, String name, String orderID, String payID, String paymode, String phone, String restid, String status, String time) {
        this.address = address;
        this.date = date;
        this.id = id;
        this.itemdetail = itemdetail;
        this.itemtotal = itemtotal;
        this.mail = mail;
        this.name = name;
        this.orderID = orderID;
        this.payID = payID;
        this.paymode = paymode;
        this.phone = phone;
        this.restid = restid;
        this.status = status;
        this.time = time;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getItemdetail() {
        return itemdetail;
    }

    public void setItemdetail(String itemdetail) {
        this.itemdetail = itemdetail;
    }

    public String getItemtotal() {
        return itemtotal;
    }

    public void setItemtotal(String itemtotal) {
        this.itemtotal = itemtotal;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public String getPayID() {
        return payID;
    }

    public void setPayID(String payID) {
        this.payID = payID;
    }

    public String getPaymode() {
        return paymode;
    }

    public void setPaymode(String paymode) {
        this.paymode = paymode;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRestid() {
        return restid;
    }

    public void setRestid(String restid) {
        this.restid = restid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
