package com.example.diana.serverapp.ViewHolder;


import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.widget.TextView;

import com.example.diana.serverapp.Common.Common;
import com.example.diana.serverapp.Interface.ItemClickListener;
import com.example.diana.serverapp.R;


public class OrderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnCreateContextMenuListener{

    public TextView  orderId,orderStatus,orderPhone,orderAddress,orderDate;

    private ItemClickListener itemClickListener;
    public OrderViewHolder(View itemView) {
        super(itemView);
        orderAddress=(TextView)itemView.findViewById(R.id.order_address);
        orderPhone=(TextView)itemView.findViewById(R.id.order_phone);
        orderStatus=(TextView)itemView.findViewById(R.id.order_status);
        orderId=(TextView)itemView.findViewById(R.id.order_id);
        orderDate=(TextView)itemView.findViewById(R.id.order_date);


        itemView.setOnClickListener(this);
        itemView.setOnCreateContextMenuListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {
       itemClickListener.onClick(v,getAdapterPosition(),false);

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("Select The Action");
        menu.add(0,0,getAdapterPosition(), "Update status");
        menu.add(0,1,getAdapterPosition(), Common.DELETE);
    }
}
