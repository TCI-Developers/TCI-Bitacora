package com.tci.consultoria.tcibitacora;

import android.Manifest;
import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.tci.consultoria.tcibitacora.Background.IntentService;
import com.tci.consultoria.tcibitacora.Controller.AgregarActividad;
import com.tci.consultoria.tcibitacora.Controller.CargarActividades;
import com.tci.consultoria.tcibitacora.Controller.ReporteActividades;
import com.tci.consultoria.tcibitacora.Estaticas.statics;
import com.tci.consultoria.tcibitacora.Singleton.Principal;

public class MainActivity extends AppCompatActivity {
    private TelephonyManager mTelephony;

    CardView card_CargarActividades,card_ReporteActividades,card_AgregarActividad;
    Principal p = Principal.getInstance();
    public static String EMPRESA="";
    public static String myIMEI = "";
    private static final String[] PERMISOS = {
            Manifest.permission.READ_PHONE_STATE
    };
    private static final int REQUEST_CODE = 1;
    private boolean IMEIVALIDO= false;
    public static boolean connected;
    public static String rSOCIAL="";
    AlertDialog alert = null;
    LocationManager manager;
    private SwipeRefreshLayout swipeLoadImei;
    public static Intent intentservice;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_tci);

        init();
        manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            AlertNoGps();
        }
        validaInternet();
        swipeLoadImei.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                veficaIMEI();
                swipeLoadImei.setRefreshing(false);
            }
        });
    }

    @Override
    protected void onStart() {
        veficaIMEI();
        super.onStart();
    }

    private void AlertNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.Theme_Dialog_Translucent);
        builder.setTitle("GPS")
                .setMessage(statics.ALERT_MESSAGE_GPS_DESACTIVADO)
                .setIcon(R.drawable.ic_location_off)
                .setCancelable(false)
                .setPositiveButton("Activar", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));

                    }
                });
        alert = builder.create();
        alert.show();
        alert.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.RED);
    }

    public void init(){
        card_CargarActividades = findViewById(R.id.card_CargarActividades);
        card_ReporteActividades = findViewById(R.id.card_ReporteActividades);
        card_AgregarActividad = findViewById(R.id.card_AgregarActividad);
        try {
            EMPRESA = p.firebaseAuth.getCurrentUser().getEmail();
        } catch(Exception e){}
        int pos = EMPRESA.indexOf("@");
        EMPRESA = EMPRESA.substring(0,pos);
        razonSocial(EMPRESA);
        swipeLoadImei = findViewById(R.id.swipeLoadImei);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                cerrarSesion();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            AlertDialog.Builder alerta = new AlertDialog.Builder(MainActivity.this);
            alerta.setTitle(statics.TITULO_ALERTA_SALIDA_APP)
            .setMessage(statics.MESSAGE_ALERTA_SALIDA_APP)
                    .setPositiveButton(statics.BUTTON_OK_ALERTA_SALIDA_APP, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finishAffinity();
                }
            }).setNegativeButton(statics.BUTTON_CANCEL_ALERTA_SALIDA_APP, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            }).create().show();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    public void iniciarUbicacionreal(){
            intentservice  = new Intent(this, IntentService.class);
            startService(intentservice);
    }
    public void intentCaragarActividades(View view){
        if(IMEIVALIDO) {
            Intent intent = new Intent(MainActivity.this, CargarActividades.class);
            startActivity(intent);
        }else{
            Snackbar.make(view, "No tienes actividades.", Snackbar.LENGTH_LONG).show();
        }
    }
    public void intentReporteActividades(View view){
        if(IMEIVALIDO){
            Intent intent = new Intent(MainActivity.this,ReporteActividades.class);
            startActivity(intent);
        }else {
            Snackbar.make(view, "No puedes ingresar actividades.", Snackbar.LENGTH_LONG).show();
        }
    }

    public void intentAgregarActividad(final View view){
        if(IMEIVALIDO){
            Intent intent = new Intent(MainActivity.this,AgregarActividad.class);
            startActivity(intent);
        }else {
            Snackbar.make(view, "No puedes ingresar actividades.", Snackbar.LENGTH_LONG).show();
        }
    }

    public String getIMEI(){
        int leer = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
        if (leer == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, PERMISOS, REQUEST_CODE);
        }else{
            mTelephony = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            if (mTelephony.getDeviceId() != null){
                myIMEI = mTelephony.getDeviceId();
            }
        }
        return myIMEI;
    }

    public void cerrarSesion(){
        FirebaseAuth.getInstance().signOut();
        //startActivity(new Intent(MainActivity.this,Login.class));
        finish();
    }

    public void veficaIMEI(){
        getIMEI();
        p.databaseReference.child("Bitacora")
                .child(EMPRESA)
                .child("actividades").child("usuarios").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    if(snapshot.getKey().equals(myIMEI)){
                        if(!IMEIVALIDO){
                            iniciarUbicacionreal();
                        }
                        IMEIVALIDO = true;
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void validaInternet(){
        DatabaseReference connectedRef = p.firebaseDatabase.getInstance().getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                connected = snapshot.getValue(Boolean.class);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Error internet:" + error, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void razonSocial(String empresa){
        switch (empresa){
            case "arfi":
                rSOCIAL = statics.RAZON_SOCIAL_GRUPO_ARFI;
                break;
            case "tci":
                rSOCIAL = statics.RAZON_SOCIAL_TCI;
                break;
        }
    }

    @Override
    public void onTrimMemory(int level) {
        if (level == ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN){

        }
        super.onTrimMemory(level);
    }

    @Override
    protected void onDestroy() {
        stopService(intentservice);
        super.onDestroy();
    }
}
