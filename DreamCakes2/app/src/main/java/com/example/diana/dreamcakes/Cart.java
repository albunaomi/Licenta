package com.example.diana.dreamcakes;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.diana.dreamcakes.Common.Common;
import com.example.diana.dreamcakes.Common.Config;
import com.example.diana.dreamcakes.Database.Database;
import com.example.diana.dreamcakes.Helper.RecyclerItemTouchHelper;
import com.example.diana.dreamcakes.Interface.RecyclerItemTouchHelperListener;
import com.example.diana.dreamcakes.Model.CartItem;
import com.example.diana.dreamcakes.Model.DataMessage;
import com.example.diana.dreamcakes.Model.Request;
import com.example.diana.dreamcakes.Model.Response;
import com.example.diana.dreamcakes.Model.Token;
import com.example.diana.dreamcakes.Remote.APIService;
import com.example.diana.dreamcakes.ViewHolder.CartAdapter;
import com.example.diana.dreamcakes.ViewHolder.CartViewHolder;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.joda.time.DateTime;
import org.joda.time.Weeks;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;

public class Cart extends AppCompatActivity implements RecyclerItemTouchHelperListener, DatePickerDialog.OnDateSetListener {

    private static final int PAYPAL_REQUEST_CODE = 9999;
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

    static PayPalConfiguration configuration=new PayPalConfiguration()
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
            .clientId(Config.PAYPAL_CLIENT_ID);

    EditText date,address;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        mService=Common.getFCMService();

        Intent intent=new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION,configuration);
        startService(intent);

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

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    private void onPlaceOrderClick() {
        AlertDialog.Builder alertDialog=new AlertDialog.Builder(Cart.this);
        alertDialog.setTitle("One more step");

       View view=LayoutInflater.from(getBaseContext()).inflate(R.layout.place_order_layout,null);
        address=(EditText)view.findViewById(R.id.edt_address);
        date=(EditText)view.findViewById(R.id.edt_date);
        RadioButton rbtn_home=(RadioButton)view.findViewById(R.id.rbtn_home_address) ;
        RadioButton rbtn_other=(RadioButton)view.findViewById(R.id.rbtn_other_address) ;
        final RadioButton rbtn_cash=(RadioButton)view.findViewById(R.id.rbtn_cash) ;
        final RadioButton rbtn_payPal=(RadioButton)view.findViewById(R.id.rbtn_paypal) ;



        rbtn_other.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    address.setText("");
                    address.setHint("Enter your adress");
                }
            }
        });
        rbtn_home.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    if( Common.currentUser.getHomeAddress()!=null||
                            !TextUtils.isEmpty(Common.currentUser.getHomeAddress()))
                        address.setText(Common.currentUser.getHomeAddress());
                    else
                        Toast.makeText(getApplicationContext(), "Please update your Home Address", Toast.LENGTH_SHORT);


                }
            }
        });

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               showDatePicker();
            }
        });

        alertDialog.setView(view);
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(address.getText().equals(""))
                {
                    Toast.makeText(getBaseContext(), "Please enter address or Select option address", Toast.LENGTH_SHORT);
                }

                if(!rbtn_cash.isChecked()&&!rbtn_payPal.isChecked())
                {
                    Toast.makeText(getBaseContext(),"Please select Payment option",Toast.LENGTH_SHORT).show();
                    return;
                }else if(rbtn_payPal.isChecked())
                {
                    PayPalPayment payPalPayment = new PayPalPayment(new BigDecimal(convertRONInEUR(tPrice)),
                            "EUR",
                            "Dream Cakes Order",
                            PayPalPayment.PAYMENT_INTENT_SALE);

                    Intent intent = new Intent(getApplicationContext(), PaymentActivity.class);
                    intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, configuration);
                    intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payPalPayment);
                    startActivityForResult(intent, PAYPAL_REQUEST_CODE);
                }else if(rbtn_cash.isChecked()){
                    Request request =new Request(Common.currentUser.getPhone(),
                            Common.currentUser.getFullName(),
                            address.getText().toString(),
                            tPrice,
                            "COD",
                            date.getText().toString(),
                            "Unpaid",
                            cartItems);

                    String order_number=String.valueOf(System.currentTimeMillis());
                    databaseReference.child(order_number)
                            .setValue(request);


                    new Database(getBaseContext()).cleanCart(Common.currentUser.getPhone());
                    sendNotificationOrder(order_number);
                    Toast.makeText(Cart.this,"Order place",Toast.LENGTH_SHORT).show();
                    finish();

                }
                
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

    private String convertRONInEUR(String pret){
        double euro=Double.parseDouble(pret)*(0.21);
        return String.valueOf(euro);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==PAYPAL_REQUEST_CODE)
        {
            if(resultCode==RESULT_OK)
            {
                PaymentConfirmation config=data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if(config!=null)
                {
                    try{
                        String paymentDetail=config.toJSONObject().toString(4);
                        JSONObject jsonObject=new JSONObject(paymentDetail);
                        Request request =new Request(Common.currentUser.getPhone(),
                                Common.currentUser.getFullName(),
                                address.getText().toString(),
                                tPrice,
                                "PayPal",
                                date.getText().toString(),
                                jsonObject.getJSONObject("response").getString("state"),
                                cartItems);

                        String order_number=String.valueOf(System.currentTimeMillis());
                        databaseReference.child(order_number)
                                .setValue(request);


                        new Database(getBaseContext()).cleanCart(Common.currentUser.getPhone());
                        sendNotificationOrder(order_number);
                       Toast.makeText(this,"Order place",Toast.LENGTH_SHORT).show();
                        finish();

                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                }
            }else if(resultCode== Activity.RESULT_CANCELED)
                Toast.makeText(this,"Payment cancel",Toast.LENGTH_SHORT).show();
            else if(resultCode==PaymentActivity.RESULT_EXTRAS_INVALID)
                Toast.makeText(this,"Invalid payment",Toast.LENGTH_SHORT).show();
        }
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
       int year = calendar.get(Calendar.YEAR);
       int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(Cart.this,year,month,day);
        datePickerDialog.setThemeDark(false);
        datePickerDialog.showYearPickerFirst(false);
        datePickerDialog.setTitle("Date Picker");

        //min date
        Calendar minDate=Calendar.getInstance();;
        minDate.set(year,month,day+2);
        datePickerDialog.setMinDate(minDate);

        //max date
        Calendar maxDate=Calendar.getInstance();;
        maxDate.set(Calendar.MONTH, month+1);
        datePickerDialog.setMaxDate(maxDate);

        //disable sundays
        Calendar sunday;
        List<Calendar> weekends = new ArrayList<>();

        DateTime date1=new DateTime(minDate);
        DateTime date2=new DateTime(maxDate);
        int weeks =  Weeks.weeksBetween(date1, date2).getWeeks()+1;

        for (int i = 0; i < (weeks * 7) ; i = i + 7) {
            sunday = Calendar.getInstance();
            sunday.add(Calendar.DAY_OF_YEAR, (Calendar.SUNDAY - sunday.get(Calendar.DAY_OF_WEEK) + 7 + i));
            weekends.add(sunday);
        }
        Calendar[] disabledDays = weekends.toArray(new Calendar[weekends.size()]);
        datePickerDialog.setDisabledDays(disabledDays);

        datePickerDialog.show(getFragmentManager(),"Date picker");
    }

    private void sendNotificationOrder(final String order_number) {
        DatabaseReference tokens=FirebaseDatabase.getInstance().getReference("Tokens");
        Query data=tokens.orderByChild("serverToken").equalTo(true);
        data.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapShot:dataSnapshot.getChildren()){
                    Token serverToken=postSnapShot.getValue(Token.class);

                    Map<String,String> dataSend=new HashMap<>();
                    dataSend.put("title","Dream Cakes");
                    dataSend.put("message","You have new order "+order_number);
                    DataMessage dataMessage=new DataMessage(serverToken.getToken(),dataSend);

                    String test=new Gson().toJson(dataMessage);
                    Log.d("Content",test);
                    mService.sendNotification(dataMessage)
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
        cartItems=new Database(this).getCartItems(Common.currentUser.getPhone());
        adapter=new CartAdapter(cartItems,this);
        recyclerView.setAdapter(adapter);

        double total=getTotalPrice();
        tPrice= String.valueOf(total);
        totalPrice.setText(new StringBuilder("Total:").append(total));

    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCartItems();
    }
    public double getTotalPrice( )
    {
        double total=0;
        List<CartItem> items=new Database(getBaseContext()).getCartItems(Common.currentUser.getPhone());
        for(CartItem item:items)
            total+=(Double.parseDouble(item.getPrice())*(Integer.parseInt(item.getQuantity())));
        return total;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if(viewHolder instanceof CartViewHolder){
            String name=((CartAdapter)recyclerView.getAdapter()).getItem(viewHolder.getAdapterPosition()).getCakeName();
            final CartItem deleteItem=((CartAdapter)recyclerView.getAdapter()).getItem(viewHolder.getAdapterPosition());
            final int deleteIndex=viewHolder.getAdapterPosition();

            adapter.removeItem(deleteIndex);
            new Database(getBaseContext()).removeItemFromCart(deleteItem.getCakeId(),Common.currentUser.getPhone());

             double total=getTotalPrice();

            tPrice= String.valueOf(total);
            totalPrice.setText(new StringBuilder("Total:").append(total));

            Snackbar snackbar=Snackbar.make(rootLayout,name+" removed form cart",Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO",new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    adapter.restoreItem(deleteItem,deleteIndex);
                    new Database(getBaseContext()).addItemToCart(deleteItem);
                    double t=getTotalPrice();
                    tPrice= String.valueOf(t);
                    totalPrice.setText(new StringBuilder("Total:").append(t));
                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        }
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String d = dayOfMonth+"/"+(monthOfYear+1)+"/"+year;
        date.setText(d);
    }
}
