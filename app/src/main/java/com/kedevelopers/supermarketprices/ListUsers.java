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
import android.widget.Toast;

import com.kedevelopers.supermarketprices.Adapter.UserAdapter;
import com.kedevelopers.supermarketprices.Model.User;
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

public class ListUsers extends AppCompatActivity {

    private ArrayList<User> listUsers;
    private ProgressBar progressBar1;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private UserAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_users);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Initializing Views
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        progressBar1 = (ProgressBar) findViewById(R.id.progressBar1);

        progressBar1.setVisibility(View.INVISIBLE);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ListUsers.this, AdminAddUser.class);
                startActivity(intent);
            }
        });
        loadUsersListJSON();
    }

    private void loadUsersListJSON(){
        progressBar1.setVisibility(View.VISIBLE);


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Config.KE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RequestInterface request = retrofit.create(RequestInterface.class);
        Call<JSONResponse> call = request.getUserssList();
        call.enqueue(new Callback<JSONResponse>() {

            @Override
            public void onResponse(Call<JSONResponse> call, retrofit2.Response<JSONResponse> response) {
                JSONResponse jsonResponse = response.body();
                // Log.d("Prods", jsonResponse.toString());
                //  Toast.makeText(getApplicationContext(),jsonResponse.toString(),Toast.LENGTH_SHORT).show();
                listUsers = new ArrayList<>(Arrays.asList(jsonResponse.getUsers()));
                Log.d("Prods", listUsers.toString());

                adapter = new UserAdapter(listUsers, getApplicationContext());
                recyclerView.setAdapter(adapter);
                progressBar1.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onFailure(Call<JSONResponse> call, Throwable t) {
                //   Log.d("Error",t.getMessage());
            }
        });
    }
}
