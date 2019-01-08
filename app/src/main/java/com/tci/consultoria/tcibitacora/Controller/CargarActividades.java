package com.tci.consultoria.tcibitacora.Controller;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import com.tci.consultoria.tcibitacora.Estaticas.statics;
import com.tci.consultoria.tcibitacora.R;

import java.util.Calendar;

public class CargarActividades extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{
    EditText etxt_FechaInicio,etxt_FechaFin;
    int dia,mes,ano;
    String Fecha="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cargar_actividades);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        init();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    public void init(){
        etxt_FechaInicio = findViewById(R.id.etxt_FechaInicio);
        etxt_FechaFin = findViewById(R.id.etxt_FechaFin);
    }
    public void obtenerFechaInicio(View view){
        Calendar calendario = Calendar.getInstance();
        dia = calendario.get(Calendar.DAY_OF_MONTH);
        mes = calendario.get(Calendar.MONTH);
        ano = calendario.get(Calendar.YEAR);
        Fecha = "inicio";
        DatePickerDialog date  = new DatePickerDialog(CargarActividades.this, this, ano,mes+1,dia);
        date.show();
    }
    public void obtenerFechaFin(View view){
        Fecha = "fin";
        DatePickerDialog date  = new DatePickerDialog(CargarActividades.this, this, ano,mes+1,dia);
        date.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        if(Fecha.equals("inicio"))
            etxt_FechaInicio.setText(String.format("%1$04d/%2$02d/%3$02d", year, month, dayOfMonth));
        else if(Fecha.equals("fin"))
            etxt_FechaFin.setText(String.format("%1$04d/%2$02d/%3$02d", year, month, dayOfMonth));
    }
}
