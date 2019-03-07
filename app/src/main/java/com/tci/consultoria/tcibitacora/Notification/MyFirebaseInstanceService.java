
package com.tci.consultoria.tcibitacora.Notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.tci.consultoria.tcibitacora.R;
import java.util.Map;

public class MyFirebaseInstanceService extends FirebaseMessagingService {
    private final int NOTIFICATION_ID = 1;
    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.e("Nuevo Token",s);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

            showNotification(remoteMessage.getNotification().getTitle(),remoteMessage.getNotification().getBody(),
                    remoteMessage.getPriority(),remoteMessage.getNotification().getLink().toString());

    }



    private void showNotification(String title, String body,int priority,String link) {
        String notificacion_ID="com.tci.consultoria.tcibitacora";
        NotificationManager nManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder notificacion = new NotificationCompat.Builder(getApplicationContext(), null);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Notificacion";
            String descripcion = "Servicio";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(notificacion_ID, name, importance);
            mChannel.setDescription(descripcion);
            mChannel.enableLights(true);
            mChannel.setSound(defaultSoundUri,null);
            mChannel.setLightColor(Color.BLUE);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 400, 500, 400, 300, 200, 400});
            nManager.createNotificationChannel(mChannel);
            notificacion = new NotificationCompat.Builder(this, notificacion_ID);
        }
        notificacion.setContentTitle(title)
                .setContentText(body)
                .setSubText(link)
                .setSound(defaultSoundUri)
                .setSmallIcon(R.drawable.logo)
                .setPriority(priority)
                //.addAction(R.drawable.logo,"TCI-Consultoria",null)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .build();
        //.setOngoing(true)
        nManager.notify(NOTIFICATION_ID, notificacion.build());
    }
}
