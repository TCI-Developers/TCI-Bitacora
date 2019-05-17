package com.tci.consultoria.tcibitacora.Controller;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
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
import com.tci.consultoria.tcibitacora.Adapter.RecyclerBitacora;
import com.tci.consultoria.tcibitacora.AlertDialog.AlertUpdate;
import com.tci.consultoria.tcibitacora.Estaticas.RecyclerViewClick;
import com.tci.consultoria.tcibitacora.Estaticas.statics;
import com.tci.consultoria.tcibitacora.Modelos.Actividad;
import com.tci.consultoria.tcibitacora.Modelos.Bitacora;
import com.tci.consultoria.tcibitacora.QuickBase.ParseXmlData;
import com.tci.consultoria.tcibitacora.QuickBase.Results;
import com.tci.consultoria.tcibitacora.R;
import com.tci.consultoria.tcibitacora.Singleton.Principal;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.tci.consultoria.tcibitacora.MainActivity.EMPRESA;
import static com.tci.consultoria.tcibitacora.MainActivity.myIMEI;

public class ReporteActividades extends AppCompatActivity implements AlertUpdate.DialogListener{
    Principal p = Principal.getInstance();
    public static ArrayList<Actividad> listActividades;
    Actividad act = new Actividad();
    private RecyclerBitacora recyclerActReport;
    private RecyclerView recyclerActividades;
    TextView textCartItemCount;
    private ArrayList<String> namePhoto;
    private UploadTask uploadTask = null;
    public static ArrayList<String> imgRUTA;
    private String downloadImageUrl;
    private ProgressBar bar;
    private TextView progres,txtSinActividades;
    int mCartItemCount = 0;
    public boolean connected;
    public static int positionAlert;
    public static ArrayList<String> RECORD = new ArrayList<>();
    public static ArrayList<String> UID_BITACORA = new ArrayList<>();
    private SwipeRefreshLayout swipeLoad;
    private int cont = 0;
    public ProgressDialog progressDoalog;
    int contador=1;
    //token TCi Consultoria
    private static final String Tiket = "9_bpqnx8hh8_b2c6pu_fwjc_a_-b_di9hv2qb4t5jbp9jhvu3thpdfdt49mr8dugqz499kgcecg5vb3m_bwg8928";

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
                    progressDoalog.setMessage("Subiendo información....");
                    progressDoalog.setTitle("Por favor espera!");
                    progressDoalog.setCancelable(false);
                    progressDoalog.show();
                    ExecutorService executor = Executors.newSingleThreadExecutor();
                    executor.submit(new Runnable() {
                        @Override
                        public void run() {
                            for (int i = 0; i < RECORD.size(); i++) {
                                for (int j = 0; j < imgRUTA.size(); j++) {
                                    if (!imgRUTA.get(j).isEmpty() && listActividades.get(j).getStatus() != 2) {
                                        subirFotoFirebase(i, j);
                                    } else if (listActividades.get(j).getStatus() != 2) {
                                        subirsinFoto(i, j);
                                        listActividades.get(j).setStatus(2);
                                    }
                                }
                            }
                        }
                    });

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
        namePhoto = new ArrayList<String>();
        imgRUTA = new ArrayList<String>();
        bar = findViewById(R.id.progressBar);
        progres = findViewById(R.id.textView2);
        swipeLoad = findViewById(R.id.swipeLoad);
        txtSinActividades = findViewById(R.id.txtSinActividades);
        listActividades = new ArrayList<>();
        progressDoalog = new ProgressDialog(ReporteActividades.this);
    }
    public void cargaActividades(){
        p.databaseReference.child("Bitacora")
                .child(EMPRESA)
                .child("actividades").child("usuarios").child(myIMEI).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listActividades.clear();
                RECORD.clear();
                namePhoto.clear();
                imgRUTA.clear();
                UID_BITACORA.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    try{
                        for (DataSnapshot snapshot1:snapshot.getChildren()) {
                            Bitacora bitacora =  snapshot1.getValue(Bitacora.class);
                            act = snapshot1.getValue(Actividad.class);
                            int status = act.getStatus();
                            if (status < 2 && status > 0) {
                                listActividades.add(act);
                                RECORD.add(snapshot.getKey());
                                UID_BITACORA.add(snapshot1.getKey());
                                namePhoto.add(bitacora.getFechaRegistro() + "-" + EMPRESA);
                                imgRUTA.add(bitacora.getPath());
                            }
                        }
                    }catch (Exception e) {
                    }
                }
                mCartItemCount = listActividades.size();
                setupBadge();
                if(listActividades.size()!=0) {
                    txtSinActividades.setVisibility(View.GONE);
                    recyclerActReport = new RecyclerBitacora(listActividades, new RecyclerViewClick() {
                        @Override
                        public void onClick(View v, int position) {
                            RECORD.get(position);
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

    public void subirsinFoto(final int posRecord,final int posBitacora){
        final HashMap<String, Object> productMap = new HashMap<>();
        productMap.put("status", 2);
        p.databaseReference
                .child("Bitacora")
                .child(EMPRESA)
                .child("actividades")
                .child("usuarios")
                .child(myIMEI)
                .child(RECORD.get(posRecord)).child(UID_BITACORA.get(posBitacora)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Bitacora bit = new Bitacora();
                try{
                    bit = dataSnapshot.getValue(Bitacora.class);
                }catch (Exception e){}
                if(dataSnapshot.exists() && bit.getStatus()==1){
                    p.databaseReference
                            .child("Bitacora")
                            .child(EMPRESA)
                            .child("actividades")
                            .child("usuarios")
                            .child(myIMEI)
                            .child(RECORD.get(posRecord)).child(UID_BITACORA.get(posBitacora))
                            .updateChildren(productMap);
                    listActividades.get(posBitacora).setStatus(2);
                    uploadQuickBase(posBitacora);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void subirFotoFirebase(final int posRecord,final int posBitacora) {
        final StorageReference path = p.storageRef.child(EMPRESA+"/"+namePhoto.get(posBitacora));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Matrix matrix = new Matrix();
        matrix.postRotate(90.0f);
        byte[] data = new byte[0];
        try{
            Bitmap imageBitmap = BitmapFactory.decodeFile(imgRUTA.get(posBitacora));
            Bitmap rotatedBitmap = Bitmap.createBitmap(imageBitmap, 0, 0, imageBitmap.getWidth(), imageBitmap.getHeight(), matrix, true);
            rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 35, baos);
            data = baos.toByteArray();
        }catch (Exception e){
            //Toast.makeText(getApplicationContext(), statics.ERROR_FOTOGRAFIA, Toast.LENGTH_LONG).show();
        }
        final byte[] finalData = data;
        p.databaseReference
                    .child("Bitacora")
                    .child(EMPRESA)
                    .child("actividades")
                    .child("usuarios")
                    .child(myIMEI)
                    .child(RECORD.get(posRecord)).child(UID_BITACORA.get(posBitacora)).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Bitacora bit = new Bitacora();
                    try{
                        bit = dataSnapshot.getValue(Bitacora.class);
                    }catch (Exception e){}
                    if(dataSnapshot.exists() && bit.getStatus()==1){
                        uploadTask = path.putBytes(finalData);
                        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                    @Override
                                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                        if (!task.isSuccessful()) {
                                            Toast.makeText(ReporteActividades.this,"Error en obtener ur1:"+task.getException().toString(),Toast.LENGTH_SHORT).show();
                                            throw task.getException();
                                        }else{
                                            downloadImageUrl = p.storageRef.child(EMPRESA+"/"+namePhoto.get(posBitacora)).getDownloadUrl().toString();
                                        }
                                        return p.storageRef.child(EMPRESA+"/"+namePhoto.get(posBitacora)).getDownloadUrl();
                                    }
                                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task) {
                                        if (task.isSuccessful()) {
                                            final HashMap<String, Object> productMap = new HashMap<>();
                                            downloadImageUrl = task.getResult().toString();
                                            // Toast.makeText(MainActivity.this, "obtenimos la url de firebase correctamente", Toast.LENGTH_SHORT).show();
                                            listActividades.get(posBitacora).setUrl(downloadImageUrl);
                                            productMap.put("url", downloadImageUrl);
                                            productMap.put("status", 2);
                                                p.databaseReference
                                                        .child("Bitacora")
                                                        .child(EMPRESA)
                                                        .child("actividades")
                                                        .child("usuarios")
                                                        .child(myIMEI)
                                                        .child(RECORD.get(posRecord)).child(UID_BITACORA.get(posBitacora))
                                                        .updateChildren(productMap);
                                                uploadQuickBase(posBitacora);
                                                listActividades.get(posBitacora).setStatus(2);
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
                            public void onFailure(@NonNull Exception e) {ProgressDialog progressDoalog;
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

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
    }

    void uploadQuickBase(int position){
        String Query = "https://aortizdemontellanoarevalo.quickbase.com/db/bnu3r2cfy?a=API_AddRecord"
                +"&_fid_17="+listActividades.get(position).getRecord()+ //Record ID
                "&_fid_9="  +listActividades.get(position).getLatitud()+","+listActividades.get(position).getLongitud()+//Latitud&atLongitud
                "&_fid_18=" +myIMEI+ // Imei
                "&_fid_6="  +listActividades.get(position).getActRealizada()+ //Descripcion de actividad
                "&_fid_7="  +listActividades.get(position).getFechaRegistro()+// Hora de registro
                "&_fid_19=" +listActividades.get(position).getOpcion()+ // Tipo de actividad
                "&_fid_20=" +listActividades.get(position).getViaticos()+// Viaticos consumidos
                "&_fid_23=" +listActividades.get(position).getRazonSocial()+ // Razon social
                "&_fid_24=" +URLEncoder.encode(listActividades.get(position).getUrl())+// URL de Imagen
                "&ticket="  +Tiket+
                "&apptoken=" + statics.tokenTCI;
            Log.e("URL: ",Query);
        try{
            new CargarDatos().execute(Query.replace(" ", "%20"));
        } catch (Exception e){
            Toast.makeText(this, "Error de conexión", Toast.LENGTH_SHORT).show();
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

    @Override
    public void getTextDialogFregment(String opcion, int opcSelect, String actvidad, Double viativos, String Record, String uidBitacora, int position) {
        if(opcion.equals(statics.VALIDA_SPINNER)){
            showAlertUpdate();
            Toast.makeText(ReporteActividades.this, statics.VALIDA_ERROR_APINNER, Toast.LENGTH_LONG).show();
        }else if(actvidad.length()==0){
            showAlertUpdate();
            Toast.makeText(ReporteActividades.this, statics.TOAST_ERROR_DESCRIPCION_ALERTDIALOG_ACTUALIZAR, Toast.LENGTH_LONG).show();
        }else if(viativos<0){
            showAlertUpdate();
            Toast.makeText(ReporteActividades.this, statics.TOAST_ERROR_VIATICOS_ALERTDIALOG_ACTUALIZAR, Toast.LENGTH_LONG).show();
        }else{
            final HashMap<String, Object> productMap = new HashMap<>();

            productMap.put("opcion", opcion);
            productMap.put("selectopc", opcSelect);
            productMap.put("actRealizada", actvidad);
            productMap.put("viaticos", viativos);

                p.databaseReference
                        .child("Bitacora")
                        .child(EMPRESA)
                        .child("actividades")
                        .child("usuarios")
                        .child(myIMEI)
                        .child(Record)
                        .child(uidBitacora)
                        .updateChildren(productMap);
            cargaActividades();
        }
    }


    class CargarDatos extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            try {
                while (true) {
                    return Results.downloadUrl(urls[0]);
                }

            } catch (IOException e) {
                cancel(true);
                return e.getCause().toString();
            }
        }

        @Override
        protected void onPostExecute(String result) {

            String resultado = ParseXmlData.ParseXmlData(result);

            /*Si la variable resultado es distinto a null entonces es por que quickBase
            nos envio una respuesta que xml con mensaje de exito o de algun error generado en la consulta*/
            if (resultado != null) {

                //Si hay error en la carga de datos en quickBase, los datos los mandamos a Hostinger
                if (resultado.equals("No error")) {
                    Log.d("Mensaje del Servidor", resultado);
                    try {
                        if(contador>=imgRUTA.size()){
                            Toast.makeText(getApplicationContext(), "Se subio la informacion correctamente", Toast.LENGTH_LONG).show();
                            progressDoalog.dismiss();
                            cargaActividades();
                            contador=1;
                            //finish();
                        }else{
                            contador++;
                        }

                        //cargaActividades();
                        //finish();

                    } catch (Exception e) {
//                            Toast.makeText(MainActivity.this, "Error al subir", Toast.LENGTH_SHORT).show();
                        System.out.println("error al subir: " + e.getMessage());
                    }
                } else {
                    Log.d("Error de consulta", resultado);
                    progressDoalog.dismiss();
                    Toast.makeText(getApplicationContext(), "Error: "+resultado, Toast.LENGTH_LONG).show();
                }
            } else {
                /**En caso que respuesta sea null es por que fue error de http como los son;
                 * 404,500,403 etc*/
                progressDoalog.dismiss();
                Log.d("Error del Servidor ", result);
                Toast.makeText(getApplicationContext(), "Error: "+result, Toast.LENGTH_LONG).show();
            }
        }
    }




}
