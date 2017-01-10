package com.kedevelopers.supermarketprices.Model;

/**
 * Created by BEN on 12/14/2016.
 */

public class Product {
    public String getImage() {
        return image;
    }

    public String getName() {
        return name;
    }
    public String getDescription() {
        return description;
    }

    private String image;



    private String name;
    private String description;

    public String getPrice() {
        return price;
    }

    private String price;

    public void setName(String name) {
        this.name = name;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
