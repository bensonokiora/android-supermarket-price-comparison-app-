package com.kedevelopers.supermarketprices;

/**
 * Created by BEN on 12/14/2016.
 */

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface RequestInterface {

    @GET("supermarket_api/v1/get-all-products")
    Call<JSONResponse> getJSON();

    @GET("supermarket_api/v1/get-all-supermarket-products")
    Call<JSONResponse> getProductsList();

    @GET("supermarket_api/v1/get-all-users")
    Call<JSONResponse> getUserssList();


}
