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

import java.util.HashMap;
import java.util.List;
import java.util.Map;


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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mProgressDialog = new ProgressDialog(this);

        tx = (TextView) findViewById(R.id.textt);
        imageView = (ImageView) findViewById(R.id.imagesingle);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String name = extras.getString("name");
            String img = extras.getString("image");

            getSupportActionBar().setTitle(name);
            Picasso.with(ProductView.this).load(img).into(imageView);


            ViewSellers(name);


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


                    String p = "";

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
                    Toast.makeText(getApplicationContext(), "Check your internet Connection | TimeoutError",
                            Toast.LENGTH_LONG).show();
                } else if (error instanceof AuthFailureError) {
                    //TODO
                    Toast.makeText(getApplicationContext(), "AuthFailureError error",
                            Toast.LENGTH_LONG).show();
                } else if (error instanceof ServerError) {
                    //TODO
                    Toast.makeText(getApplicationContext(), "ServerError error",
                            Toast.LENGTH_LONG).show();
                } else if (error instanceof NetworkError) {
                    //TODO
                    Toast.makeText(getApplicationContext(), "NetworkError error",
                            Toast.LENGTH_LONG).show();
                } else if (error instanceof ParseError) {
                    //TODO
                    Toast.makeText(getApplicationContext(), "ParseError error",
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


}

