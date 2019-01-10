package com.tci.consultoria.tcibitacora.Singleton;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class Principal extends AppCompatActivity {

    public FirebaseDatabase firebaseDatabase;
    public DatabaseReference databaseReference;
    public StorageReference storageRef;
    public FirebaseAuth firebaseAuth;

    private static Principal principal;
    private Principal() {

    }

    public static Principal getInstance(){
        if (principal == null ) {
            principal = new Principal();
        }
        return principal;
    }

    public void InicializarFirebase(){
        if (principal == null ) {
            principal = new Principal();
        }else{
            FirebaseApp.initializeApp(getInstance());
            firebaseDatabase = FirebaseDatabase.getInstance();
            firebaseDatabase.setPersistenceEnabled(true);

            databaseReference = firebaseDatabase.getReference();
            storageRef = FirebaseStorage.getInstance().getReference();
            firebaseAuth = FirebaseAuth.getInstance();
        }
    }

}
