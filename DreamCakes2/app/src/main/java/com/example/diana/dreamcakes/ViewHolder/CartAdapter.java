package com.example.diana.dreamcakes.ViewHolder;


import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.diana.dreamcakes.Cart;
import com.example.diana.dreamcakes.Interface.ItemClickListener;
import com.example.diana.dreamcakes.Model.CartItem;
import com.example.diana.dreamcakes.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView name,price;
    public ImageView image;
    private ItemClickListener itemClickListener;
    ElegantNumberButton quantity;

    public CartViewHolder(View itemView) {
        super(itemView);
        name=(TextView)itemView.findViewById(R.id.item_name);
        price=(TextView)itemView.findViewById(R.id.item_price);
        image=(ImageView)itemView.findViewById(R.id.item_img);
        quantity=(ElegantNumberButton)itemView.findViewById(R.id.quantity);
    }

    public void setName(TextView name) {
        this.name = name;
    }

    @Override
    public void onClick(View v) {

    }
}

public class CartAdapter extends  RecyclerView.Adapter<CartViewHolder> {

    private List<CartItem> cartList=new ArrayList<>();
    private Context context;

    public CartAdapter(List<CartItem> cartList, Context context) {
        this.cartList = cartList;
        this.context = context;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater=LayoutInflater.from(context);
        View view=inflater.inflate(R.layout.cart_item_layout,parent,false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, final int position) {

        Picasso.with(context).load(cartList.get(position).getCakeImage())
                .into(holder.image);
        holder.quantity.setNumber(String.valueOf(cartList.get(position).getQuantity()));
        double priceIt=(Double.parseDouble(cartList.get(position).getPrice()))*(Integer.parseInt(cartList.get(position).getQuantity()));
        holder.price.setText(new StringBuilder("Lei").append(priceIt));
        holder.name.setText(cartList.get(position).getCakeName());

        holder.quantity.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
            @Override
            public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {
                CartItem cart=cartList.get(position);
                cart.setQuantity(String.valueOf(newValue));
            }
        });

    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }
}
