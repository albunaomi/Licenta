package com.example.diana.dreamcakes;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.diana.dreamcakes.Common.Common;
import com.example.diana.dreamcakes.Model.Request;
import com.example.diana.dreamcakes.ViewHolder.OrderViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Order extends AppCompatActivity {

    public RecyclerView recyclerView;
    public  RecyclerView.LayoutManager layoutManager;

    FirebaseRecyclerAdapter<Request,OrderViewHolder>adapter;
    FirebaseDatabase database;
    DatabaseReference requests;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        database=FirebaseDatabase.getInstance();
        requests=database.getReference("Requests");

        recyclerView=(RecyclerView)findViewById(R.id.recycler_orders);
        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        loadOrders(Common.uphone);
    }

    private void loadOrders(String phone) {
        adapter=new FirebaseRecyclerAdapter<Request,OrderViewHolder>(
                Request.class,
                R.layout.order_item,
                OrderViewHolder.class,
                requests.orderByChild("phone")
                .equalTo(phone)
        ){

            @Override
            protected void populateViewHolder(OrderViewHolder viewHolder, Request model, int position) {
                viewHolder.orderId.setText(new StringBuilder("Order ID: ").append(adapter.getRef(position).getKey()));
                viewHolder.orderStatus.setText(new StringBuilder("Status: ").append(convertStatus(model.getStatus())));
                viewHolder.orderPhone.setText(new StringBuilder("Phone: ").append(model.getPhone()));
                viewHolder.orderAddress.setText(new StringBuilder("Address: ").append(model.getAddress()));
            }
        };
        recyclerView.setAdapter(adapter);
    }

    private String convertStatus(String status) {

        if(status.equals("0"))
            return "Placed";
        else if(status.equals("1"))
            return "On my way";
        else
            return  "Shipped";
    }
}
