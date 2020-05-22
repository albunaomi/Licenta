package com.example.diana.dreamcakes;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.diana.dreamcakes.Interface.ItemClickListener;
import com.example.diana.dreamcakes.Model.Cake;
import com.example.diana.dreamcakes.ViewHolder.CakeViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class CakeList extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference cakeList;

    String categoryId="";

    FirebaseRecyclerAdapter<Cake,CakeViewHolder> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cake_list);

        database=FirebaseDatabase.getInstance();
        cakeList=database.getReference("Cake");

        recyclerView=(RecyclerView)findViewById(R.id.recycler_cakes);
        recyclerView.setHasFixedSize(true);
        layoutManager=new GridLayoutManager(this,2);
        recyclerView.setLayoutManager(layoutManager);

        if(getIntent()!=null)
            categoryId=getIntent().getStringExtra("CategoryId");
        if(!categoryId.isEmpty()&& categoryId!=null){
            loadCakes(categoryId);
        }
    }
    private void loadCakes(String categoryId){
        adapter=new FirebaseRecyclerAdapter<Cake, CakeViewHolder>(Cake.class,R.layout.cake_item,
                CakeViewHolder.class,cakeList.orderByChild("CategoryId").equalTo(categoryId)) {
            @Override
            protected void populateViewHolder(CakeViewHolder viewHolder, Cake model, int position) {
                viewHolder.cake_name.setText(model.getName());
                viewHolder.price.setText(model.getPrice());
                Picasso.with(getBaseContext()).load(model.getImage()).into(viewHolder.image);

                final Cake cake=model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                        Intent cakesDetail=new Intent(CakeList.this,CakesDetail.class);
                        cakesDetail.putExtra("CakeId",adapter.getRef(position).getKey());
                        startActivity(cakesDetail);
                    }
                });
            }
        };
        recyclerView.setAdapter(adapter);
    }
}
