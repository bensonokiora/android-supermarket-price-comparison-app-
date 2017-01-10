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
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.kedevelopers.supermarketprices.AppController;
import com.kedevelopers.supermarketprices.Config;
import com.kedevelopers.supermarketprices.CustomVolleyRequest;
import com.kedevelopers.supermarketprices.Model.SupermarketProduct;
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

public class SupermarketPriceAdapter extends RecyclerView.Adapter<SupermarketPriceAdapter.ViewHolder> {

    //Imageloader to load image
    private ImageLoader imageLoader;
    private Context context;
    private SupermarketPriceAdapter context2;
    private ProgressDialog mProgressDialog;

    //List to store all Products
    ArrayList<SupermarketProduct> Products;
    private  ArrayList<SupermarketProduct> mproducts;
    //Constructor of this class
    public SupermarketPriceAdapter(ArrayList<SupermarketProduct> Products, Context context){
        super();
        //Getting all Products
        this.Products = Products;
        this.context = context;
    }

    @Override
    public SupermarketPriceAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.prices_list, parent, false);
        mProgressDialog = new ProgressDialog(context);

        SupermarketPriceAdapter.ViewHolder viewHolder = new SupermarketPriceAdapter.ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(SupermarketPriceAdapter.ViewHolder holder, int position) {

        //Getting the particular item from the list
        SupermarketProduct Product =  Products.get(position);


        //Showing data on the views
        holder.textViewSupermarket.setText(Product.getName());
        holder.textViewPrice.setText(Product.getPrice());

    }

    @Override
    public int getItemCount() {
        return Products.size();
    }
    public void setFilter(List<SupermarketProduct> products){
        Products = new ArrayList<>();
        Products.addAll(products);
        notifyDataSetChanged();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        //Views
        public TextView textViewSupermarket;
        public TextView textViewPrice;
        public LinearLayout card_layout;

        //Initializing Views
        public ViewHolder(View itemView) {
            super(itemView);
            textViewSupermarket = (TextView) itemView.findViewById(R.id.textViewSupermarket);
            textViewPrice = (TextView) itemView.findViewById(R.id.textViewPrice);
            card_layout = (LinearLayout) itemView.findViewById(R.id.card_layout);



        }


    }





}