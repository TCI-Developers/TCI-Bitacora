package com.tci.consultoria.tcibitacora.Controller;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.tci.consultoria.tcibitacora.Adapter.RecyclerAct;
import com.tci.consultoria.tcibitacora.AlertDialog.AlertUpdate;
import com.tci.consultoria.tcibitacora.Estaticas.RecyclerViewClick;
import com.tci.consultoria.tcibitacora.Estaticas.statics;
import com.tci.consultoria.tcibitacora.Modelos.Actividad;
import com.tci.consultoria.tcibitacora.R;
import com.tci.consultoria.tcibitacora.Singleton.Principal;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static com.tci.consultoria.tcibitacora.MainActivity.EMPRESA;
import static com.tci.consultoria.tcibitacora.MainActivity.myIMEI;

public class ReporteActividades extends AppCompatActivity implements AlertUpdate.DialogListener{
    private RecyclerView recyclerAct;
    Principal p = Principal.getInstance();
    public static ArrayList<Actividad> listActividades = new ArrayList<Actividad>();
    Actividad act = new Actividad();
    private RecyclerAct recyclerActReport;
    private RecyclerView recyclerActividades;
    TextView textCartItemCount;
    private ArrayList<String> ID = new ArrayList<>();
    int mCartItemCount = 0;
    public boolean connected;
    public static int positionAlert;
    public static ArrayList<String> UID = new ArrayList<>();

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

        final MenuItem menuItem = menu.findItem(R.id.btn_upload);

        View actionView = MenuItemCompat.getActionView(menuItem);
        textCartItemCount = (TextView) actionView.findViewById(R.id.cart_badge);

        setupBadge();
        actionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(menuItem);
            }
        });
        return true;
    }

    private void setupBadge() {
        if (textCartItemCount != null) {
            if (mCartItemCount == 0) {
                if (textCartItemCount.getVisibility() != View.GONE) {
                    textCartItemCount.setVisibility(View.GONE);
                }
            } else {
                textCartItemCount.setText(String.valueOf(Math.min(mCartItemCount, 99)));
                if (textCartItemCount.getVisibility() != View.VISIBLE) {
                    textCartItemCount.setVisibility(View.VISIBLE);
                }
            }
        }
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
                        act = snapshot.getValue(Actividad.class);
                        int status = act.getStatus();
                        if(status < 2){
                            listActividades.add(act);
                            UID.add(snapshot.getKey());
                        }
                    }else{
                        for (DataSnapshot snapshot1:snapshot.getChildren()){
                            act = snapshot1.getValue(Actividad.class);
                            int status = act.getStatus();
                            if(status < 2){
                            listActividades.add(act);
                            UID.add(snapshot1.getKey());
                            }
                        }
                    }
                }
                mCartItemCount = listActividades.size();
                setupBadge();
                if(listActividades.size()!=0) {
                    recyclerActReport = new RecyclerAct(listActividades, new RecyclerViewClick() {
                        @Override
                        public void onClick(View v, int position) {
                            UID.get(position);
                            positionAlert = position;
                            showAlertUpdate();
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

    public void showAlertUpdate(){
        AlertUpdate alertUpdate = new AlertUpdate();
        alertUpdate.setCancelable(false);
        alertUpdate.show(getSupportFragmentManager(),"actualizar");
    }

    @Override
    public void getTextDialogFregment(String opcion, String actvidad, Double viativos, String UID, int position) {
        if(opcion.equals(statics.VALIDA_SPINNER)){
            showAlertUpdate();
            Toast.makeText(ReporteActividades.this, statics.VALIDA_ERROR_APINNER, Toast.LENGTH_LONG).show();
        }else if(actvidad.length()==0){
            showAlertUpdate();
            Toast.makeText(ReporteActividades.this, statics.TOAST_ERROR_DESCRIPCION_ALERTDIALOG_ACTUALIZAR, Toast.LENGTH_LONG).show();
        }else if(viativos==0.0){
            showAlertUpdate();
            Toast.makeText(ReporteActividades.this, statics.TOAST_ERROR_VIATICOS_ALERTDIALOG_ACTUALIZAR, Toast.LENGTH_LONG).show();
        }else{
            act.setActRealizada(actvidad);
            act.setFecha(listActividades.get(position).getFecha());
            act.setHora(listActividades.get(position).getHora());
            act.setLatitud(listActividades.get(position).getLatitud());
            act.setLongitud(listActividades.get(position).getLongitud());
            act.setNombre(listActividades.get(position).getNombre());
            act.setOpcion(opcion);
            act.setPath(listActividades.get(position).getPath());
            act.setRazonSocial(listActividades.get(position).getRazonSocial());
            act.setRecord(listActividades.get(position).getRecord());
            act.setStatus(listActividades.get(position).getStatus());
            act.setUrl(listActividades.get(position).getUrl());
            act.setViaticos(viativos);
            act.setPrograming(listActividades.get(position).getPrograming());

            int comprueva = listActividades.get(position).getPrograming();
            if(comprueva != 0) {
                p.databaseReference
                        .child("Bitacora")
                        .child(EMPRESA)
                        .child("actividades")
                        .child("usuarios")
                        .child(myIMEI)
                        .child(UID)
                        .setValue(act);
            }else {
                p.databaseReference
                        .child("Bitacora")
                        .child(EMPRESA)
                        .child("actividades")
                        .child("usuarios")
                        .child(myIMEI)
                        .child(statics.NO_PROGRAMADA)
                        .child(UID)
                        .setValue(act);
            }
        }
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
}
