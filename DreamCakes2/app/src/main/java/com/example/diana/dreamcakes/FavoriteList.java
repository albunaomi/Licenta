package com.example.diana.dreamcakes;

import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.RelativeLayout;

import com.example.diana.dreamcakes.Common.Common;
import com.example.diana.dreamcakes.Database.Database;
import com.example.diana.dreamcakes.Helper.RecyclerItemTouchHelper;
import com.example.diana.dreamcakes.Interface.RecyclerItemTouchHelperListener;
import com.example.diana.dreamcakes.Model.Favorite;
import com.example.diana.dreamcakes.ViewHolder.CartAdapter;
import com.example.diana.dreamcakes.ViewHolder.CartViewHolder;
import com.example.diana.dreamcakes.ViewHolder.FavoriteAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class FavoriteList extends AppCompatActivity implements RecyclerItemTouchHelperListener{

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference databaseReference;

    FavoriteAdapter adapter;
    List<Favorite> favoriteItems=new ArrayList<>();

    RelativeLayout rootLayout;
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
        rootLayout=(RelativeLayout)findViewById(R.id.rootLayout) ;

        ItemTouchHelper.SimpleCallback itemTouchHelperCallBack=new RecyclerItemTouchHelper(0,ItemTouchHelper.LEFT,this);
        new ItemTouchHelper(itemTouchHelperCallBack).attachToRecyclerView(recyclerView);
        loadFavoriteCakes();

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    private void loadFavoriteCakes() {
        favoriteItems=new Database(this).getFavoriteCakes(Common.currentUser.getPhone());
        adapter=new FavoriteAdapter(favoriteItems,this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadFavoriteCakes();
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if(viewHolder instanceof FavoriteAdapter.FavoriteViewHolder){
            String name=favoriteItems.get(viewHolder.getAdapterPosition()).getCakeName();
            final Favorite deleteItem=favoriteItems.get(viewHolder.getAdapterPosition());
            final int deleteIndex=viewHolder.getAdapterPosition();

            adapter.removeItem(deleteIndex);
            new Database(getBaseContext()).removeFavoriteCake(deleteItem.getCakeId(), Common.currentUser.getPhone());


            Snackbar snackbar=Snackbar.make(rootLayout,name+" removed form favorite",Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO",new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    adapter.restoreItem(deleteItem,deleteIndex);
                    new Database(getBaseContext()).addFavoriteCake(deleteItem);
                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        }
    }
}
