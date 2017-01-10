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
 * Created by BEN on 12/22/2016.
 */

public class SuperMarketProductAdapter extends RecyclerView.Adapter<SuperMarketProductAdapter.ViewHolder> {

    //Imageloader to load image
    private ImageLoader imageLoader;
    private Context context;
    private ProgressDialog mProgressDialog;

    //List to store all Products
    ArrayList<SupermarketProduct> Products;
    private  ArrayList<SupermarketProduct> mproducts;
    //Constructor of this class
    public SuperMarketProductAdapter(ArrayList<SupermarketProduct> Products, Context context){
        super();
        //Getting all Products
        this.Products = Products;
        this.context = context;
    }

    @Override
    public SuperMarketProductAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.products_list, parent, false);
        mProgressDialog = new ProgressDialog(context);

        SuperMarketProductAdapter.ViewHolder viewHolder = new SuperMarketProductAdapter.ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(SuperMarketProductAdapter.ViewHolder holder, int position) {

        //Getting the particular item from the list
        SupermarketProduct Product =  Products.get(position);

        //Loading image from url
        imageLoader = CustomVolleyRequest.getInstance(context).getImageLoader();
        imageLoader.get(Product.getImage(), ImageLoader.getImageListener(holder.imageView, R.drawable.image1, android.R.drawable.ic_dialog_alert));

        //Showing data on the views
        holder.imageView.setImageUrl(Product.getImage(), imageLoader);
        holder.textViewName.setText(Product.getName());
        holder.textViewDescription.setText(Product.getDescription());

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
    private  void ShowAddDialog(){
      /*  Dialog.Builder builder = null;

        boolean isLightTheme = ThemeManager.getInstance().getCurrentTheme() == 0;

        builder = new SimpleDialog.Builder(isLightTheme ? R.style.SimpleDialogLight : R.style.SimpleDialog){

            @Override
            protected void onBuildDone(Dialog dialog) {
                dialog.layoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            }

            @Override
            public void onPositiveActionClicked(DialogFragment fragment) {
                EditText et_pass = (EditText)fragment.getDialog().findViewById(R.id.custom_et_password);
                // Toast.makeText(mActivity, "Connected. pass=" + et_pass.getText().toString(), Toast.LENGTH_SHORT).show();
                super.onPositiveActionClicked(fragment);
            }

            @Override
            public void onNegativeActionClicked(DialogFragment fragment) {
                // Toast.makeText(mActivity, "Cancelled", Toast.LENGTH_SHORT).show();
                super.onNegativeActionClicked(fragment);
            }
        };

        builder.title("Google Wi-Fi")
                .positiveAction("CONNECT")
                .negativeAction("CANCEL")
                .contentView(R.layout.layout_dialog_add_product);
        FragmentManager fragmentManager = context.getFragmentManager();
        DialogFragment fragment = DialogFragment.newInstance(builder);
        fragment.show(context.getSupp(), null);*/
    }

    class ViewHolder extends RecyclerView.ViewHolder {
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
            card_layout = (LinearLayout) itemView.findViewById(R.id.card_layout);

            card_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Context context = view.getContext();
                    final int position = getAdapterPosition();
                    LayoutInflater li = LayoutInflater.from(context);
                    View promptsView = li.inflate(R.layout.prompt_add_product, null);

                    TextView textView =(TextView) promptsView.findViewById(R.id.textView1);

                    textView.setText("Add Your Supermarket Price on \n\n" + textViewName.getText() +" \n\n (ksh)");
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                            context);

                    // set prompts.xml to alertdialog builder
                    alertDialogBuilder.setView(promptsView);

                    final EditText userInput = (EditText) promptsView.findViewById(R.id.editTextDialogUserInput);

                    // set dialog message
                    alertDialogBuilder
                            .setCancelable(false)
                            .setPositiveButton("OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            // get user input and set it to result
                                            // edit text
                                            // Toast.makeText(context,  "position " +position +textViewName.getText(), Toast.LENGTH_SHORT).show();
                                           String pn = textViewName.getText().toString();
                                            String pr = userInput.getText().toString();
                                            String sm = SupermarketAdminProducts.supermarket;
                                            AddPrice(pn,pr, sm);
                                        }
                                    })
                            .setNegativeButton("Cancel",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });

                    // create alert dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();

                    // show it
                    alertDialog.show();

                }
            });
card_layout.setOnLongClickListener(new View.OnLongClickListener() {
    @Override
    public boolean onLongClick(View view) {

       // DeleteProduct()
        return false;
    }
});
        }

        private void DeleteProduct(final String id) {

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

                        if(!error){
                            Toast.makeText(context,message,Toast.LENGTH_SHORT).show();

                        }

                        else{

                            Toast.makeText(context,message,Toast.LENGTH_SHORT).show();


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
                        Toast.makeText(context,"Check your internet Connection | TimeoutError",
                                Toast.LENGTH_LONG).show();
                    } else if (error instanceof AuthFailureError) {
                        //TODO
                        Toast.makeText(context,"AuthFailureError error",
                                Toast.LENGTH_LONG).show();
                    } else if (error instanceof ServerError) {
                        //TODO
                        Toast.makeText(context,"ServerError error",
                                Toast.LENGTH_LONG).show();
                    } else if (error instanceof NetworkError) {
                        //TODO
                        Toast.makeText(context,"NetworkError error",
                                Toast.LENGTH_LONG).show();
                    } else if (error instanceof ParseError) {
                        //TODO
                        Toast.makeText(context,"ParseError error",
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

        private void AddPrice(final String productname,final String price,final String supermarket) {

            String tag_string_req = "req_save";

           // showProgressDialog();
            StringRequest strReq = new StringRequest(Request.Method.POST,
                    Config.ADD_URL_PRICE, new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {
                    Log.d("TAG", "Upload Response: " + response.toString());

             //       hideProgressDialog();
                    try {
                        JSONObject jObj = new JSONObject(response);
                        boolean error = jObj.getBoolean("error");
                        String message = jObj.getString("message");

                        if(!error){
                            Toast.makeText(context,message,Toast.LENGTH_SHORT).show();

                        }

                        else{

                            Toast.makeText(context,message,Toast.LENGTH_SHORT).show();


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
                        Toast.makeText(context,"Check your internet Connection | TimeoutError",
                                Toast.LENGTH_LONG).show();
                    } else if (error instanceof AuthFailureError) {
                        //TODO
                        Toast.makeText(context,"AuthFailureError error",
                                Toast.LENGTH_LONG).show();
                    } else if (error instanceof ServerError) {
                        //TODO
                        Toast.makeText(context,"ServerError error",
                                Toast.LENGTH_LONG).show();
                    } else if (error instanceof NetworkError) {
                        //TODO
                        Toast.makeText(context,"NetworkError error",
                                Toast.LENGTH_LONG).show();
                    } else if (error instanceof ParseError) {
                        //TODO
                        Toast.makeText(context,"ParseError error",
                                Toast.LENGTH_LONG).show();
                    }

               //     hideProgressDialog();
                }
            }) {

                @Override
                protected Map<String, String> getParams() {
                    // Posting params to register url
                    Map<String, String> params = new HashMap<String, String>();
                    //  params.put("tag", "upload");
                    params.put("product_name", productname);
                    params.put("price", price);
                    params.put("supermarket", supermarket);

                    return params;
                }

            };

            // Adding request to request queue
            AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
        }
        }





}