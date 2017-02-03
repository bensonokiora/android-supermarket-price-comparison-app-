package com.kedevelopers.supermarketprices.Adapter;

/**
 * Created by BEN on 11/17/2016.
 */

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.kedevelopers.supermarketprices.CustomVolleyRequest;
import com.kedevelopers.supermarketprices.Model.Product;
import com.kedevelopers.supermarketprices.ProductView;
import com.kedevelopers.supermarketprices.R;

import java.util.ArrayList;
import java.util.List;


public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ViewHolder> {

    //Imageloader to load image
    private ImageLoader imageLoader;
    private Context context;
    //List to store all Productses
    ArrayList<Product> Productses;
    private  ArrayList<Product> mproducts;
    //Constructor of this class
    public ProductsAdapter(ArrayList<Product> Productses, Context context){
        super();
        //Getting all Productses
        this.Productses = Productses;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.products_list, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        //Getting the particular item from the list
        Product Products =  Productses.get(position);

        //Loading image from url
        imageLoader = CustomVolleyRequest.getInstance(context).getImageLoader();
        imageLoader.get(Products.getImage(), ImageLoader.getImageListener(holder.imageView, R.drawable.image1, android.R.drawable.ic_dialog_alert));
        //Showing data on the views
        holder.imageView.setImageUrl(Products.getImage(), imageLoader);
        holder.textViewName.setText(Products.getName());
        holder.textViewDescription.setText(Products.getDescription());

    }

    @Override
    public int getItemCount() {
        return Productses.size();
    }
    public void setFilter(List<Product> products){
        Productses = new ArrayList<>();
        Productses.addAll(products);
        notifyDataSetChanged();
    }
    class ViewHolder extends RecyclerView.ViewHolder{
        //Views
        public NetworkImageView imageView;
        public TextView textViewName;
        public TextView textViewDescription;
        public LinearLayout card_layout;

        //Initializing Views
        public ViewHolder(View itemView) {
            super(itemView);
            imageView = (NetworkImageView) itemView.findViewById(R.id.imageViewHero);
            textViewName = (TextView) itemView.findViewById(R.id.textViewName);
            textViewDescription = (TextView) itemView.findViewById(R.id.textViewDescription);
            card_layout= (LinearLayout) itemView.findViewById(R.id.card_layout);

            card_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Context context = view.getContext();
                    int position = getAdapterPosition();
                    Product Products =  Productses.get(position);

                    Intent intent = new Intent(context, ProductView.class);

                    intent.putExtra("data",position);
                    intent.putExtra("name", Products.getName() );

                    intent.putExtra("image", Products.getImage() );


                    context.startActivity(intent);

                }
            });
        }

    }

}