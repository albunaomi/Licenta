package com.example.diana.dreamcakes;

import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.example.diana.dreamcakes.Common.Common;
import com.example.diana.dreamcakes.Database.Database;
import com.example.diana.dreamcakes.Interface.ItemClickListener;
import com.example.diana.dreamcakes.Model.Cake;
import com.example.diana.dreamcakes.Model.CartItem;
import com.example.diana.dreamcakes.Model.Favorite;
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

    Database localDB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cake_list);

        database=FirebaseDatabase.getInstance();
        cakeList=database.getReference("Cake");

        localDB=new Database(this);

        recyclerView=(RecyclerView)findViewById(R.id.recycler_cakes);
        recyclerView.setHasFixedSize(true);
        layoutManager=new GridLayoutManager(this,2);
        recyclerView.setLayoutManager(layoutManager);

        if(getIntent()!=null)
            categoryId=getIntent().getStringExtra("CategoryId");
        if(!categoryId.isEmpty()&& categoryId!=null){
            loadCakes(categoryId);
        }

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void loadCakes(String categoryId){
        adapter=new FirebaseRecyclerAdapter<Cake, CakeViewHolder>(Cake.class,R.layout.cake_item,
                CakeViewHolder.class,cakeList.orderByChild("categoryId").equalTo(categoryId)) {
            @Override
            protected void populateViewHolder(final CakeViewHolder viewHolder, final Cake model, final int position) {
                viewHolder.cake_name.setText(model.getName());
                viewHolder.price.setText(model.getPrice());
                Picasso.with(getBaseContext()).load(model.getImage()).into(viewHolder.image);

                if(localDB.isFavoriteCake(adapter.getRef(position).getKey(),Common.currentUser.getPhone())) {
                    viewHolder.fav_img.setImageResource(R.drawable.ic_favorite_black_24dp);
                }

                viewHolder.fav_img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!localDB.isFavoriteCake(adapter.getRef(position).getKey(),Common.currentUser.getPhone())){
                            Favorite f=new Favorite(Common.currentUser.getPhone(),adapter.getRef(position).getKey(),
                                    model.getName(),
                                    model.getImage(),
                                    model.getPrice());
                            localDB.addFavoriteCake(f);
                            viewHolder.fav_img.setImageResource(R.drawable.ic_favorite_black_24dp);
                            Toast.makeText(CakeList.this,""+model.getName()+" was added to Favorites",Toast.LENGTH_SHORT).show();
                        }
                        else{
                            localDB.removeFavoriteCake(adapter.getRef(position).getKey(),Common.currentUser.getPhone());
                            viewHolder.fav_img.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                            Toast.makeText(CakeList.this,""+model.getName()+" was removed to Favorites",Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                viewHolder.cart_img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new Database(getBaseContext()).addItemToCart(new CartItem(
                                Common.currentUser.getPhone(),
                               adapter.getRef(position).getKey(),
                                model.getName(),
                                model.getImage(),
                                model.getPrice(),
                               "1"));
                        Toast.makeText(CakeList.this,"Add To Cart",Toast.LENGTH_SHORT).show();
                    }
                });
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
