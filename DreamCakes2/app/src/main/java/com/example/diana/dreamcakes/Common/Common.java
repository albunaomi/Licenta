package com.example.diana.dreamcakes.Common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.example.diana.dreamcakes.Model.User;
import com.example.diana.dreamcakes.Remote.APIService;
import com.example.diana.dreamcakes.Remote.RetrofitClient;

import java.util.Calendar;
import java.util.Locale;

public class Common {
    public static User currentUser;


    public static String PHONE_TEXT="userPhone";
    private static final String BASE_URL="https://fcm.googleapis.com/";
    public static final String USER_KEY="User";
    public static final String PWD_KEY="Password";

    public  static APIService getFCMService(){

        return RetrofitClient.getClient(BASE_URL).create(APIService.class);
    }

    public static boolean isConnectedToInternet(Context context)
    {
        ConnectivityManager connectivityManager=(ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if(connectivityManager!=null)
        {
            NetworkInfo[] info=connectivityManager.getAllNetworkInfo();
            for(int i=0;i<info.length;i++)
            {
                if(info[i].getState()==NetworkInfo.State.CONNECTED)
                    return true;
            }
        }
        return  false;
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
