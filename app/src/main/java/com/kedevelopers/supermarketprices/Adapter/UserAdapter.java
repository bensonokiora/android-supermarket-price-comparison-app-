package com.kedevelopers.supermarketprices.Adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.kedevelopers.supermarketprices.AddProduct;
import com.kedevelopers.supermarketprices.AppController;
import com.kedevelopers.supermarketprices.Config;
import com.kedevelopers.supermarketprices.ListUsers;
import com.kedevelopers.supermarketprices.Model.User;
import com.kedevelopers.supermarketprices.R;
import com.kedevelopers.supermarketprices.SupermarketAdminProducts;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by BEN on 12/23/2016.
 */

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    //List to store all Users
    ArrayList<User> Users;
    //Imageloader to load image
    private ImageLoader imageLoader;
    private Context context;
    private UserAdapter context2;
    private ProgressDialog mProgressDialog;
    private ArrayList<User> mUsers;

    //Constructor of this class
    public UserAdapter(ArrayList<User> Users, Context context) {
        super();
        //Getting all Users
        this.Users = Users;
        this.context = context;
    }

    @Override
    public UserAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.users_list, parent, false);
        mProgressDialog = new ProgressDialog(context);

        UserAdapter.ViewHolder viewHolder = new UserAdapter.ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(UserAdapter.ViewHolder holder, int position) {

        //Getting the particular item from the list
        User user = Users.get(position);


        //Showing data on the views
        holder.textViewSupermarket.setText(user.getSupermarket());
        holder.textViewName.setText(user.getName());
        holder.textViewEmail.setText(user.getEmail());
        holder.textViewid.setText(user.getId());

    }

    @Override
    public int getItemCount() {
        return Users.size();
    }

    public void setFilter(List<User> Users) {
        Users = new ArrayList<>();
        Users.addAll(Users);
        notifyDataSetChanged();
    }

    private void DeleteUser(final String id) {

        String tag_string_req = "req_save";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                Config.DELETE_USER_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("TAG", "Upload Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    String message = jObj.getString("message");

                    if (!error) {
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();

                    } else {

                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();


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
                    Toast.makeText(context, "Check your internet Connection | TimeoutError",
                            Toast.LENGTH_LONG).show();
                } else if (error instanceof AuthFailureError) {
                    //TODO
                    Toast.makeText(context, "AuthFailureError error",
                            Toast.LENGTH_LONG).show();
                } else if (error instanceof ServerError) {
                    //TODO
                    Toast.makeText(context, "ServerError error",
                            Toast.LENGTH_LONG).show();
                } else if (error instanceof NetworkError) {
                    //TODO
                    Toast.makeText(context, "NetworkError error",
                            Toast.LENGTH_LONG).show();
                } else if (error instanceof ParseError) {
                    //TODO
                    Toast.makeText(context, "ParseError error",
                            Toast.LENGTH_LONG).show();
                }

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                //  params.put("tag", "upload");
                params.put("id", id);


                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        //Views
        public TextView textViewSupermarket;
        public TextView textViewName;
        public TextView textViewEmail;
        public TextView textViewid;

        public LinearLayout card_layout;

        //Initializing Views
        public ViewHolder(View itemView) {
            super(itemView);
            textViewSupermarket = (TextView) itemView.findViewById(R.id.textViewSupermarket);
            textViewName = (TextView) itemView.findViewById(R.id.textViewName);
            textViewid = (TextView) itemView.findViewById(R.id.textViewId);

            textViewEmail = (TextView) itemView.findViewById(R.id.textViewEmail);
            card_layout = (LinearLayout) itemView.findViewById(R.id.card_layout);

            card_layout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {

                    String em = textViewid.getText().toString();
                    DeleteUser(em);

                    return false;
                }
            });
            card_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });

        }


    }


}