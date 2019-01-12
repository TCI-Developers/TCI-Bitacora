package com.tci.consultoria.tcibitacora.Controller;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.tci.consultoria.tcibitacora.Adapter.RecyclerAct;
import com.tci.consultoria.tcibitacora.Estaticas.RecyclerViewClick;
import com.tci.consultoria.tcibitacora.Modelos.Actividad;
import com.tci.consultoria.tcibitacora.R;
import com.tci.consultoria.tcibitacora.Singleton.Principal;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static com.tci.consultoria.tcibitacora.MainActivity.EMPRESA;
import static com.tci.consultoria.tcibitacora.MainActivity.myIMEI;
import static java.lang.Thread.sleep;

public class CargarActividades extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{
    EditText etxt_FechaInicio,etxt_FechaFin;
    int dia,mes,ano;
    String Fecha="";
    private Date date, date2,date3;
    private RecyclerView recycler_actividades;
    private SimpleDateFormat dateFormat;
    private RecyclerAct recyclerAct;
    private ArrayList<Actividad> listActividades = new ArrayList<Actividad>();
    public static ArrayList<Actividad> listFechaActividades = new ArrayList<Actividad>();
    private ArrayList<String> UID = new ArrayList<>();

    Principal p = Principal.getInstance();
    Actividad act = new Actividad();

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
        dateFormat = new SimpleDateFormat("yy-MM-dd", Locale.getDefault());
        recycler_actividades = findViewById(R.id.recycler_actividades);
    }
    public void obtenerFechaInicio(View view){
        Calendar calendario = Calendar.getInstance();
        dia = calendario.get(Calendar.DAY_OF_MONTH);
        mes = calendario.get(Calendar.MONTH);
        ano = calendario.get(Calendar.YEAR);
        Fecha = "inicio";
        DatePickerDialog date  = new DatePickerDialog(CargarActividades.this, this, ano,mes,dia);
        date.show();
    }
    public void obtenerFechaFin(View view){
        Fecha = "fin";
        DatePickerDialog date  = new DatePickerDialog(CargarActividades.this, this, ano,mes,dia);
        date.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        if(Fecha.equals("inicio")) {
            etxt_FechaInicio.setText(String.format("%1$04d-%2$02d-%3$02d", year, month + 1, dayOfMonth));
            cargarAgenda();
        }else if(Fecha.equals("fin")) {
            etxt_FechaFin.setText(String.format("%1$04d-%2$02d-%3$02d", year, month + 1, dayOfMonth));
            cargarAgenda();
        }
    }

    public void cargarActividades(final String f1, final String f2){
        View v;
        p.databaseReference.child("Bitacora")
                .child(EMPRESA)
                .child("actividades").child("usuarios").child(myIMEI)
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listFechaActividades.clear();
                UID.clear();
                for(DataSnapshot obSnapshot : dataSnapshot.getChildren()){
                    Actividad act = obSnapshot.getValue(Actividad.class);
                    listActividades.add(act);
                    try {
                        date3 = dateFormat.parse(act.getFecha());
                        date = dateFormat.parse(f1);
                        date2 = dateFormat.parse(f2);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if((date3.after(date)||date3.equals(date)) && (date3.before(date2)||date3.equals(date2))) {
                        listFechaActividades.add(act);
                        UID.add(obSnapshot.getKey());
                    }
                }
                if(listFechaActividades.size()!=0) {
                    recyclerAct = new RecyclerAct(listFechaActividades, new RecyclerViewClick() {
                        @Override
                        public void onClick(View v, int position) {
                            agregarActividad(position);
                        }
                    });

                    recycler_actividades.setAdapter(recyclerAct);
                    recycler_actividades.setHasFixedSize(true);
                    recycler_actividades.setLayoutManager(new GridLayoutManager(CargarActividades.this, 2));
                }else{
                    recycler_actividades.setAdapter(null);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void cargarAgenda(){
        if(etxt_FechaFin.getText().length()!=0&&etxt_FechaInicio.getText().length()!=0){
            final String fechaInicio = etxt_FechaInicio.getText().toString();
            final String fechaFin = etxt_FechaFin.getText().toString();
            cargarActividades(fechaInicio,fechaFin);
        }
    }

    public void agregarActividad(int i){
        Intent intent = new Intent(CargarActividades.this,AgregarActividad.class);
        intent.putExtra("UID",UID.get(i));
        intent.putExtra("actividad","Nombre de actividad: "+listFechaActividades.get(i).getNombre());
        intent.putExtra("posicion",i);
        startActivity(intent);
    }
}
