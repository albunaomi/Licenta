package com.example.diana.serverapp;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.diana.serverapp.Common.Common;
import com.example.diana.serverapp.Interface.ItemClickListener;
import com.example.diana.serverapp.Model.Notification;
import com.example.diana.serverapp.Model.Request;
import com.example.diana.serverapp.Model.Response;
import com.example.diana.serverapp.Model.Sender;
import com.example.diana.serverapp.Model.Token;
import com.example.diana.serverapp.Remote.APIService;
import com.example.diana.serverapp.ViewHolder.OrderViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.jaredrummler.materialspinner.MaterialSpinner;

import retrofit2.Call;
import retrofit2.Callback;

public class Order extends AppCompatActivity {

    public RecyclerView recyclerView;
    public  RecyclerView.LayoutManager layoutManager;

    FirebaseRecyclerAdapter<Request,OrderViewHolder> adapter;
    FirebaseDatabase database;
    DatabaseReference requests;

    MaterialSpinner spinner;
    APIService mService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        mService=Common.getFCMClient();
        database=FirebaseDatabase.getInstance();
        requests=database.getReference("Requests");

        recyclerView=(RecyclerView)findViewById(R.id.recycler_orders);
        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        loadOrders();
    }

    private void loadOrders() {
        adapter=new FirebaseRecyclerAdapter<Request,OrderViewHolder>(
                Request.class,
                R.layout.order_item,
                OrderViewHolder.class,
                requests
        ){

            @Override
            protected void populateViewHolder(OrderViewHolder viewHolder, Request model, int position) {
                viewHolder.orderId.setText(new StringBuilder("Order ID: ").append(adapter.getRef(position).getKey()));
                viewHolder.orderStatus.setText(new StringBuilder("Status: ").append(Common.convertNrToStatus(model.getStatus())));
                viewHolder.orderPhone.setText(new StringBuilder("Phone: ").append(model.getPhone()));
                viewHolder.orderAddress.setText(new StringBuilder("Address: ").append(model.getAddress()));
                viewHolder.orderDate.setText(new StringBuilder("Date: ")
                        .append(Common.getDate(Long.parseLong(adapter.getRef(position).getKey()))));

                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                    }
                });

            }
        };
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(item.getTitle().equals(Common.UPDATE))
        {
            updateOrder(adapter.getRef(item.getOrder()).getKey(),adapter.getItem(item.getOrder()));
        }
        else   if(item.getTitle().equals(Common.DELETE))
        {
            deleteOrder(adapter.getRef(item.getOrder()).getKey());
            Toast.makeText(this, "Item deleted!", Toast.LENGTH_SHORT).show();

        }
        return super.onContextItemSelected(item);
    }

    private void deleteOrder(String key){
        requests.child(key).removeValue();
    }
    private void updateOrder(final String key, final Request item) {
        AlertDialog.Builder alertDialog=new AlertDialog.Builder(Order.this);
        alertDialog.setTitle("Update Order");
        alertDialog.setMessage("Choose status");

        View view= LayoutInflater.from(getBaseContext()).inflate(R.layout.update_order,null);

       spinner=(MaterialSpinner)view.findViewById(R.id.status);
       spinner.setItems("Placed","On my way","Shipped");

        alertDialog.setView(view);
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                item.setStatus(String.valueOf(spinner.getSelectedIndex()));

                requests.child(key).setValue(item);
                sendOrderStatusToUser(key,item);

            }
        });
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertDialog.show();

    }

    private void sendOrderStatusToUser(final String key,Request item) {
        DatabaseReference tokens=FirebaseDatabase.getInstance().getReference("Tokens");
        tokens.orderByKey().equalTo(item.getPhone())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot postSnapShot:dataSnapshot.getChildren()) {
                            Token token = postSnapShot.getValue(Token.class);

                            Notification notification=new Notification("Naomi Diana","Your order "+key+" was updated");
                            Sender content=new Sender(token.getToken(),notification);
                            mService.sendNotification(content)
                                    .enqueue(new Callback<Response>() {
                                        @Override
                                        public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                                            if(response.body().success==1)
                                            {
                                                Toast.makeText(Order.this,"Order was updated!",Toast.LENGTH_SHORT);
                                            }
                                            else
                                                Toast.makeText(Order.this,"Order was updated but faild to send notification!",Toast.LENGTH_SHORT);

                                        }

                                        @Override
                                        public void onFailure(Call<Response> call, Throwable t) {
                                            Log.e("ERROR",t.getMessage());

                                        }
                                    });
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }
}
