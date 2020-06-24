package com.example.diana.dreamcakes;

import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.andremion.counterfab.CounterFab;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.diana.dreamcakes.Common.Common;
import com.example.diana.dreamcakes.Database.Database;
import com.example.diana.dreamcakes.Model.Cake;
import com.example.diana.dreamcakes.Model.CartItem;
import com.example.diana.dreamcakes.Model.Favorite;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class CakesDetail extends AppCompatActivity {

    TextView cake_name,cake_price,cake_description;
    ImageView image;
    CollapsingToolbarLayout collapsingToolbarLayout;

    CounterFab btnCart;
    ElegantNumberButton numberButton;

    String cakeId="";

    FirebaseDatabase database;
    DatabaseReference databaseReference;

    Cake cake;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cakes_detail);

        database=FirebaseDatabase.getInstance();
        databaseReference=database.getReference("Cake");

        numberButton=(ElegantNumberButton)findViewById(R.id.number_btn);
        btnCart=(CounterFab) findViewById(R.id.btnCart);

        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Database(getBaseContext()).addItemToCart(new CartItem(
                        Common.currentUser.getPhone(),
                        cakeId,
                        cake.getName(),
                        cake.getImage(),
                        cake.getPrice(),
                        numberButton.getNumber()));

                Toast.makeText(CakesDetail.this,"Add To Cart",Toast.LENGTH_SHORT).show();
                btnCart.setCount(new Database(getBaseContext()).getCountCart(Common.currentUser.getPhone()));
            }
        });

        btnCart.setCount(new Database(this).getCountCart(Common.currentUser.getPhone()));

        cake_description=(TextView)findViewById(R.id.description);
        cake_price=(TextView)findViewById(R.id.cake_price);
        cake_name=(TextView)findViewById(R.id.cake_name);
        image=(ImageView)findViewById(R.id.cake_img);

        collapsingToolbarLayout=(CollapsingToolbarLayout)findViewById(R.id.collapsing);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppbar);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.CollapsedAppbar);

        if(getIntent()!=null)
         cakeId=getIntent().getStringExtra("CakeId");
        if(!cakeId.isEmpty()){
            getDetailCakes(cakeId);
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
        btnCart.setCount(new Database(this).getCountCart(Common.currentUser.getPhone()));

    }
    private void getDetailCakes(String cakeId) {
        databaseReference.child(cakeId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                cake=dataSnapshot.getValue(Cake.class);

                Picasso.with(getBaseContext()).load(cake.getImage()).into(image);

                collapsingToolbarLayout.setTitle(cake.getName());

                cake_price.setText(cake.getPrice());
                cake_name.setText(cake.getName());
                cake_description.setText(cake.getDescription());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
