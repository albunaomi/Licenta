package com.example.diana.dreamcakes;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.diana.dreamcakes.Database.Database;
import com.example.diana.dreamcakes.Model.Favorite;
import com.example.diana.dreamcakes.ViewHolder.FavoriteAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class FavoriteList extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference databaseReference;

    FavoriteAdapter adapter;
    List<Favorite> favoriteItems=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_list);
        database=FirebaseDatabase.getInstance();
        databaseReference=database.getReference("Requests");

        recyclerView=(RecyclerView)findViewById(R.id.recycler_fav);
        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        
        loadFavoriteCakes();
    }

    private void loadFavoriteCakes() {
        favoriteItems=new Database(this).getFavoriteCakes();
        adapter=new FavoriteAdapter(favoriteItems,this);
        recyclerView.setAdapter(adapter);
    }
}
