package com.tci.consultoria.tcibitacora;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.tci.consultoria.tcibitacora.Estaticas.statics;
import com.tci.consultoria.tcibitacora.Singleton.Principal;

public class Login extends AppCompatActivity {
    EditText txt_user, txt_pass;
    Principal p = Principal.getInstance();
    private FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        init();
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            AlertDialog.Builder alerta = new AlertDialog.Builder(Login.this);
            alerta.setTitle(statics.TITULO_ALERTA_SALIDA_APP)
                    .setMessage(statics.MESSAGE_ALERTA_SALIDA_APP)
                    .setPositiveButton(statics.BUTTON_OK_ALERTA_SALIDA_APP, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finishAffinity();
                        }
                    }).setNegativeButton(statics.BUTTON_CANCEL_ALERTA_SALIDA_APP, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            }).create().show();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onStart() {
        super.onStart();
        p.firebaseAuth.addAuthStateListener(authStateListener);
    }
    public void init(){
        txt_user = findViewById(R.id.txt_user);
        txt_pass = findViewById(R.id.txt_pass);
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser()!=null){
                    startActivity(new Intent(Login.this,MainActivity.class));
                    cleanLogin();
                }
            }
        };
    }

    public boolean validaLogin(){
        String Email = txt_user.getText().toString();
        String Contrasenia = txt_pass.getText().toString();
        if(Email.equals("")){
            txt_user.setError("Ingresa E-mail");
        }else if(Contrasenia.equals("")){
            txt_pass.setError("Ingresa contraseña");
        }else{
            return true;
        }

        return  false;
    }
    public void cleanLogin(){
        txt_user.setText("");
        txt_pass.setText("");
    }
    public void login(View view){
        if(validaLogin()){
        p.firebaseAuth.signInWithEmailAndPassword(txt_user.getText().toString(),txt_pass.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(Login.this, statics.AUTENTICACION_FALLIDA,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        }
    }



}
