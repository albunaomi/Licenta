package com.example.diana.dreamcakes;

import android.content.DialogInterface;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.diana.dreamcakes.Common.Common;
import com.example.diana.dreamcakes.Database.Database;
import com.example.diana.dreamcakes.Helper.RecyclerItemTouchHelper;
import com.example.diana.dreamcakes.Interface.RecyclerItemTouchHelperListener;
import com.example.diana.dreamcakes.Model.CartItem;
import com.example.diana.dreamcakes.Model.Notification;
import com.example.diana.dreamcakes.Model.Request;
import com.example.diana.dreamcakes.Model.Response;
import com.example.diana.dreamcakes.Model.Sender;
import com.example.diana.dreamcakes.Model.Token;
import com.example.diana.dreamcakes.Model.User;
import com.example.diana.dreamcakes.Remote.APIService;
import com.example.diana.dreamcakes.ViewHolder.CartAdapter;
import com.example.diana.dreamcakes.ViewHolder.CartViewHolder;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class Cart extends AppCompatActivity implements RecyclerItemTouchHelperListener {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference databaseReference;

    public TextView totalPrice;
    Button placeOrder;

    List<CartItem>  cartItems=new ArrayList<>();
    CartAdapter adapter;

    RelativeLayout rootLayout;
    public String tPrice;

    APIService mService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        mService=Common.getFCMService();

        database=FirebaseDatabase.getInstance();
        databaseReference=database.getReference("Requests");

        recyclerView=(RecyclerView)findViewById(R.id.recycler_cart);
        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        rootLayout=(RelativeLayout)findViewById(R.id.rootLayout) ;

        totalPrice=(TextView)findViewById(R.id.totalPrice);
        placeOrder=(Button)findViewById(R.id.btn_place_order);

        placeOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPlaceOrderClick();
             

            }
        });
        ItemTouchHelper.SimpleCallback itemTouchHelperCallBack=new RecyclerItemTouchHelper(0,ItemTouchHelper.LEFT,this);
        new ItemTouchHelper(itemTouchHelperCallBack).attachToRecyclerView(recyclerView);
        loadCartItems();
    }

    private void onPlaceOrderClick() {
        AlertDialog.Builder alertDialog=new AlertDialog.Builder(Cart.this);
        alertDialog.setTitle("One more step");

       View view=LayoutInflater.from(getBaseContext()).inflate(R.layout.place_order_layout,null);
    final  EditText address=(EditText)view.findViewById(R.id.edt_address);
    //nu imi ia rbtn
        RadioButton rbtn_home=(RadioButton)findViewById(R.id.rbtn_home_address) ;
        RadioButton rbtn_other=(RadioButton)findViewById(R.id.rbtn_other_address) ;
        RadioButton rbtn_cash=(RadioButton)findViewById(R.id.rbtn_cash) ;
        RadioButton rbtn_card=(RadioButton)findViewById(R.id.rbtn_card) ;

        if (rbtn_home!= null){
     rbtn_home.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    address.setText("");
                    address.setHint("Enter your address");
                }
            }
        });}
        if (rbtn_other!= null){
        rbtn_other.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    address.setText("");
                    address.setHint("Enter your adress");
                }
            }
        });}

        alertDialog.setView(view);
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               Request request =new Request(Common.currentUser.getPhone(),
                        Common.currentUser.getFullName(),
                        address.getText().toString(),
                        tPrice,
                        cartItems);

                String order_number=String.valueOf(System.currentTimeMillis());
                databaseReference.child(order_number)
                        .setValue(request);


                new Database(getBaseContext()).cleanCart(Common.currentUser.getPhone());
                sendNotificationOrder(order_number);
                
            }
        }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog=alertDialog.create();
        dialog.show();
    }

    private void sendNotificationOrder(final String order_number) {
        DatabaseReference tokens=FirebaseDatabase.getInstance().getReference("Tokens");
        Query data=tokens.orderByChild("isServerTokens").equalTo(true);
        data.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapShot:dataSnapshot.getChildren()){
                    Token serverToken=postSnapShot.getValue(Token.class);

                    Notification notification=new Notification("Dream Cakes","You have new order "+order_number);
                    Sender content=new Sender(serverToken.getToken(),notification);
                    mService.sendNotification(content)
                            .enqueue(new Callback<Response>() {
                                @Override
                                public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                                    if (response.code() == 200) {
                                        if (response.body().success == 1) {
                                            Toast.makeText(Cart.this, "Thank you, Order Place", Toast.LENGTH_SHORT);
                                            finish();
                                        } else
                                            Toast.makeText(Cart.this, "Failed", Toast.LENGTH_SHORT);

                                    }
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

    private void loadCartItems() {
        cartItems=new Database(this).getCartItems(Common.uphone);
        adapter=new CartAdapter(cartItems,this);
        recyclerView.setAdapter(adapter);

        double total=0;
        for(CartItem item:cartItems)
           total+=(Double.parseDouble(item.getPrice())*(Integer.parseInt(item.getQuantity())));
        tPrice= String.valueOf(total);
        totalPrice.setText(new StringBuilder("Total:").append(total));

    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCartItems();
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if(viewHolder instanceof CartViewHolder){
            String name=((CartAdapter)recyclerView.getAdapter()).getItem(viewHolder.getAdapterPosition()).getCakeName();
            final CartItem deleteItem=((CartAdapter)recyclerView.getAdapter()).getItem(viewHolder.getAdapterPosition());
            final int deleteIndex=viewHolder.getAdapterPosition();

            adapter.removeItem(deleteIndex);
            new Database(getBaseContext()).removeItemFromCart(deleteItem.getCakeId(),Common.currentUser.getPhone());

            double total=0;
            List<CartItem> items=new Database(getBaseContext()).getCartItems(Common.currentUser.getPhone());
            for(CartItem item:items)
                total+=(Double.parseDouble(item.getPrice())*(Integer.parseInt(item.getQuantity())));
            tPrice= String.valueOf(total);
            totalPrice.setText(new StringBuilder("Total:").append(total));

            Snackbar snackbar=Snackbar.make(rootLayout,name+" removed form cart",Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO",new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    adapter.restoreItem(deleteItem,deleteIndex);
                    new Database(getBaseContext()).addItemToCart(deleteItem);
                    double total=0;
                    List<CartItem> items=new Database(getBaseContext()).getCartItems(Common.currentUser.getPhone());
                    for(CartItem item:items)
                        total+=(Double.parseDouble(item.getPrice())*(Integer.parseInt(item.getQuantity())));
                    tPrice= String.valueOf(total);
                    totalPrice.setText(new StringBuilder("Total:").append(total));
                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        }
    }
}
