package com.example.diana.serverapp;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.diana.serverapp.Common.Common;
import com.example.diana.serverapp.Interface.ItemClickListener;
import com.example.diana.serverapp.Model.CartItem;
import com.example.diana.serverapp.Model.DataMessage;
import com.example.diana.serverapp.Model.Request;
import com.example.diana.serverapp.Model.Response;
import com.example.diana.serverapp.Model.Token;
import com.example.diana.serverapp.Remote.APIService;
import com.example.diana.serverapp.ViewHolder.OrderAdapter;
import com.example.diana.serverapp.ViewHolder.OrderViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jaredrummler.materialspinner.MaterialSpinner;



import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;

public class Order extends AppCompatActivity{

    public RecyclerView recyclerView;
    public  RecyclerView.LayoutManager layoutManager;

    BottomNavigationView bottomNavigationView;

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

        bottomNavigationView=(BottomNavigationView)findViewById(R.id.bottom_nav) ;
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
               if(item.getItemId()==R.id.order_new)
                   loadOrders("0");
               else if(item.getItemId()==R.id.order_processing)
                   loadOrders("1");
                else if(item.getItemId()==R.id.order_shipping)
                    loadOrders("2");
                else if(item.getItemId()==R.id.order_shipped)
                    loadOrders("3");
                return true;
            }
        });
        loadOrders("0");

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
    private void loadOrders(String status) {
        adapter=new FirebaseRecyclerAdapter<Request,OrderViewHolder>(
                Request.class,
                R.layout.order_item,
                OrderViewHolder.class,
                requests.orderByChild("status").equalTo(status)
        ){

            @Override
            protected void populateViewHolder(OrderViewHolder viewHolder, final Request model, int position) {
                viewHolder.orderId.setText(new StringBuilder("Order ID: ").append(adapter.getRef(position).getKey()));
                viewHolder.orderStatus.setText(new StringBuilder("Status: ").append(Common.convertNrToStatus(model.getStatus())));
                viewHolder.orderPhone.setText(new StringBuilder("Phone: ").append(model.getPhone()));
                viewHolder.orderAddress.setText(new StringBuilder("Address: ").append(model.getAddress()));
                viewHolder.orderDate.setText(new StringBuilder("Date: ")
                        .append(Common.getDate(Long.parseLong(adapter.getRef(position).getKey()))));

                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        showDialog(model.getOrder());

                    }
                });

            }
        };
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
    }

    private void showDialog(List<CartItem> order) {
        View view=LayoutInflater.from(getBaseContext()).inflate(R.layout.layout_order_detail,null);

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

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(item.getTitle().equals("Update status"))
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
       spinner.setItems("Placed","Processing","On my way","Shipped");

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

                            Map<String,String>dataSend=new HashMap<>();
                            dataSend.put("title","Dream Cakes");
                            dataSend.put("message","Your order "+key+" was updated");
                            DataMessage dataMessage=new DataMessage(token.getToken(),dataSend);
                            mService.sendNotification(dataMessage)
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
