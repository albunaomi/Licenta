package com.example.diana.serverapp.Common;


import com.example.diana.serverapp.Model.Category;
import com.example.diana.serverapp.Model.User;
import com.example.diana.serverapp.Remote.APIService;
import com.example.diana.serverapp.Remote.RetrofitClient;

import java.util.Calendar;
import java.util.Locale;

public class Common {
    public static User currentUser;

    public static final int PICK_IMAGE_REQUEST=71;
    public static final String UPDATE="Update";
    public static final String DELETE="Delete";
    private static final String BASE_URL="https://fcm.googleapis.com/";

    public  static APIService getFCMClient(){

        return RetrofitClient.getClient(BASE_URL).create(APIService.class);
    }

    public static  String convertNrToStatus(String status){
        if(status.equals("0"))
            return "Placed";
        else if(status.equals("1"))
            return "On my way";
        else
            return  "Shipped";
    }

    public static String getDate(long time)
    {
        Calendar calendar=Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(time);
        StringBuilder date=new StringBuilder(
                android.text.format.DateFormat.format("dd-MM-yyyy HH:mm"
                ,calendar)
                .toString());

        return  date.toString();
    }

}
