package com.cdp.puntosderiesgo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class Login extends AppCompatActivity {

    private EditText v_email;
    private EditText v_clave;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        v_email= findViewById(R.id.txtemail);
        v_clave= findViewById(R.id.txtpassword);
        TextView v_register = findViewById(R.id.txtRegister);

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

        String username= getIntent().getStringExtra("username");
        String idFotolocal= getIntent().getStringExtra("userPhoto");
        String emailUser= getIntent().getStringExtra("email");

            if(username!=null&&idFotolocal!=null){
                DatabaseReference comprobar = FirebaseDatabase.getInstance().getReference()
                        .child("Usuarios");

                comprobar.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.child(Objects.requireNonNull(mAuth.getUid())).exists()) {
                        }else{
                                comprobar.child(mAuth.getUid()).child("username").setValue(username);
                                comprobar.child(mAuth.getUid()).child("userPhoto").setValue(idFotolocal);
                                comprobar.child(mAuth.getUid()).child("email").setValue(emailUser);
                            }
                        }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

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
        if(v_email.getText().toString().trim().equals("")||v_clave.getText().toString().trim().equals("")){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Por favor introducir todos los datos")
                    .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            builder.show();
        }else {

            String email = v_email.getText().toString().trim();
            String password = v_clave.getText().toString().trim();

            progressDialog.setMessage("Accediendo a la cuenta...");
            progressDialog.show();

            mAuth.signInWithEmailAndPassword(email, password).
                    addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                Log.d("TAG", "login:success");
                                Toast.makeText(Login.this,
                                        "Acceso Correcto",
                                        Toast.LENGTH_LONG).show();

                                Intent ns = new Intent(Login.this, MainActivity.class);
                                startActivity(ns);

                            } else {
                                Log.d("TAG", "login:failure",
                                        task.getException());
                                Toast.makeText(Login.this,
                                        "No se pudo Acceder",
                                        Toast.LENGTH_LONG).show()
                                ;
                            }
                            progressDialog.dismiss();
                        }
                    })
            ;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode== KeyEvent.KEYCODE_BACK){
            Intent intent=new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        return super.onKeyDown(keyCode, event);
    }

}