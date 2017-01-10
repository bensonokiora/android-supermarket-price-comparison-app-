package com.kedevelopers.supermarketprices;

/**
 * Created by BEN on 11/17/2016.
 */

public class Config {
    //Data URL
    public static final String KE_URL = "http://192.168.137.1";
   public static final String SELLER_URL = "http://api.kedevelopers.com/supermarket_api/getSellers.php?name=";
    public static final String SELLER = "http://api.kedevelopers.com/supermarket_api/v1/get-sellers";


    //JSON TAGS
    public static final String TAG_IMAGE_URL = "image";
    public static final String TAG_NAME = "name";
    public static final String URL_ADD_USER ="http://api.kedevelopers.com/supermarket_api/v1/register" ;
    public static final String URL_SUP_USER ="http://api.kedevelopers.com/supermarket_api/v1/login" ;
    public static final String ADD_URL ="http://api.kedevelopers.com/supermarket_api/v1/add-product" ;
    public static final String ADD_URL_PRICE ="http://api.kedevelopers.com/supermarket_api/v1/add-product-price" ;

    public static final String DELETE_USER_URL = "http://api.kedevelopers.com/supermarket_api/v1/delete-user";
}
