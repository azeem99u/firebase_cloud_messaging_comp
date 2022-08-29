package com.example.fcmdemo;

import static androidx.core.app.NotificationCompat.DEFAULT_SOUND;
import static androidx.core.app.NotificationCompat.DEFAULT_VIBRATE;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.media.audiofx.NoiseSuppressor;
import android.net.Uri;
import android.os.IBinder;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MyFCMService extends FirebaseMessagingService {
    private static final String TAG = "mytag";
    Bitmap bitmapImage = null;


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        if (remoteMessage.getNotification() != null) {

            if (remoteMessage.getNotification().getImageUrl() != null){
                bitmapImage = getBitmapFromUrl(remoteMessage.getNotification().getImageUrl().toString());
            }
            showNotification(remoteMessage);
        }

        if (remoteMessage.getData().size() > 0) {

            for (String key:remoteMessage.getData().keySet()){
                Log.d(TAG,"key : "+key+" data: "+remoteMessage.getData().get(key)+"\n");
            }

        }
    }



    private void showNotification(RemoteMessage remoteMessage) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        @SuppressLint("UnspecifiedImmutableFlag")
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
            bigTextStyle.setBigContentTitle(remoteMessage.getNotification().getTitle());
            bigTextStyle.bigText(remoteMessage.getNotification().getBody());

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Notification notification =
                new NotificationCompat.Builder(this, App.CHANNEL_ID)

                        .setSmallIcon(R.drawable.ic_baseline_message_24)
                        .setBadgeIconType(NotificationCompat.BADGE_ICON_LARGE)
                        .setLargeIcon(bitmapImage)
/*
                        .setStyle(bigTextStyle)*/
                        .setStyle(new NotificationCompat.BigPictureStyle()
                                .bigPicture(bitmapImage).bigLargeIcon(null))

                        .setContentTitle(remoteMessage.getNotification().getTitle())
                        .setContentText(remoteMessage.getNotification().getBody())
                        .setAutoCancel(true)
                        .setLights(Color.YELLOW,200,200)
                        .setSound(defaultSoundUri)
                        .setColor(getResources().getColor(R.color.teal_200))
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setVibrate(new long[]{0, 250, 250, 250})
                        .setDefaults(DEFAULT_SOUND | DEFAULT_VIBRATE)
                        .setContentIntent(pendingIntent).build();


        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0,notification);

    }


    private void storeRegIdInPref(String token) {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("mypref", 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("regId", token);
        editor.apply();
    }

    public Bitmap getBitmapFromUrl(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public MyFCMService() {
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.e("NEW_TOKEN", s);
        storeRegIdInPref(s);
        Log.d(TAG, "onNewToken: msdkjfkls");
    }


}