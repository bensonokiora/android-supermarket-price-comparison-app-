package com.kedevelopers.supermarketprices;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.kedevelopers.supermarketprices.Adapter.SuperMarketProductAdapter;
import com.kedevelopers.supermarketprices.Model.SupermarketProduct;
import com.rey.material.app.Dialog;
import com.rey.material.app.DialogFragment;
import com.rey.material.app.SimpleDialog;
import com.rey.material.app.ThemeManager;

import java.util.ArrayList;
import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SupermarketAdminProducts extends AppCompatActivity {

    private ArrayList<SupermarketProduct> listProduct;
    private ProgressBar progressBar1;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private SuperMarketProductAdapter adapter;
    public   static String supermarket;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supermarket_admin_products);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ShowDialog("Supermarket Admin" ,"List of all available products. Add price to existing products or create a new product");
        //Initializing Views
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        progressBar1 = (ProgressBar) findViewById(R.id.progressBar1);
          progressBar1.setVisibility(View.INVISIBLE);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
             supermarket = extras.getString("supermarket");
            String name = extras.getString("name");
            getSupportActionBar().setTitle( name.substring(0, 1).toUpperCase() + name.substring(1) + ": " + supermarket + " Admin");


        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SupermarketAdminProducts.this, AddProduct.class);
                intent.putExtra("supermarket", supermarket);

                startActivity(intent);

            }
        });
        loadProductsListJSON();

    }
    private void loadProductsListJSON(){
        progressBar1.setVisibility(View.VISIBLE);


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
                listProduct = new ArrayList<>(Arrays.asList(jsonResponse.getSupermarketProducts()));
                Log.d("Prods", listProduct.toString());

                adapter = new SuperMarketProductAdapter(listProduct, getApplicationContext());
                recyclerView.setAdapter(adapter);
                progressBar1.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onFailure(Call<JSONResponse> call, Throwable t) {
             //   Log.d("Error",t.getMessage());
            }
        });
    }
    private void ShowDialog(String title, String message){
        // int id = item.getItemId();
        Dialog.Builder builder = null;

        boolean isLightTheme = ThemeManager.getInstance().getCurrentTheme() == 0;

        builder = new SimpleDialog.Builder(isLightTheme ? R.style.SimpleDialogLight : R.style.SimpleDialog){
            @Override
            public void onPositiveActionClicked(DialogFragment fragment) {
                //  Toast.makeText(MainActivity.this, "Agreed", Toast.LENGTH_SHORT).show();
                super.onPositiveActionClicked(fragment);
            }

            @Override
            public void onNegativeActionClicked(DialogFragment fragment) {
                //   Toast.makeText(MainActivity.this, "Disagreed", Toast.LENGTH_SHORT).show();
                super.onNegativeActionClicked(fragment);
            }
        };

        ((SimpleDialog.Builder)builder).message(message)
                .title(title)
                .positiveAction("OK");
        DialogFragment fragment = DialogFragment.newInstance(builder);
        fragment.show(getSupportFragmentManager(),null);

    }
}
