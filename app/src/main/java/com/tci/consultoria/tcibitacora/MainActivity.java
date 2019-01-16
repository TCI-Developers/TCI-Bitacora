package com.tci.consultoria.tcibitacora;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
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
import com.tci.consultoria.tcibitacora.Controller.AgregarActividad;
import com.tci.consultoria.tcibitacora.Controller.CargarActividades;
import com.tci.consultoria.tcibitacora.Controller.ReporteActividades;
import com.tci.consultoria.tcibitacora.Estaticas.statics;
import com.tci.consultoria.tcibitacora.Singleton.Principal;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private TelephonyManager mTelephony;

    CardView card_CargarActividades,card_ReporteActividades,card_AgregarActividad;
    Principal p = Principal.getInstance();
    public static String EMPRESA="";
    public static String myIMEI = "";
    private static final String[] PERMISOS = {
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private static final int REQUEST_CODE = 1;
    private boolean IMEIVALIDO= false;
    public static boolean connected;
    public static String rSOCIAL="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        int leer = ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_PHONE_STATE);
        int leer2 = ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION);
        int leer3 = ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION);
        int leer4 = ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (leer == PackageManager.PERMISSION_DENIED || leer2 == PackageManager.PERMISSION_DENIED || leer3 == PackageManager.PERMISSION_DENIED || leer4 == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(MainActivity.this, PERMISOS, REQUEST_CODE);
        }
        getIMEI();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_tci);

        init();
        validaInternet();
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
        veficaIMEI();
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
        p.databaseReference.child("Bitacora")
                .child(EMPRESA)
                .child("actividades").child("usuarios").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    if(snapshot.getKey().equals(myIMEI)){
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


}
