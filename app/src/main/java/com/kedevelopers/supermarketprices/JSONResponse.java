package com.kedevelopers.supermarketprices;

import com.kedevelopers.supermarketprices.Model.Product;
import com.kedevelopers.supermarketprices.Model.SupermarketProduct;
import com.kedevelopers.supermarketprices.Model.User;

/**
 * Created by BEN on 12/14/2016.
 */

public class JSONResponse {
    private Product[] products;
    private SupermarketProduct[] supermarketProducts;
    private SupermarketProduct[] supermarketPrices;
    private User[] user;

    public Product[] getProducts() {
        return products;
    }
    public User[] getUsers() {
        return user;
    }
    public SupermarketProduct[] getSupermarketProducts() {
        return supermarketProducts;
    }
    public SupermarketProduct[] getSupermarketPrices() {
        return supermarketPrices;
    }
}
