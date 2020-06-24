package com.example.diana.dreamcakes;

import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.diana.dreamcakes.Common.Common;
import com.example.diana.dreamcakes.Interface.ItemClickListener;
import com.example.diana.dreamcakes.Model.CartItem;
import com.example.diana.dreamcakes.Model.Request;
import com.example.diana.dreamcakes.ViewHolder.OrderAdapter;
import com.example.diana.dreamcakes.ViewHolder.OrderViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

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

        loadOrders(Common.currentUser.getPhone());

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    public boolean onSupportNavigateUp(){
        finish();
        return true;
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
            protected void populateViewHolder(OrderViewHolder viewHolder, final Request model, final int position) {
                viewHolder.orderId.setText(new StringBuilder("Order ID: ").append(adapter.getRef(position).getKey()));
                viewHolder.orderStatus.setText(new StringBuilder("Status: ").append(Common.convertNrToStatus(model.getStatus())));
                viewHolder.orderPhone.setText(new StringBuilder("Phone: ").append(model.getPhone()));
                viewHolder.orderAddress.setText(new StringBuilder("Address: ").append(model.getAddress()));
                viewHolder.orderDate.setText(new StringBuilder("Order date: ")
                        .append(Common.getDate(Long.parseLong(adapter.getRef(position).getKey()))));
                viewHolder.deliveryDate.setText(new StringBuilder("Date:")
                        .append(model.getDate()));
                viewHolder.cancelOrder.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(adapter.getItem(position).getStatus().equals("0")){
                            cancelOrder(adapter.getRef(position).getKey());
                        }else
                            Toast.makeText(Order.this,"You cannot cancel this Order",Toast.LENGTH_SHORT).show();
                    }
                });
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        showDialog(model.getOrder());
                    }
                });
            }
        };
        recyclerView.setAdapter(adapter);
    }

    private void showDialog(List<CartItem> order) {
        View view= LayoutInflater.from(getBaseContext()).inflate(R.layout.layout_order_detail,null);

        AlertDialog.Builder alertDialog=new AlertDialog.Builder(this);
        alertDialog.setView(view);

        Button btn_ok=(Button)view.findViewById(R.id.btn_ok);
        RecyclerView recyclerView=(RecyclerView)view.findViewById(R.id.recycler_order_cakes);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        OrderAdapter orderAdapter=new OrderAdapter(order,this);
        recyclerView.setAdapter(orderAdapter);

        final AlertDialog dialog=alertDialog.create();
        dialog.show();
        dialog.getWindow().setGravity(Gravity.CENTER);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private void cancelOrder(final String key) {
        requests.child(key)
                .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(Order.this,new StringBuilder("Order ")
                        .append(key)
                        .append(" has been deleted ").toString(),Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Order.this,e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

}
