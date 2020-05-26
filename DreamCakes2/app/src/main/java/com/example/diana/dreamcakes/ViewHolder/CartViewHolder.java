package com.example.diana.dreamcakes.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.diana.dreamcakes.Interface.ItemClickListener;
import com.example.diana.dreamcakes.R;


public class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView name,price;
    public ImageView image;
    private ItemClickListener itemClickListener;
    ElegantNumberButton quantity;

    public RelativeLayout view_background;
    public LinearLayout view_foreground;

    public CartViewHolder(View itemView) {
        super(itemView);
        name=(TextView)itemView.findViewById(R.id.item_name);
        price=(TextView)itemView.findViewById(R.id.item_price);
        image=(ImageView)itemView.findViewById(R.id.item_img);
        quantity=(ElegantNumberButton)itemView.findViewById(R.id.quantity);
        view_background=(RelativeLayout) itemView.findViewById(R.id.view_background);
        view_foreground=(LinearLayout) itemView.findViewById(R.id.view_foreground);
    }

    public void setName(TextView name) {
        this.name = name;
    }

    @Override
    public void onClick(View v) {

    }
}
