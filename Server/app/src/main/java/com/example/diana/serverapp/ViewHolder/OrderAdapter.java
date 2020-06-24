package com.example.diana.serverapp.ViewHolder;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.diana.serverapp.Interface.ItemClickListener;
import com.example.diana.serverapp.Model.CartItem;
import com.example.diana.serverapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.MyViewHolder>  {
    private List<CartItem> cakes;
    Context context;

    public OrderAdapter(List<CartItem> cakes, Context context) {
        this.cakes = cakes;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context)
        .inflate(R.layout.order_cakes_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Picasso.with(context).load(cakes.get(position).getCakeImage())
                .into(holder.image);
        holder.quantity.setText(new StringBuilder("Quantity: ").append(cakes.get(position).getQuantity()).append(" Kg"));
        holder.cake_name.setText(cakes.get(position).getCakeName());
    }

    @Override
    public int getItemCount() {
        return cakes.size();
    }

    public class MyViewHolder extends  RecyclerView.ViewHolder {
        public TextView cake_name, quantity;
        public ImageView image;

        public MyViewHolder(View itemView) {
            super(itemView);
            cake_name=(TextView)itemView.findViewById(R.id.cake_n);
            image= (ImageView)itemView.findViewById(R.id.cake_img);
            quantity=(TextView)itemView.findViewById(R.id.cake_kg);
        }
    }
}
