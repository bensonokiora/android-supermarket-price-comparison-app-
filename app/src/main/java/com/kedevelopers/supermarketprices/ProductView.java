package com.kedevelopers.supermarketprices;

import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.kedevelopers.supermarketprices.Adapter.SupermarketPriceAdapter;
import com.kedevelopers.supermarketprices.Model.Product;
import com.kedevelopers.supermarketprices.Model.SupermarketProduct;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ProductView extends AppCompatActivity {
    private ProgressDialog mProgressDialog;
    TextView tx;
    ImageView imageView;
    private List<Product> listProducts;
    private ArrayList<SupermarketProduct> listProduct2;

    //Creating Views
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter adapter;

    //Volley Request Queue
    private RequestQueue requestQueue;

    //The request counter to send ?page=1, ?page=2  requests
    private int requestCount = 1;
    private String requestname = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mProgressDialog = new ProgressDialog(this);

         tx = (TextView)findViewById(R.id.textt);
        imageView = (ImageView) findViewById(R.id.imagesingle);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int value = extras.getInt("data");
            String name = extras.getString("name");
            String img = extras.getString("image");

            getSupportActionBar().setTitle(name);
              Picasso.with(ProductView.this).load(img).into(imageView);

            //The key argument here must match that used in the other activity
           // tx.setText(String.valueOf(value) +  name);

           ViewSellers(name);


            requestname = (name);
        }

        // ViewSellers("1");
        //Initializing Views
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);


        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //Initializing our Productses list
        listProducts = new ArrayList<>();
        listProduct2 = new ArrayList<>();

        requestQueue = Volley.newRequestQueue(this);

        //Calling method to get data to fetch data
      //  getData();

        //Adding an scroll change listener to recyclerview
        // recyclerView.setOnScrollChangeListener(this);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {

            recyclerView.setOnScrollChangeListener(new RecyclerView.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    //Ifscrolled at last then
                    if (isLastItemDisplaying(recyclerView)) {
                        //Calling the method getdata again
                     //   getData();
                    }
                }
            });
        } else {
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    //Ifscrolled at last then
                    if (isLastItemDisplaying(recyclerView)) {
                        //Calling the method getdata again
                      //  getData();
                    }
                }
            });

        }
        //initializing our adapter
     //   adapter = new ProductsAdapter(listProducts, this);

        //Adding adapter to recyclerview
      //  recyclerView.setAdapter(adapter);
        adapter = new SupermarketPriceAdapter(listProduct2, getApplicationContext());
        //Adding adapter to recyclerview
        recyclerView.setAdapter(adapter);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Share this product comparison", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("uploading...");
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }
    private void ViewSellers(final String productname) {
        String tag_string_req = "req_save";
       // Toast.makeText(getApplicationContext(),"name" ,Toast.LENGTH_SHORT).show();

        showProgressDialog();
        StringRequest strReq = new StringRequest(Request.Method.POST,
                Config.SELLER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("TAG", "Upload Response: " + response.toString());
               // Toast.makeText(getApplicationContext(),response.toString(),Toast.LENGTH_SHORT).show();

                hideProgressDialog();
                try {
                    JSONArray list = null;
                    JSONObject jObj = new JSONObject(response);
                    list = jObj.getJSONArray("result");


                     // Toast.makeText(getApplicationContext(),jObj.toString(),Toast.LENGTH_SHORT).show();
                 String t ="";
                    String p ="";

                    String img ="";

                   /* for(int i=0;i<list.length();i++) {
                        SupermarketProduct product = new SupermarketProduct();

                        JSONObject c = list.getJSONObject(i);

                        // User successfully stored in MySQL
                        // Now store the user in sqlite
                        // String uid = jObj.getString("productId");

                        // JSONObject user = jObj.getJSONObject("result");
                        String name = c.getString("supermarket");
                        p = c.getString("price");

                        product.setName(name);
                        product.setPrice(p);

                        listProduct2.add(product);

                        //tx.setText(t + p);
                        // img = c.getString("image");

                      //  Toast.makeText(getApplicationContext(),"name" +name ,Toast.LENGTH_SHORT).show();
                          //t += name + "\n\n";
                    }
                    adapter.notifyDataSetChanged();*/

                    for (int i = 0; i < list.length(); i++) {
                        //Creating the Product object
                        SupermarketProduct product = new SupermarketProduct();
                        JSONObject json = null;
                        try {
                            //Getting json
                            json = list.getJSONObject(i);

                            String name = json.getString("supermarket");
                            p = json.getString("price");

                            product.setName(name);
                            product.setPrice(p);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //Adding the Product object to the list
                        listProduct2.add(product);
                    }

                    //Notifying the adapter that data has been added or changed
                    adapter.notifyDataSetChanged();
                  //  Picasso.with(ProductView.this).load(img).into(imageView);
                  //   Toast.makeText(getApplicationContext(),jObj.toString(),Toast.LENGTH_SHORT).show();

                       // Toast.makeText(getApplicationContext(), "User successfully registered. Try login now!", Toast.LENGTH_LONG).show();

                       


                      //  String errorMsg = jObj.getString("message");
                      /*  Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();*/
                    hideProgressDialog();


                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),
                            e.toString(), Toast.LENGTH_LONG).show();

                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", "Upload Error: " + error.toString());
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(getApplicationContext(),"Check your internet Connection | TimeoutError",
                            Toast.LENGTH_LONG).show();
                } else if (error instanceof AuthFailureError) {
                    //TODO
                    Toast.makeText(getApplicationContext(),"AuthFailureError error",
                            Toast.LENGTH_LONG).show();
                } else if (error instanceof ServerError) {
                    //TODO
                    Toast.makeText(getApplicationContext(),"ServerError error",
                            Toast.LENGTH_LONG).show();
                } else if (error instanceof NetworkError) {
                    //TODO
                    Toast.makeText(getApplicationContext(),"NetworkError error",
                            Toast.LENGTH_LONG).show();
                } else if (error instanceof ParseError) {
                    //TODO
                    Toast.makeText(getApplicationContext(),"ParseError error",
                            Toast.LENGTH_LONG).show();
                }

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                //  params.put("tag", "upload");
               params.put("product_name", productname);


                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private JsonArrayRequest getDataFromServer(int requestCount) {
        //Initializing ProgressBar
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar1);

        //Displaying Progressbar
        progressBar.setVisibility(View.VISIBLE);
        setProgressBarIndeterminateVisibility(true);

        //JsonArrayRequest of volley
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Config.SELLER_URL + "Product1",
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        //Calling method parseData to parse the json response
                        parseData(response);
                        //Hiding the progressbar
                        progressBar.setVisibility(View.GONE);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar.setVisibility(View.GONE);
                        //If an error occurs that means end of the list has reached
                        Toast.makeText(ProductView.this, "No More Items Available", Toast.LENGTH_SHORT).show();
                    }
                });

        //Returning the request
        return jsonArrayRequest;
    }

    //This method will get data from the web api
    private void getData() {
        //Adding the method to the queue by calling the method getDataFromServer
        requestQueue.add(getDataFromServer(requestCount));
        //Incrementing the request counter
        requestCount++;
    }

    //This method will parse json data
    private void parseData(JSONArray array) {
        for (int i = 0; i < array.length(); i++) {
            //Creating the Products object
            Product Products = new Product();
            JSONObject json = null;
            try {
                //Getting json
                json = array.getJSONObject(i);

                //Adding data to the Products object
                Products.setImage(json.getString(Config.TAG_IMAGE_URL));
                Products.setName(json.getString(Config.TAG_NAME));
                //Products.setPublisher(json.getString(Config.TAG_PUBLISHER));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //Adding the Products object to the list
            listProducts.add(Products);
        }

        //Notifying the adapter that data has been added or changed
        adapter.notifyDataSetChanged();
    }

    //This method would check that the recyclerview scroll has reached the bottom or not
    private boolean isLastItemDisplaying(RecyclerView recyclerView) {
        if (recyclerView.getAdapter().getItemCount() != 0) {
            int lastVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
            if (lastVisibleItemPosition != RecyclerView.NO_POSITION && lastVisibleItemPosition == recyclerView.getAdapter().getItemCount() - 1)
                return true;
        }
        return false;
    }

    private void loadJSON(){
      //  progressBar1.setVisibility(View.VISIBLE);


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Config.KE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RequestInterface request = retrofit.create(RequestInterface.class);
        Call<JSONResponse> call = request.getProductsList();
        call.enqueue(new Callback<JSONResponse>() {

            @Override
            public void onResponse(Call<JSONResponse> call, retrofit2.Response<JSONResponse> response) {
                JSONResponse jsonResponse = response.body();
                // Log.d("Prods", jsonResponse.toString());
                //  Toast.makeText(getApplicationContext(),jsonResponse.toString(),Toast.LENGTH_SHORT).show();
                listProduct2 = new ArrayList<>(Arrays.asList(jsonResponse.getSupermarketPrices()));
                Log.d("Prods", listProduct2.toString());

                adapter = new SupermarketPriceAdapter(listProduct2, getApplicationContext());
                recyclerView.setAdapter(adapter);
              //  progressBar1.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onFailure(Call<JSONResponse> call, Throwable t) {
//                Log.d("Error",t.getMessage());
            }
        });
    }
}

