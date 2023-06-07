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

public class Register extends AppCompatActivity {

    private EditText v_email;
    private EditText v_clave;
    private TextView v_login;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        v_email= findViewById(R.id.txtregemail);
        v_clave= findViewById(R.id.txtregpassword);
        v_login= findViewById(R.id.txtLogin);

        SpannableString ss= new SpannableString(v_login.getText());
        ClickableSpan clickableSpan= new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Intent ns=new Intent(Register.this,Login.class);
                startActivity(ns);
            }
        };

        ss.setSpan(clickableSpan,23,32, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        v_login.setText(ss);
        v_login.setMovementMethod(LinkMovementMethod.getInstance());

        mAuth= FirebaseAuth.getInstance();
        progressDialog=new ProgressDialog(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser= mAuth.getCurrentUser();
    }

    public void doRegistrar(View view){

        String email= v_email.getText().toString().trim();
        String password= v_clave.getText().toString().trim();


        progressDialog.setMessage("Realizando el registro...");
        progressDialog.show();

        mAuth.createUserWithEmailAndPassword(email,password).
                addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){

                            Log.d("TAG","createUserWithEmail:success");
                            Toast.makeText(Register.this,
                                    "Se ha registrado los datos",
                                    Toast.LENGTH_LONG).show();

                            v_email.setText("");
                            v_clave.setText("");

                        }else{
                            Log.d("TAG","createUserWithEmail:failure",
                                    task.getException());
                            Toast.makeText(Register.this,
                                    "No se pudo registrar los datos",
                                    Toast.LENGTH_LONG).show();
                        }
                        progressDialog.dismiss();
                    }
                });
    }

}