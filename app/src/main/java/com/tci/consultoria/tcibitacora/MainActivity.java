package com.tci.consultoria.tcibitacora;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.tci.consultoria.tcibitacora.Controller.AgregarActividad;
import com.tci.consultoria.tcibitacora.Controller.CargarActividades;

public class MainActivity extends AppCompatActivity {
    CardView card_CargarActividades,card_ReporteActividades,card_AgregarActividad;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_tci);
        init();

    }
    public void init(){
        card_CargarActividades = findViewById(R.id.card_CargarActividades);
        card_ReporteActividades = findViewById(R.id.card_ReporteActividades);
        card_AgregarActividad = findViewById(R.id.card_AgregarActividad);
    }


    public void intentCaragarActividades(View view){
        Intent intent = new Intent(MainActivity.this,CargarActividades.class);
        startActivity(intent);
    }

    public void intentAgregarActividad(View view){
        Intent intent = new Intent(MainActivity.this,AgregarActividad.class);
        startActivity(intent);
    }
}
