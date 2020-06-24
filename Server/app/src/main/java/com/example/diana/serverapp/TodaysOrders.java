package com.example.diana.serverapp;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.diana.serverapp.Common.Common;
import com.example.diana.serverapp.Interface.ItemClickListener;
import com.example.diana.serverapp.Model.CartItem;
import com.example.diana.serverapp.Model.Request;
import com.example.diana.serverapp.ViewHolder.OrderAdapter;
import com.example.diana.serverapp.ViewHolder.OrderViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class TodaysOrders extends AppCompatActivity {
    public RecyclerView recyclerView;
    public  RecyclerView.LayoutManager layoutManager;

    FirebaseRecyclerAdapter<Request,OrderViewHolder> adapter;
    FirebaseDatabase database;
    DatabaseReference requests;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todays_orders);
        database=FirebaseDatabase.getInstance();
        requests=database.getReference("Requests");

        recyclerView=(RecyclerView)findViewById(R.id.recycler_todays_orders);
        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        loadOrders();

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
    private void loadOrders() {
        String curentDate=getStringCurrentDate();
        adapter=new FirebaseRecyclerAdapter<Request,OrderViewHolder>(
                Request.class,
                R.layout.order_item,
                OrderViewHolder.class,
                requests.orderByChild("date").equalTo(curentDate)
        ){
            @Override
            protected void populateViewHolder(final OrderViewHolder viewHolder, final Request model, int position) {
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

    private String getStringCurrentDate()
{
    Calendar calendar = Calendar.getInstance();
    String y,m,d;
    d=String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
    m=String.valueOf(calendar.get(Calendar.MONTH)+1);
    y=String.valueOf(calendar.get(Calendar.YEAR));

    return d+"/"+m+"/"+y;
}

}
