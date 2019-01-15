package com.tci.consultoria.tcibitacora.Controller;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.tci.consultoria.tcibitacora.Adapter.RecyclerAct;
import com.tci.consultoria.tcibitacora.Estaticas.RecyclerViewClick;
import com.tci.consultoria.tcibitacora.Estaticas.statics;
import com.tci.consultoria.tcibitacora.Modelos.Actividad;
import com.tci.consultoria.tcibitacora.R;
import com.tci.consultoria.tcibitacora.Singleton.Principal;

import java.util.ArrayList;

import static com.tci.consultoria.tcibitacora.MainActivity.EMPRESA;
import static com.tci.consultoria.tcibitacora.MainActivity.myIMEI;

public class ReporteActividades extends AppCompatActivity {
    private RecyclerView recyclerAct;
    Principal p = Principal.getInstance();
    private ArrayList<Actividad> listActividades = new ArrayList<Actividad>();
    Actividad act = new Actividad();
    private RecyclerAct recyclerActReport;
    private RecyclerView recyclerActividades;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reporte_actividades);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        init();
        cargaActividades();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_upload, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.btn_upload:

                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    public void init(){
        recyclerActividades = findViewById(R.id.recyclerActividades);
    }


    public void cargaActividades(){
        p.databaseReference.child("Bitacora")
                .child(EMPRESA)
                .child("actividades").child("usuarios").child(myIMEI).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listActividades.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    String auxNoprogramada = snapshot.getKey();
                    if(!auxNoprogramada.equals(statics.NO_PROGRAMADA)){
                        Actividad act = snapshot.getValue(Actividad.class);
                        listActividades.add(act);
                    }else{
                        for (DataSnapshot snapshot1:snapshot.getChildren()){
                            Actividad act = snapshot1.getValue(Actividad.class);
                            listActividades.add(act);
                        }
                    }
                }

                if(listActividades.size()!=0) {
                    recyclerActReport = new RecyclerAct(listActividades, new RecyclerViewClick() {
                        @Override
                        public void onClick(View v, int position) {

                        }
                    });
                    recyclerActividades.setAdapter(recyclerActReport);
                    recyclerActividades.setHasFixedSize(true);
                    recyclerActividades.setLayoutManager(new GridLayoutManager(ReporteActividades.this, 1));
                }else{
                    recyclerActividades.setAdapter(null);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}
