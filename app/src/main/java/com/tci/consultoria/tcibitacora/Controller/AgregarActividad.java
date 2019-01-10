package com.tci.consultoria.tcibitacora.Controller;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.tci.consultoria.tcibitacora.Modelos.Opciones;
import com.tci.consultoria.tcibitacora.R;
import com.tci.consultoria.tcibitacora.Singleton.Principal;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import uk.co.senab.photoview.PhotoViewAttacher;

import static com.tci.consultoria.tcibitacora.MainActivity.EMPRESA;

public class AgregarActividad extends AppCompatActivity {
    ImageView imgPhoto;
    Spinner spnOpcion;
    PhotoViewAttacher photoViewAttacher = null;
    static final int REQUEST_TAKE_PHOTO = 1;
    List<Opciones> listaOpciones = new ArrayList<Opciones>();
    ArrayAdapter<Opciones> arrayAdapterPersona;
    Principal p = Principal.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_actividad);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        init();
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

            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
         getMenuInflater().inflate(R.menu.menu_actividad, menu);
        return true;
    }

    public void init(){
        imgPhoto = findViewById(R.id.imgPhoto);
        photoViewAttacher = new PhotoViewAttacher(imgPhoto);
        spnOpcion = findViewById(R.id.spnOpcion);

        llenarSpinner();
    }

    public static String mCurrentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
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
                .child("actividades").child("opciones").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listaOpciones.clear();
                for(DataSnapshot objSnapshot : dataSnapshot.getChildren()){
                    Opciones opc = objSnapshot.getValue(Opciones.class);
                    listaOpciones.add(opc);
                    arrayAdapterPersona = new ArrayAdapter<Opciones>(
                            AgregarActividad.this,android.R.layout.simple_spinner_item,listaOpciones);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
