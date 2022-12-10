package org.ganeshatech18102021.fooddelivery.model;

public class RestModel {
    private String access,address,email,name,phone,url,id;

    public RestModel() {
    }

    public RestModel(String access, String address, String email, String name, String phone, String url, String id) {
        this.access = access;
        this.address = address;
        this.email = email;
        this.name = name;
        this.phone = phone;
        this.url = url;
        this.id = id;
    }

    public String getAccess() {
        return access;
    }

    public void setAccess(String access) {
        this.access = access;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
