package com.tci.consultoria.tcibitacora.Notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.tci.consultoria.tcibitacora.R;

import java.util.Map;
import java.util.Random;

import static android.support.constraint.Constraints.TAG;

public class MyFirebaseInstanceService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if(remoteMessage.getData().isEmpty())
            showNotification(remoteMessage.getNotification().getTitle(),remoteMessage.getNotification().getBody());
        else
            showNotification(remoteMessage.getData());
    }

    private void showNotification(Map<String,String> data) {
        String title = data.get("title").toString();
        String body = data.get("body").toString();
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String notificacion_ID="com.tci.consultoria.tcibitacora.test";

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel =
                    new NotificationChannel(notificacion_ID,"TCI-Consultoria",NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription("TCI_Channel");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.BLACK);
            notificationChannel.setVibrationPattern(new long[]{0,100,500,1000});
            notificationChannel.enableLights(true);
            notificationManager.createNotificationChannel(notificationChannel);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this,notificacion_ID);

            builder.setAutoCancel(true).setDefaults(Notification.DEFAULT_ALL)
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.mipmap.ic_tci)
                    .setContentText(body)
                    .setContentTitle(title)
                    .setContentInfo("Info");

            notificationManager.notify(new Random().nextInt(),builder.build());
        }
    }

    private void showNotification(String title, String body) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String notificacion_ID="com.tci.consultoria.tcibitacora";

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel =
                    new NotificationChannel(notificacion_ID,"TCI-Consultoria",NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription("TCI_Channel");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.BLACK);
            notificationChannel.setVibrationPattern(new long[]{0,100,500,1000});
            notificationChannel.enableLights(true);
            notificationManager.createNotificationChannel(notificationChannel);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this,notificacion_ID);

            builder.setAutoCancel(true).setDefaults(Notification.DEFAULT_ALL)
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.mipmap.ic_tci)
                    .setContentText(body)
                    .setContentTitle(title)
                    .setContentInfo("Info");

            notificationManager.notify(new Random().nextInt(),builder.build());
        }
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.e(TAG," Token Firebase: "+s);
    }
}
