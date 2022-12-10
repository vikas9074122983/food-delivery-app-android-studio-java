package org.ganeshatech18102021.fooddelivery.model;

public class ProductModel {
    private String id,name,url;
    private long price;

    public ProductModel() {
    }

    public ProductModel(String id, String name, String url, long price) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }
}
