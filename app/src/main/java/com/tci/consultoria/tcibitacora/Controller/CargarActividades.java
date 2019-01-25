package com.tci.consultoria.tcibitacora.Controller;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.tci.consultoria.tcibitacora.Adapter.RecyclerAct;
import com.tci.consultoria.tcibitacora.Estaticas.RecyclerViewClick;
import com.tci.consultoria.tcibitacora.Estaticas.statics;
import com.tci.consultoria.tcibitacora.Modelos.ActividadNOProgramada;
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

public class CargarActividades extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{
    EditText etxt_FechaInicio,etxt_FechaFin;
    TextView txt_texto;
    int dia,mes,ano;
    String Fecha="";
    private Date date, date2,date3;
    private RecyclerView recycler_actividades;
    private SimpleDateFormat dateFormat;
    private RecyclerAct recyclerAct;
    private ArrayList<ActividadNOProgramada> listActividades = new ArrayList<ActividadNOProgramada>();
    public static ArrayList<ActividadNOProgramada> listFechaActividades = new ArrayList<ActividadNOProgramada>();
    private ArrayList<String> UID = new ArrayList<>();
    private boolean opcFechaSelected=true;
    Principal p = Principal.getInstance();
    ActividadNOProgramada act = new ActividadNOProgramada();
    LinearLayout ly_fechas;
    SimpleDateFormat dateFormatActual = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    Date dateActual = new Date();
    String fechaActual = dateFormatActual.format(dateActual);
    TextView txtSinActividades;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cargar_actividades);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        init();
        txt_texto.setText(R.string.txt_fecha_actual);
        ly_fechas.setVisibility(View.GONE);
        actividadesDia();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_calendar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.btn_calendar_white:
                if(opcFechaSelected) {
                    item.setIcon(R.drawable.ic_date_range_black_24dp);
                    etxt_FechaInicio.setText("");
                    etxt_FechaFin.setText("");
                    opcFechaSelected = false;
                    txt_texto.setText(R.string.txt_rango);
                    ly_fechas.setVisibility(View.VISIBLE);
                    recycler_actividades.setAdapter(null);
                }else{
                    item.setIcon(R.drawable.ic_date_white);
                    opcFechaSelected = true;
                    ly_fechas.setVisibility(View.GONE);
                    txt_texto.setText(R.string.txt_fecha_actual);
                    actividadesDia();
                }
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
        ly_fechas = findViewById(R.id.ly_fechas);
        txt_texto = findViewById(R.id.txt_texto);
        txtSinActividades = findViewById(R.id.txtSinActividades);
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
        p.databaseReference.child("Bitacora")
                .child(EMPRESA)
                .child("actividades").child("usuarios").child(myIMEI)
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listFechaActividades.clear();
                UID.clear();
                for(DataSnapshot obSnapshot : dataSnapshot.getChildren()){
                    try {
                        ActividadNOProgramada act = obSnapshot.getValue(ActividadNOProgramada.class);

                    String auxNoprogramada = obSnapshot.getKey();
                    if(!auxNoprogramada.equals(statics.NO_PROGRAMADA)){
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
                        if(listFechaActividades.size()!=0) {
                            txtSinActividades.setVisibility(View.GONE);
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
                            txtSinActividades.setVisibility(View.VISIBLE);
                            txtSinActividades.setText(R.string.texto_actividades_rango);
                            recycler_actividades.setAdapter(null);
                        }
                    }
                }catch (Exception e) {
                }

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void actividadesDia(){
        p.databaseReference.child("Bitacora")
                .child(EMPRESA)
                .child("actividades").child("usuarios").child(myIMEI).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listFechaActividades.clear();
                UID.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    try {
                        ActividadNOProgramada act = snapshot.getValue(ActividadNOProgramada.class);

                    String auxNoprogramada = snapshot.getKey();
                    if(!auxNoprogramada.equals(statics.NO_PROGRAMADA)){
                        try {
                            date3 = dateFormat.parse(act.getFecha());
                            date = dateFormat.parse(fechaActual);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        if(date3.equals(date)){
                            listFechaActividades.add(act);
                            UID.add(snapshot.getKey());
                        }
                        if(listFechaActividades.size()!=0) {
                            txtSinActividades.setVisibility(View.GONE);
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
                            txtSinActividades.setVisibility(View.VISIBLE);
                            txtSinActividades.setText(R.string.texto_actividades_dia);
                            recycler_actividades.setAdapter(null);
                        }
                    }
                }catch (Exception e) {

                }
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
            intent.putExtra("RECORD",UID.get(i));
            intent.putExtra("actividad","Nombre de actividad: "+listFechaActividades.get(i).getNombre());
            intent.putExtra("posicion",i);
            startActivity(intent);
    }
}
