package com.tci.consultoria.tcibitacora.Controller;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.tci.consultoria.tcibitacora.Adapter.SpinnerOpc;
import com.tci.consultoria.tcibitacora.Estaticas.statics;
import com.tci.consultoria.tcibitacora.Modelos.ActividadNOProgramada;
import com.tci.consultoria.tcibitacora.Modelos.Bitacora;
import com.tci.consultoria.tcibitacora.Modelos.opciones;
import com.tci.consultoria.tcibitacora.QuickBase.ParseXmlData;
import com.tci.consultoria.tcibitacora.QuickBase.Results;
import com.tci.consultoria.tcibitacora.R;
import com.tci.consultoria.tcibitacora.Singleton.Principal;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import uk.co.senab.photoview.PhotoViewAttacher;

import static com.tci.consultoria.tcibitacora.Controller.CargarActividades.listFechaActividades;
import static com.tci.consultoria.tcibitacora.MainActivity.EMPRESA;
import static com.tci.consultoria.tcibitacora.MainActivity.connected;
import static com.tci.consultoria.tcibitacora.MainActivity.myIMEI;
import static com.tci.consultoria.tcibitacora.MainActivity.rSOCIAL;

public class AgregarActividad extends AppCompatActivity {
    ImageView imgPhoto;
    Spinner spnOpcion;
    TextView txtActividad,txtProgress;
    ProgressBar bar;
    SpinnerOpc spinnerOpc;
    PhotoViewAttacher photoViewAttacher = null;
    static final int REQUEST_TAKE_PHOTO = 1;
    private List<opciones> listaOpciones = new ArrayList<opciones>();
    ArrayAdapter<opciones> arrayAdapterOpciones;
    String nombreActividad="";
    private String downloadImageUrl;
    EditText txtActividadRealizada,txtViaticos,txtNombreActividad;
    Principal p = Principal.getInstance();
    public static String mCurrentPhotoPath;
    LocationManager manager;
    UploadTask uploadTask = null;
    private Double latitud=0.0;
    private Double longitud=0.0;
    ActividadNOProgramada act = new ActividadNOProgramada();
    Bitacora bitacora = new Bitacora();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    Date date = new Date();
    String fecha = dateFormat.format(date);
    String RECORD;
    String hora = java.text.DateFormat.getTimeInstance().format(Calendar.getInstance().getTime());
    String namePhoto = fecha+hora+"-"+EMPRESA;
    int pos;
    private static final String[] PERMISOS = {
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private String record,latLong,actRealizada,horaquick,opcCatalogo,viaticos,rSocial,Url;
    private static final int REQUEST_CODE = 1;
    private static final String token = "b67cthncniw7b9bnuc4d4dh5hu6s";//token TCi Consultoria
    private static final String Tiket = "9_bpqnx8hh8_b2c6pu_fwjc_a_-b_di9hv2qb4t5jbp9jhvu3thpdfdt49mr8dugqz499kgcecg5vb3m_bwg8928";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_actividad);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        init();
        Mi_hubicacion();
        imgPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                            zoom(true);
                    }
                },500);
            }
        });
        txtActividad.setText(nombreActividad);
        if(RECORD !=null){
            txtNombreActividad.setVisibility(View.GONE);
        }else{
            txtNombreActividad.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.btn_foto:
                dispatchTakePictureIntent();
                break;
            case R.id.btn_agregar:
                validaFormulario();
                break;
            case R.id.btn_ubicacion:
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?daddr="+"19.4114375"+","+"-102.0719253")); //o la direccion/consulta que quiera "http://maps.google.com/maps?q="+ myLatitude  +"," + myLongitude +"("+ labLocation + ")&iwloc=A&hl=es"
                intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                startActivity(intent);
                break;

            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
         getMenuInflater().inflate(R.menu.menu_actividad, menu);
         if(EMPRESA.equals("arfi"))
             menu.getItem(0).setVisible(true);
         else
             menu.getItem(0).setVisible(false);
        return true;
    }

    public void init(){
        imgPhoto = findViewById(R.id.imgPhoto);
        photoViewAttacher = new PhotoViewAttacher(imgPhoto);
        spnOpcion = findViewById(R.id.spnOpcion);
        llenarSpinner();
        txtActividad = findViewById(R.id.txtActividad);
        try{
            nombreActividad = getIntent().getExtras().getString("actividad");
            pos = getIntent().getExtras().getInt("posicion");
            RECORD = getIntent().getExtras().getString("RECORD");

        }catch (Exception e){
            txtActividad.setVisibility(View.GONE);
        }
        txtActividadRealizada = findViewById(R.id.txtActividadRealizada);
        txtViaticos = findViewById(R.id.txtViaticos);
        txtProgress = findViewById(R.id.txtProgress);
        txtNombreActividad = findViewById(R.id.txtNombreActividad);
        bar = findViewById(R.id.progSubida);
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = EMPRESA + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, "com.tci.consultoria.tcibitacora", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            try {
                zoom(false);
                mostrarPhoto();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void mostrarPhoto() throws IOException {
        Glide.with(AgregarActividad.this)
                .load(mCurrentPhotoPath)
                .into(imgPhoto);
    }

    public void zoom(Boolean ban){
        photoViewAttacher.setZoomable(ban);
    }

    public void llenarSpinner(){

        p.databaseReference.child("Bitacora")
                .child(EMPRESA)
                .child("actividades").child("opciones")
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listaOpciones.clear();
                opciones auxopc = new opciones();
                auxopc.setOpcion("Selecciona Opcion");
                listaOpciones.add(auxopc);
                for(DataSnapshot objSnapshot : dataSnapshot.getChildren()){
                    opciones opc = objSnapshot.getValue(opciones.class);
                    listaOpciones.add(opc);
                }
                spinnerOpc = new SpinnerOpc((ArrayList<opciones>) listaOpciones,AgregarActividad.this);
                spnOpcion.setAdapter(spinnerOpc);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void validaFormulario(){
        if(spnOpcion.getSelectedItem().toString().equals(statics.VALIDA_SPINNER)){
            Toast.makeText(AgregarActividad.this, statics.VALIDA_ERROR_APINNER, Toast.LENGTH_LONG).show();
        }else if(txtNombreActividad.getVisibility() != View.GONE && txtNombreActividad.getText().length()==0){
            txtNombreActividad.setError(statics.VALIDA_ERROR_NOMBRE_ACTIVIDAD);
        }else if(txtActividadRealizada.getText().length()==0){
            txtActividadRealizada.setError(statics.VALIDA_ERROR_ACT_REALIZADA);
        }else if(txtViaticos.getText().length()==0){
            txtViaticos.setError(statics.VALIDA_ERROR_VIATICOS);
        }else if(mCurrentPhotoPath==null){
            Toast.makeText(AgregarActividad.this, statics.VALIDA_ERROR_FOTO, Toast.LENGTH_LONG).show();
        }else{
            guardarDatos();
            mCurrentPhotoPath = null;
        }
    }
    String UUID = null;
    public void guardarDatos(){
        if(connected){
            statusfirebase(1);
            //subirFirebase();
            Toast.makeText(AgregarActividad.this,"Datos listo para subir a Quickbase.",Toast.LENGTH_LONG).show();
        }else{
            statusfirebase(1);
            Toast.makeText(AgregarActividad.this,"No tienes internet, pero tus datos se han guardado localmente",Toast.LENGTH_LONG).show();
            finish();
        }
    }

    public void statusfirebase(int status){
        UUID = java.util.UUID.randomUUID().toString();
        act.setActRealizada(txtActividadRealizada.getText().toString());
        actRealizada = txtActividadRealizada.getText().toString();
        act.setHora(hora);
        horaquick = hora;
        act.setLatitud(latitud);
        act.setLongitud(longitud);
        latLong = latitud+","+longitud;
        act.setOpcion(spnOpcion.getSelectedItem().toString());
        opcCatalogo = spnOpcion.getSelectedItem().toString();
        act.setPath(mCurrentPhotoPath);
        act.setSelectopc(spnOpcion.getSelectedItemPosition());
        act.setFechaRegistro(fecha+" "+horaquick);
        act.setStatus(status);
        act.setUrl("");
        act.setViaticos(Double.parseDouble(txtViaticos.getText().toString()));
        viaticos = txtViaticos.getText().toString();
        if(RECORD != null){
            //act.setPrograming(1);
            record = listFechaActividades.get(pos).getRecord();
            bitacora.setActRealizada(actRealizada);
            bitacora.setFechaRegistro(fecha+" "+horaquick);
            bitacora.setLatitud(latitud);
            bitacora.setLongitud(longitud);
            bitacora.setOpcion(spnOpcion.getSelectedItem().toString());
            bitacora.setRazonSocial(listFechaActividades.get(pos).getRazonSocial());
            rSocial = listFechaActividades.get(pos).getRazonSocial();
            bitacora.setPath(mCurrentPhotoPath);
            bitacora.setSelectopc(spnOpcion.getSelectedItemPosition());
            bitacora.setStatus(status);
            bitacora.setUrl("");
            bitacora.setViaticos(Double.parseDouble(txtViaticos.getText().toString()));
            bitacora.setRecord(listFechaActividades.get(pos).getRecord());
            bitacora.setNombre(listFechaActividades.get(pos).getNombre());
            bitacora.setCliente(listFechaActividades.get(pos).getCliente());
            p.databaseReference
                    .child("Bitacora")
                    .child(EMPRESA)
                    .child("actividades")
                    .child("usuarios")
                    .child(myIMEI)
                    .child(RECORD).child(UUID)
                    .setValue(bitacora);
        }else{
            record = "1";
            act.setRecord("1");
            act.setFecha(fecha);
            act.setRazonSocial(rSOCIAL);
            rSocial = rSOCIAL;
            //act.setPrograming(0);
            act.setNombre(txtNombreActividad.getText().toString());
            p.databaseReference
                    .child("Bitacora")
                    .child(EMPRESA)
                    .child("actividades")
                    .child("usuarios")
                    .child(myIMEI)
                    .child(statics.NO_PROGRAMADA)
                    .child(UUID)
                    .setValue(act);
        }
        finish();
    }

    public void subirFirebase() {
        StorageReference path = p.storageRef.child(EMPRESA+"/"+namePhoto);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Matrix matrix = new Matrix();
        matrix.postRotate(90.0f);
        Bitmap imageBitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);
        Bitmap rotatedBitmap = Bitmap.createBitmap(imageBitmap, 0, 0, imageBitmap.getWidth(), imageBitmap.getHeight(), matrix, true);
        rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] data = baos.toByteArray();

        uploadTask = path.putBytes(data);
        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                bar.setVisibility(View.VISIBLE);
                txtProgress.setVisibility(View.VISIBLE);

                bar.setProgress((int) progress);
                DecimalFormat format = new DecimalFormat("#.00");
                txtProgress.setText(format.format(progress)  + " %");
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                Toast.makeText(register.this,"Img guardada en Storage",Toast.LENGTH_SHORT).show();

                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()){
                            throw task.getException();
                        }
                        downloadImageUrl = p.storageRef.child(EMPRESA+"/"+namePhoto).getDownloadUrl().toString();
                        return p.storageRef.child(EMPRESA+"/"+namePhoto).getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task)
                    {
                        if (task.isSuccessful())
                        {
                            downloadImageUrl = task.getResult().toString();
                            if(RECORD != null){
                                bitacora.setUrl(downloadImageUrl);
                            p.databaseReference
                                    .child("Bitacora")
                                    .child(EMPRESA)
                                    .child("actividades")
                                    .child("usuarios")
                                    .child(myIMEI)
                                    .child(RECORD)
                                    .child(UUID)
                                    .setValue(bitacora);
                                //uploadQuickBase();
                            }else{
                                act.setUrl(downloadImageUrl);
                                p.databaseReference
                                        .child("Bitacora")
                                        .child(EMPRESA)
                                        .child("actividades")
                                        .child("usuarios")
                                        .child(myIMEI)
                                        .child(statics.NO_PROGRAMADA)
                                        .child(UUID)
                                        .setValue(act);
                                //uploadQuickBase();
                            }
                            bar.setVisibility(View.GONE);
                            txtProgress.setVisibility(View.GONE);
                            RECORD =null;
                            mCurrentPhotoPath = null;
//                            Toast.makeText(register.this, "Datos subidos exitosamente", Toast.LENGTH_LONG).show();
                            finish();
                        }
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AgregarActividad.this, "Error: " + e, Toast.LENGTH_LONG).show();
                Log.e("Error: ", e.toString());
            }
        }).addOnCanceledListener(new OnCanceledListener() {
            @Override
            public void onCanceled() {
                Toast.makeText(AgregarActividad.this, "En el cancelable", Toast.LENGTH_LONG).show();
            }
        }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(AgregarActividad.this, "En el pausable", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void Mi_hubicacion() {
        int leer2 = ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION);
        int leer3 = ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION);
        if (leer2 == PackageManager.PERMISSION_DENIED || leer3 == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(AgregarActividad.this, PERMISOS, REQUEST_CODE);
        }
        manager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        Location local = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        actualizar(local);
    }

    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            actualizar(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    private void actualizar(Location location) {
        if (location != null) {
            latitud = location.getLatitude();
            longitud = location.getLongitude();
        }else{
            latitud = 19.3980857;
            longitud = -102.0556112;
        }
    }

    void uploadQuickBase(){
        String Query = "https://aortizdemontellanoarevalo.quickbase.com/db/bnu3r2cfy?a=API_AddRecord"
                    +"&_fid_17="+record+ //Record ID
                    "&_fid_9="  +latLong+//Latitud&atLongitud
                    "&_fid_18=" +myIMEI+ // Imei
                    "&_fid_6="  +actRealizada+ //Descripcion de actividad
                    "&_fid_7="  +fecha+// Hora de registro
                    "&_fid_19=" +opcCatalogo+ // Tipo de actividad
                    "&_fid_20=" +viaticos+// Viaticos consumidos
                    "&_fid_23=" +rSocial+ // Razon social
                    "&_fid_24=" +URLEncoder.encode(downloadImageUrl)+// URL de Imagen
                    "&ticket="  +Tiket+
                    "&apptoken=" + token;
        try{
            new CargarDatos().execute(Query.replace(" ", "%20"));
            Toast.makeText(getApplicationContext(), "Se subio la informacion correctamente", Toast.LENGTH_LONG).show();
        } catch (Exception e){
            Toast.makeText(this, "Error de conexión", Toast.LENGTH_SHORT).show();
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

                    } catch (Exception e) {
//                            Toast.makeText(MainActivity.this, "Error al subir", Toast.LENGTH_SHORT).show();
                        System.out.println("error al subir: " + e.getMessage());
                    }
                } else {
                    Log.d("Error de consulta", resultado);

                }
            } else {
                /**En caso que respuesta sea null es por que fue error de http como los son;
                 * 404,500,403 etc*/
                Log.d("Error del Servidor ", result);
            }
        }
    }

}
