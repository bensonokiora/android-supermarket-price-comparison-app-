package com.kedevelopers.supermarketprices.Model;

/**
 * Created by BEN on 12/22/2016.
 */

public class SupermarketProduct {
    public void setImage(String image) {
        this.image = image;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    private String image;
    private String name;
    private String description;
    private String price;

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getPrice() {
        return price;
    }

    public String getImage() {
        return image;
    }



}
