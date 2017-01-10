package com.kedevelopers.supermarketprices;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AdminAddUser extends AppCompatActivity {

    private ProgressDialog mProgressDialog;

    TextView fname,mname,lname,supermarket,password,email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_user);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mProgressDialog = new ProgressDialog(this);

        fname = (TextView) findViewById(R.id.firstname);
        mname = (TextView) findViewById(R.id.middlename);
        lname = (TextView) findViewById(R.id.lastname);
        email = (TextView) findViewById(R.id.email);

        supermarket = (TextView) findViewById(R.id.supermarketname);
        password = (TextView) findViewById(R.id.password);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               AddUsers();
            }
        });
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
    private void AddUsers() {
        final String fn= fname.getText().toString().trim();
        final String mn= mname.getText().toString().trim();
        final String ln= lname.getText().toString().trim();
        final String em= email.getText().toString().trim();

        final String sp= supermarket.getText().toString().trim();
        final String ps= password.getText().toString().trim();

        String tag_string_req = "req_save";
        // Toast.makeText(getApplicationContext(),"name" ,Toast.LENGTH_SHORT).show();

        showProgressDialog();
        StringRequest strReq = new StringRequest(Request.Method.POST,
                Config.URL_ADD_USER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("TAG", "Upload Response: " + response.toString());

                hideProgressDialog();
                try {

                  //  Toast.makeText(getApplicationContext(),response.toString(),Toast.LENGTH_SHORT).show();
                    if(response.toString().contains("false")){

                        Toast.makeText(AdminAddUser.this,"Successfully added users",Toast.LENGTH_SHORT).show();

                        mname.setText(null);
                        lname.setText(null);
                        fname.setText(null);
                        email.setText(null);
                        password.setText(null);
                        supermarket.setText(null);
                    }
                    JSONObject jObj = new JSONObject(response);


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", "Upload Error: " + error.getMessage());
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

                hideProgressDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                //  params.put("tag", "upload");
                params.put("name", fn );
               // params.put("mname", mn);
               // params.put("lname", ln);
                params.put("email", em);
                params.put("supermarket", sp);
                params.put("password", ps);


                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

}
