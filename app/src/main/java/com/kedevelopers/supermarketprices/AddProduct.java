package com.kedevelopers.supermarketprices;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.rey.material.app.Dialog;
import com.rey.material.app.DialogFragment;
import com.rey.material.app.SimpleDialog;
import com.rey.material.app.ThemeManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AddProduct extends AppCompatActivity {

    private int PICK_IMAGE_REQUEST = 1;
    private Bitmap bitmap = null;
    private Button buttonChoose;
    private ImageView imageView;
    private ProgressDialog mProgressDialog;

    EditText productname, description, price, supermarket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mProgressDialog = new ProgressDialog(this);

        imageView = (ImageView) findViewById(R.id.imgView);
        buttonChoose = (Button) findViewById(R.id.btnupload);
        productname = (EditText) findViewById(R.id.productname);
        description = (EditText) findViewById(R.id.description);
        price = (EditText) findViewById(R.id.price);
        supermarket = (EditText) findViewById(R.id.supermarket);
        supermarket.setVisibility(View.VISIBLE);
        buttonChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser();
            }
        });
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String sm = extras.getString("supermarket");
            supermarket.setText(sm);

        }
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String a, b, c, d;
                a = productname.getText().toString().trim();
                b = description.getText().toString().trim();
                c = price.getText().toString().trim();
                d = supermarket.getText().toString().trim();
                if (!a.isEmpty() && !b.isEmpty() && !c.isEmpty() && !d.isEmpty()) {
                    AddProduct2(a, b, c, d);

                } else {
                    Toast.makeText(AddProduct.this, "Validate All Data First", Toast.LENGTH_SHORT).show();

                }

            }
        });
    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                //Getting the Bitmap from Gallery
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                //Setting the Bitmap to ImageView
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void clear() {
        description.setText(null);
        productname.setText(null);
        price.setText(null);
        supermarket.setText(null);
    }

    private void AddProduct2(final String productname, final String description, final String price, final String supermarket) {

        String tag_string_req = "req_save";
        // Toast.makeText(getApplicationContext(),"name" ,Toast.LENGTH_SHORT).show();
        final String attach = getStringImage(bitmap);

        showProgressDialog();
        StringRequest strReq = new StringRequest(Request.Method.POST,
                Config.ADD_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("TAG", "Upload Response: " + response.toString());

                hideProgressDialog();
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    String message = jObj.getString("message");

                    if (!error) {
                        Toast.makeText(AddProduct.this, message, Toast.LENGTH_SHORT).show();
                        clear();
                    } else {

                        Toast.makeText(AddProduct.this, message, Toast.LENGTH_SHORT).show();


                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", "Upload Error: " + error.getMessage());
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

                hideProgressDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                //  params.put("tag", "upload");
                params.put("product_name", productname);
                params.put("description", description);
                params.put("price", price);
                params.put("supermarket", supermarket);
                params.put("image", attach);


                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
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


}
