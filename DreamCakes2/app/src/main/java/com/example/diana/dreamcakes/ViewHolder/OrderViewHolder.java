package com.example.diana.dreamcakes.ViewHolder;


import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.diana.dreamcakes.Interface.ItemClickListener;
import com.example.diana.dreamcakes.R;

public class OrderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView  orderId,orderStatus,orderPhone,orderAddress;

    private ItemClickListener itemClickListener;
    public OrderViewHolder(View itemView) {
        super(itemView);
        orderAddress=(TextView)itemView.findViewById(R.id.order_address);
        orderPhone=(TextView)itemView.findViewById(R.id.order_phone);
        orderStatus=(TextView)itemView.findViewById(R.id.order_status);
        orderId=(TextView)itemView.findViewById(R.id.order_id);

        itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {
       itemClickListener.onClick(v,getAdapterPosition(),false);

    }
}
