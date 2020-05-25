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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.diana.dreamcakes.Common.Common;
import com.example.diana.dreamcakes.Database.Database;
import com.example.diana.dreamcakes.Helper.RecyclerItemTouchHelper;
import com.example.diana.dreamcakes.Interface.RecyclerItemTouchHelperListener;
import com.example.diana.dreamcakes.Model.CartItem;
import com.example.diana.dreamcakes.Model.Request;
import com.example.diana.dreamcakes.ViewHolder.CartAdapter;
import com.example.diana.dreamcakes.ViewHolder.CartViewHolder;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class Cart extends AppCompatActivity implements RecyclerItemTouchHelperListener {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference databaseReference;

     public TextView totalPrice;
    Button placeOrder;

    List<CartItem>  cartItems=new ArrayList<>();
    CartAdapter adapter;

    RelativeLayout rootLayput;
    public String tPrice;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        database=FirebaseDatabase.getInstance();
        databaseReference=database.getReference("Requests");

        recyclerView=(RecyclerView)findViewById(R.id.recycler_cart);
        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        rootLayput=(RelativeLayout)findViewById(R.id.rootLayout) ;
        ItemTouchHelper.SimpleCallback itemTouchHelperCallBack=new RecyclerItemTouchHelper(0,ItemTouchHelper.LEFT,this);

        totalPrice=(TextView)findViewById(R.id.totalPrice);
        placeOrder=(Button)findViewById(R.id.btn_place_order);

        placeOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialof();
             

            }
        });

        loadCartItems();
    }

    private void showAlertDialof() {
        AlertDialog.Builder alertDialog=new AlertDialog.Builder(Cart.this);
        alertDialog.setTitle("One more step");
        alertDialog.setMessage("Enter your address: ");

        final EditText editText=new EditText(Cart.this);
        LinearLayout.LayoutParams lp =new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        editText.setLayoutParams(lp);
        alertDialog.setView(editText);
        alertDialog.setIcon(R.drawable.cart);
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Request request =new Request(Common.currentUser.getPhone(),
                        Common.currentUser.getFullName(),
                        editText.getText().toString(),
                        tPrice,
                        cartItems);

                databaseReference.child(String.valueOf(System.currentTimeMillis()))
                        .setValue(request);

                new Database(getBaseContext()).cleanCart();
                Toast.makeText(Cart.this,"Order Place",Toast.LENGTH_SHORT).show();
                finish();
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

    private void loadCartItems() {
        cartItems=new Database(this).getCartItems();
        adapter=new CartAdapter(cartItems,this);
        recyclerView.setAdapter(adapter);

        double total=0;
        for(CartItem item:cartItems)
           total+=(Double.parseDouble(item.getPrice())*(Integer.parseInt(item.getQuantity())));
        tPrice= String.valueOf(total);
        totalPrice.setText(new StringBuilder("Total:").append(total));

    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if(viewHolder instanceof CartViewHolder){
            String name=((CartAdapter)recyclerView.getAdapter()).getItem(viewHolder.getAdapterPosition()).getCakeName();
            final CartItem deleteItem=((CartAdapter)recyclerView.getAdapter()).getItem(viewHolder.getAdapterPosition());
            final int deleteIndex=viewHolder.getAdapterPosition();

            adapter.removeItem(deleteIndex);
            new Database(getBaseContext()).removeItemFromCart(deleteItem.getCakeId());

            double total=0;
            List<CartItem> items=new Database(getBaseContext()).getCartItems();
            for(CartItem item:items)
                total+=(Double.parseDouble(item.getPrice())*(Integer.parseInt(item.getQuantity())));
            tPrice= String.valueOf(total);
            totalPrice.setText(new StringBuilder("Total:").append(total));

            Snackbar snackbar=Snackbar.make(rootLayput,name+" removed form cart",Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO",new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    adapter.restoreItem(deleteItem,deleteIndex);
                    new Database(getBaseContext()).addItemToCart(deleteItem);
                    double total=0;
                    List<CartItem> items=new Database(getBaseContext()).getCartItems();
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
