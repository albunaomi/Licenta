package com.example.diana.dreamcakes.Common;

import com.example.diana.dreamcakes.Model.User;
import com.example.diana.dreamcakes.Remote.APIService;
import com.example.diana.dreamcakes.Remote.RetrofitClient;

import java.util.Calendar;
import java.util.Locale;

public class Common {
    public static User currentUser;
    public static String uphone;
    public static String name;

    private static final String BASE_URL="https://fcm.googleapis.com/";
    public static final String USER_KEY="User";
    public static final String PWD_KEY="Password";

    public  static APIService getFCMService(){

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
