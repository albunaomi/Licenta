package com.example.diana.dreamcakes.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.diana.dreamcakes.Interface.ItemClickListener;
import com.example.diana.dreamcakes.R;

/**
 * Created by Diana on 5/17/2020.
 */

public class CakeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView cake_name, price;
    public ImageView image,fav_img,cart_img;
    private ItemClickListener itemClickListener;

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public CakeViewHolder(View itemView) {
        super(itemView);
        cake_name=(TextView)itemView.findViewById(R.id.cake_name);
        image= (ImageView)itemView.findViewById(R.id.cake_image);
        price=(TextView)itemView.findViewById(R.id.price);
        fav_img=(ImageView)itemView.findViewById(R.id.btn_favorite);
        cart_img=(ImageView)itemView.findViewById(R.id.btn_cart);

        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        itemClickListener.onClick(v,getAdapterPosition(),false);
    }
}
