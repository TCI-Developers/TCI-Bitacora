package com.tci.consultoria.tcibitacora.Background;

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;

import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;
import com.tci.consultoria.tcibitacora.Singleton.Principal;

import java.util.Calendar;
import java.util.HashMap;

import static com.tci.consultoria.tcibitacora.MainActivity.EMPRESA;
import static com.tci.consultoria.tcibitacora.MainActivity.myIMEI;

public class IntentService extends Service {
        private Looper mServiceLooper;
        private ServiceHandler mServiceHandler;
        LocationManager manager;
        Principal p = Principal.getInstance();
        private Double latitud=0.0;
        private Double longitud=0.0;
        private String hora;
    private static final String[] PERMISOS = {
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        private static final int REQUEST_CODE = 1;

        // Handler that receives messages from the thread
        private final class ServiceHandler extends Handler {
            public ServiceHandler(Looper looper) {
                super(looper);
            }

            @Override
            public void handleMessage(Message msg) {
                // Normally we would do some work here, like download a file.
                // For our sample, we just sleep for 5 seconds.
                final HashMap<String, Object> productMap = new HashMap<>();
                productMap.put("lat",latitud );
                productMap.put("lgn", longitud);
                productMap.put("hora", hora);
                try {
                    p.databaseReference
                            .child("Bitacora")
                            .child(EMPRESA)
                            .child("actividades")
                            .child("usuarios")
                            .child(myIMEI).updateChildren(productMap);
                    Thread.sleep(300000);//300000
                } catch (InterruptedException e) {
                    // Restore interrupt status.
                    Thread.currentThread().interrupt();
                }
                stopSelf(msg.arg1);
            }
        }

        @Override
        public void onCreate() {
            // Start up the thread running the service.  Note that we create a
            // separate thread because the service normally runs in the process's
            // main thread, which we don't want to block.  We also make it
            // background priority so CPU-intensive work will not disrupt our UI.
            try {
                HandlerThread thread = new HandlerThread("ServiceStartArguments",
                        Process.THREAD_PRIORITY_BACKGROUND);
                thread.start();
                hora = java.text.DateFormat.getTimeInstance().format(Calendar.getInstance().getTime());
                Mi_hubicacion();
                // Get the HandlerThread's Looper and use it for our Handler
                mServiceLooper = thread.getLooper();
                mServiceHandler = new ServiceHandler(mServiceLooper);
            }catch (Exception e){

            }
        }

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
                //Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();
                // For each start request, send a message to start a job and deliver the
                // start ID so we know which request we're stopping when we finish the job
                Message msg = mServiceHandler.obtainMessage();
                msg.arg1 = startId;
                mServiceHandler.sendMessage(msg);

            // If we get killed, after returning from here, restart
            return START_STICKY;
        }

        @Override
        public IBinder onBind(Intent intent) {
            // We don't provide binding, so return null
            return null;
        }

        @Override
        public void onDestroy() {
            //Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent("com.tci.consultoria.tcibitacora.Background.IntentService");
            sendBroadcast(intent);
        }

    private void Mi_hubicacion() {
        int leer2 = ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION);
        int leer3 = ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION);
        if (leer2 == PackageManager.PERMISSION_DENIED || leer3 == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions((Activity) getApplicationContext(), PERMISOS, REQUEST_CODE);
        }
        manager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        Location local = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        actualizar(local);
    }

    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            actualizar(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    private void actualizar(Location location) {
        if (location != null) {
            latitud = location.getLatitude();
            longitud = location.getLongitude();
        }
//        else{
//            latitud = 19.3980857;
//            longitud = -102.0556112;
//        }
    }
}

