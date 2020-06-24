package com.example.diana.serverapp.Helper;


import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.net.Uri;
import android.os.Build;

import com.example.diana.serverapp.R;

public class NotificationHelper extends ContextWrapper {
    private static final String NAO_CHANEL_ID="com.example.diana.serverapp.NAO";
    private static final String NAO_CHANEL_NAME="Dream Cakes";

    private NotificationManager manager;
    public NotificationHelper(Context base) {
        super(base);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
            createChannel();
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createChannel() {
        NotificationChannel naoChannel=new NotificationChannel(NAO_CHANEL_ID,NAO_CHANEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT);
        naoChannel.enableLights(false);
        naoChannel.enableVibration(true);
        naoChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        getManager().createNotificationChannel(naoChannel);
    }

   public NotificationManager getManager() {
        if(manager==null)
            manager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        return  manager;
    }

    @TargetApi(Build.VERSION_CODES.O)
    public Notification.Builder getDCChannelNotification(String title, String body, PendingIntent contentIntent, Uri soundUri)
    {
        return new Notification.Builder(getApplicationContext(),NAO_CHANEL_ID)
                .setContentIntent(contentIntent)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setSound(soundUri)
                .setAutoCancel(false);
    }
    @TargetApi(Build.VERSION_CODES.O)
    public Notification.Builder getDCChannelNotification(String title, String body, Uri soundUri)
    {
        return new Notification.Builder(getApplicationContext(),NAO_CHANEL_ID)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setSound(soundUri)
                .setAutoCancel(false);
    }
}
