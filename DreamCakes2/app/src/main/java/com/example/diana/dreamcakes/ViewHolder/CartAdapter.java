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
import com.example.diana.dreamcakes.Database.Database;
import com.example.diana.dreamcakes.Interface.ItemClickListener;
import com.example.diana.dreamcakes.Model.CartItem;
import com.example.diana.dreamcakes.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;



public class CartAdapter extends  RecyclerView.Adapter<CartViewHolder> {

    private List<CartItem> cartList=new ArrayList<>();
    private Cart cart;

    public CartAdapter(List<CartItem> cartList, Cart cart) {
        this.cartList = cartList;
        this.cart = cart;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater=LayoutInflater.from(cart);
        View view=inflater.inflate(R.layout.cart_item_layout,parent,false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, final int position) {

        Picasso.with(cart).load(cartList.get(position).getCakeImage())
                .into(holder.image);
        holder.quantity.setNumber(String.valueOf(cartList.get(position).getQuantity()));
        double priceIt=(Double.parseDouble(cartList.get(position).getPrice()))*(Integer.parseInt(cartList.get(position).getQuantity()));
        holder.price.setText(new StringBuilder("RON ").append(priceIt));
        holder.name.setText(cartList.get(position).getCakeName());

        holder.quantity.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
            @Override
            public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {
                CartItem cartItem=cartList.get(position);
                cartItem.setQuantity(String.valueOf(newValue));
                new Database(cart).updateCart(cartItem);
                double total=0;
                List<CartItem> items=new Database(cart).getCartItems();
                for(CartItem item:items)
                    total+=(Double.parseDouble(item.getPrice())*(Integer.parseInt(item.getQuantity())));
                cart.tPrice= String.valueOf(total);
                cart.totalPrice.setText(new StringBuilder("Total:").append(total));

            }
        });

    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    public CartItem getItem(int position){
        return cartList.get(position);
    }

    public void removeItem(int position){
        cartList.remove(position);
        notifyItemRemoved(position);
    }
    public void restoreItem(CartItem item,int position){
        cartList.add(position,item);
        notifyItemInserted(position);
    }
}
