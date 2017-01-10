package com.kedevelopers.supermarketprices.Adapter;


import android.content.Context;
        import android.content.Intent;
import android.os.Bundle;
        import android.support.v7.widget.RecyclerView;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
import android.widget.TextView;

        import com.android.volley.toolbox.ImageLoader;
        import com.android.volley.toolbox.NetworkImageView;
import com.kedevelopers.supermarketprices.CustomVolleyRequest;
import com.kedevelopers.supermarketprices.ProductView;
import com.kedevelopers.supermarketprices.R;
import com.kedevelopers.supermarketprices.SingleProduct;

import java.util.ArrayList;
        import java.util.List;

/**
 */
public class SingleProductAdapter extends RecyclerView.Adapter<SingleProductAdapter.ViewHolder> {

    //Imageloader to load image
    private ImageLoader imageLoader;
    private Context context;

    //List to store all Productses
    List<SingleProduct> Productses;
    private  List<SingleProduct> mproducts;
    //Constructor of this class
    public SingleProductAdapter(List<SingleProduct> Productses, Context context){
        super();
        //Getting all Productses
        this.Productses = Productses;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.seller_list, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        //Getting the particular item from the list
        SingleProduct Products =  Productses.get(position);

        //Loading image from url
        imageLoader = CustomVolleyRequest.getInstance(context).getImageLoader();
        imageLoader.get(Products.getImageUrl(), ImageLoader.getImageListener(holder.imageView, R.drawable.image1, android.R.drawable.ic_dialog_alert));

        //Showing data on the views
        holder.imageView.setImageUrl(Products.getImageUrl(), imageLoader);
        holder.textViewName.setText(Products.getName());

    }

    @Override
    public int getItemCount() {
        return Productses.size();
    }
    public void setFilter(List<SingleProduct> products){
        mproducts = new ArrayList<>();
        mproducts.addAll(products);
        notifyDataSetChanged();
    }
    class ViewHolder extends RecyclerView.ViewHolder{
        //Views
        public NetworkImageView imageView;
        public TextView textViewName;

        //Initializing Views
        public ViewHolder(View itemView) {
            super(itemView);
            imageView = (NetworkImageView) itemView.findViewById(R.id.imageViewHero);
            textViewName = (TextView) itemView.findViewById(R.id.textViewName);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Context context = view.getContext();
                    int position = getAdapterPosition();
                    Intent intent = new Intent(context, ProductView.class);

                    intent.putExtra("data",position);
                    intent.putExtra("name", textViewName.getText() );

                    context.startActivity(intent);

                }
            });
        }

    }

}