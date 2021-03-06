package com.example.diana.dreamcakes;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.andremion.counterfab.CounterFab;
import com.example.diana.dreamcakes.Common.Common;
import com.example.diana.dreamcakes.Database.Database;
import com.example.diana.dreamcakes.Interface.ItemClickListener;
import com.example.diana.dreamcakes.Model.Category;
import com.example.diana.dreamcakes.Model.Token;
import com.example.diana.dreamcakes.ViewHolder.CategoryViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import io.paperdb.Paper;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    FirebaseDatabase database;
    DatabaseReference category;
    TextView txtName;

    RecyclerView recycler_menu;
    RecyclerView.LayoutManager layoutManager;
    FirebaseRecyclerAdapter<Category, CategoryViewHolder> adapter;

    CounterFab fab;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Menu");
        setSupportActionBar(toolbar);

        //init Firebase
        database=FirebaseDatabase.getInstance();
        category=database.getReference().child("Category");

        fab = (CounterFab) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Home.this, Cart.class);
                startActivity(intent);
            }
        });


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Paper.init(this);

        recycler_menu=(RecyclerView)findViewById(R.id.recycler_menu);
        recycler_menu.setHasFixedSize(true);
        layoutManager=new GridLayoutManager(this,2);
        recycler_menu.setLayoutManager(layoutManager);


        //det name for user
        View headerView=navigationView.getHeaderView(0);
        txtName=(TextView)headerView.findViewById(R.id.user_profile_name);

        loadMenu( );
        updateToken(FirebaseInstanceId.getInstance().getToken());

    }

    private void updateToken(String token) {
        FirebaseDatabase database=FirebaseDatabase.getInstance();
        DatabaseReference tokens=database.getReference("Tokens");
        Token data=new Token(token,false);
        tokens.child(Common.currentUser.getPhone()).setValue(data);
    }


    private void loadMenu() {
        adapter= new FirebaseRecyclerAdapter<Category, CategoryViewHolder>(Category.class,R.layout.category_item_layout,CategoryViewHolder.class,category) {
            @Override
            protected void populateViewHolder(CategoryViewHolder viewHolder, Category model, int position) {
                fab.setCount(new Database(getBaseContext()).getCountCart(Common.currentUser.getPhone()));
               txtName.setText(Common.currentUser.getFullName());
                viewHolder.textMenuName.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getImage()).into(viewHolder.image);
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent cakeList=new Intent(Home.this,CakeList.class);
                        cakeList.putExtra("CategoryId",adapter.getRef(position).getKey());
                        startActivity(cakeList);
                    }
                });

            }
        };

        recycler_menu.setAdapter(adapter);
    }


    @Override
    protected void onResume() {
        super.onResume();
      fab.setCount(new Database(this).getCountCart(Common.currentUser.getPhone()));

    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home_address) {
            showHomeAddress();
        } else if (id == R.id.nav_cart) {
            startActivity(new Intent(Home.this,Cart.class));
        } else if (id == R.id.nav_fav)
        {
            startActivity(new Intent(Home.this,FavoriteList.class));

        } else if (id == R.id.nav_orders) {

            startActivity(new Intent(Home.this,Order.class));

        } else if (id == R.id.nav_logout) {
            singOut();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showHomeAddress() {
        AlertDialog.Builder alertDialog=new AlertDialog.Builder(Home.this);
        alertDialog.setTitle("Change Home Address");
        View view= LayoutInflater.from(this).inflate(R.layout.home_address_layout,null);
        final MaterialEditText address=(MaterialEditText)view.findViewById(R.id.edtHomeAdr);

        alertDialog.setView(view);
        alertDialog.setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                Common.currentUser.setHomeAddress(address.getText().toString());

                FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                FirebaseDatabase.getInstance().getReference("User")
                        .child(currentFirebaseUser.getUid())
                        .setValue(Common.currentUser)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(Home.this, "Update Address Successful", Toast.LENGTH_SHORT);
                            }
                        });

            }
        });
        alertDialog.show();
    }

    private void singOut() {
        Paper.book().destroy();
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Singout")
                .setMessage("Do you really want to sign out?")
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Common.currentUser=null;
                Intent intent=new Intent(Home.this,MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                FirebaseAuth.getInstance().signOut();
                finish();
            }
        });
        AlertDialog dialog=builder.create();
        dialog.show();
    }
}
