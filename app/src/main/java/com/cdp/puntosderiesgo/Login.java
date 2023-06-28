package com.cdp.puntosderiesgo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {

    private EditText v_email;
    private EditText v_clave;
    private TextView v_register;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        v_email= findViewById(R.id.txtemail);
        v_clave= findViewById(R.id.txtpassword);
        v_register= findViewById(R.id.txtRegister);

        SpannableString ss= new SpannableString(v_register.getText());
        ClickableSpan clickableSpan= new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Intent ns=new Intent(Login.this,Register.class);
                startActivity(ns);
            }
        };

        ss.setSpan(clickableSpan,23,33, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        v_register.setText(ss);
        v_register.setMovementMethod(LinkMovementMethod.getInstance());

        mAuth= FirebaseAuth.getInstance();
        progressDialog=new ProgressDialog(this);

    }

    @Override
    protected void onStart(){
        super.onStart();
        mAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser currentUser= mAuth.getCurrentUser();
                if (currentUser != null) {
                    Log.w("TAG", "onAuthStateChanged - Logueado");
                    Intent ns=new Intent(Login.this, MainActivity.class);
                    startActivity(ns);
                } else {
                    Log.w("TAG", "onAuthStateChanged - Cerro sesion");
                }
            }
        });
    }

    public void doCargar(View view){

        String email= v_email.getText().toString().trim();
        String password= v_clave.getText().toString().trim();

        progressDialog.setMessage("Accediendo a la cuenta...");
        progressDialog.show();

        mAuth.signInWithEmailAndPassword(email,password).
                addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){

                            Log.d("TAG","login:success");
                            Toast.makeText(Login.this,
                                    "Acceso Correcto",
                                    Toast.LENGTH_LONG).show();

                            Intent ns=new Intent(Login.this,MainActivity.class);
                            startActivity(ns);

                        }else{
                            Log.d("TAG","login:failure",
                                    task.getException());
                            Toast.makeText(Login.this,
                                    "No se pudo Acceder",
                                    Toast.LENGTH_LONG).show();
                        }
                        progressDialog.dismiss();
                    }
                });

    }

    public void passtoRegister(){
        Intent ns=new Intent(Login.this,Register.class);
        startActivity(ns);
    }

}