package com.tci.consultoria.tcibitacora.Controller;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.tci.consultoria.tcibitacora.Adapter.RecyclerAct;
import com.tci.consultoria.tcibitacora.AlertDialog.AlertUpdate;
import com.tci.consultoria.tcibitacora.Estaticas.RecyclerViewClick;
import com.tci.consultoria.tcibitacora.Estaticas.statics;
import com.tci.consultoria.tcibitacora.Modelos.Actividad;
import com.tci.consultoria.tcibitacora.R;
import com.tci.consultoria.tcibitacora.Singleton.Principal;

import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
    private ArrayList<String> namePhoto;
    private UploadTask uploadTask = null;
    private ArrayList<String> record;
    public static ArrayList<String> imgRUTA;
    private String downloadImageUrl;
    private ProgressBar bar;
    private TextView progres,txtSinActividades;
    int mCartItemCount = 0;
    public boolean connected;
    public static int positionAlert;
    public static ArrayList<String> UID = new ArrayList<>();
    private SwipeRefreshLayout swipeLoad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reporte_actividades);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        init();
        cargaActividades();
        validaInternet();

        swipeLoad.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                cargaActividades();
            }
        });
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

              if(listActividades.size()!=0)  {
                    if(connected) {
                        for(int j=0; j<UID.size(); j++){
                                subirFotoFirebase(j);
                        }
                    }else{
                        Toast.makeText(getApplicationContext(), statics.TOAST_VALIDA_INTERNET, Toast.LENGTH_LONG).show();
                    }
              }else{
                  Toast.makeText(getApplicationContext(), statics.TOAST_VALIDA_DATOS_POR_SUBIR, Toast.LENGTH_LONG).show();
              }
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    public void init(){
        recyclerActividades = findViewById(R.id.recyclerActividades);
        namePhoto= new ArrayList<String>();
        record = new ArrayList<String>();
        imgRUTA = new ArrayList<String>();
        bar = findViewById(R.id.progressBar);
        progres = findViewById(R.id.textView2);
        swipeLoad = findViewById(R.id.swipeLoad);
        txtSinActividades = findViewById(R.id.txtSinActividades);
    }

    public void cargaActividades(){
        p.databaseReference.child("Bitacora")
                .child(EMPRESA)
                .child("actividades").child("usuarios").child(myIMEI).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listActividades.clear();
                UID.clear();
                namePhoto.clear();
                record.clear();
                imgRUTA.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    String auxNoprogramada = snapshot.getKey();
                    if(!auxNoprogramada.equals(statics.NO_PROGRAMADA)){
                        act = snapshot.getValue(Actividad.class);
                        int status = act.getStatus();
                        if(status < 2 && status > 0){
                            listActividades.add(act);
                            UID.add(snapshot.getKey());
                            namePhoto.add(act.getFecha()+act.getHora()+"-"+EMPRESA);
                            record.add(String.valueOf(act.getRecord()));
                            imgRUTA.add(act.getPath());
                        }
                    }else{
                        for (DataSnapshot snapshot1:snapshot.getChildren()){
                            act = snapshot1.getValue(Actividad.class);
                            int status = act.getStatus();
                            if(status < 2 && status > 0){
                            listActividades.add(act);
                            UID.add(snapshot1.getKey());
                            namePhoto.add(act.getFecha()+act.getHora()+"-"+EMPRESA);
                            record.add(String.valueOf(act.getRecord()));
                            imgRUTA.add(act.getPath());
                            }
                        }
                    }
                }
                mCartItemCount = listActividades.size();
                setupBadge();
                if(listActividades.size()!=0) {
                    txtSinActividades.setVisibility(View.GONE);
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
                    txtSinActividades.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        swipeLoad.setRefreshing(false);
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

    public void subirFotoFirebase(final int pos) {
        StorageReference path = p.storageRef.child(EMPRESA+namePhoto.get(pos));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Matrix matrix = new Matrix();
        matrix.postRotate(90.0f);
        byte[] data = new byte[0];
        try{
            Bitmap imageBitmap = BitmapFactory.decodeFile(imgRUTA.get(pos));
            Bitmap rotatedBitmap = Bitmap.createBitmap(imageBitmap, 0, 0, imageBitmap.getWidth(), imageBitmap.getHeight(), matrix, true);
            rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
            data = baos.toByteArray();
        }catch (Exception e){
            Toast.makeText(getApplicationContext(), "La fotografia ya no se encuentra en el celular", Toast.LENGTH_LONG).show();
        }

        uploadTask = path.putBytes(data);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //Toast.makeText(MainActivity.this,"Img guardada en Storage",Toast.LENGTH_SHORT).show();
                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception
                    {
                        if (!task.isSuccessful()) {
                            Toast.makeText(ReporteActividades.this,"Error en obtener ur1:"+task.getException().toString(),Toast.LENGTH_SHORT).show();
                            throw task.getException();
                        }else{
                            downloadImageUrl = p.storageRef.child(EMPRESA+namePhoto.get(pos)).getDownloadUrl().toString();
                        }
                        return p.storageRef.child(EMPRESA+namePhoto.get(pos)).getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task)
                    {
                        if (task.isSuccessful()) {
                            downloadImageUrl = task.getResult().toString();
                            // Toast.makeText(MainActivity.this, "obtenimos la url de firebase correctamente", Toast.LENGTH_SHORT).show();
                            listActividades.get(pos).setUrl(downloadImageUrl);
                            final HashMap<String, Object> productMap = new HashMap<>();

                            productMap.put("url", downloadImageUrl);
                            productMap.put("status", 2);

                            p.databaseReference
                                    .child("Bitacora")
                                    .child(EMPRESA)
                                    .child("actividades")
                                    .child("usuarios")
                                    .child(myIMEI)
                                    .child(UID.get(pos)).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.exists()){
                                        p.databaseReference
                                                .child("Bitacora")
                                                .child(EMPRESA)
                                                .child("actividades")
                                                .child("usuarios")
                                                .child(myIMEI)
                                                .child(UID.get(pos))
                                                .updateChildren(productMap);
                                        //subirQuick(pos);
                                    }else{
                                        p.databaseReference
                                                .child("Bitacora")
                                                .child(EMPRESA)
                                                .child("actividades")
                                                .child("usuarios")
                                                .child(myIMEI)
                                                .child(statics.NO_PROGRAMADA)
                                                .child(UID.get(pos))
                                                .updateChildren(productMap);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                            bar.setVisibility(View.GONE);
                            bar.setProgress(0);
                            progres.setVisibility(View.GONE);
                        }else{
                            Toast.makeText(ReporteActividades.this,"Error en obtener url2: "+task.getException().toString(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ReporteActividades.this, "Error: " + e, Toast.LENGTH_SHORT).show();
                Log.e("Error: ", e.toString());

            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                bar.setVisibility(View.VISIBLE);
                bar.setProgress((int) progress);
                progres.setVisibility(View.VISIBLE);
                DecimalFormat format = new DecimalFormat("#.00");
                progres.setText(format.format(progress)  + " %");
            }
        });
    }

}
