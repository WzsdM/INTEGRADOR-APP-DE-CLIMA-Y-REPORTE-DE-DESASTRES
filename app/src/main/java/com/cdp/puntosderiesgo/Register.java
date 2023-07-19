package com.cdp.puntosderiesgo;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Random;

public class Register extends AppCompatActivity {

    private EditText v_email;
    private EditText v_clave;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;
    private ImageView v_foto_perfil;
    private EditText v_username;
    private StorageReference storageReference;
    private static final int COD_SEL_IMAGE=300;

    private Uri image_url;
    private Intent intent;

    public Register() {
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        v_email= findViewById(R.id.txtregemail);
        v_clave= findViewById(R.id.txtregpassword);
        v_username=findViewById(R.id.txtregUsername);
        TextView v_login = findViewById(R.id.txtLogin);
        v_foto_perfil=findViewById(R.id.imgregPerfil);
        Button v_cargar_foto = findViewById(R.id.btnPerfilFoto);
        intent=new Intent(Register.this,Login.class);
        progressDialog= new ProgressDialog(this);
        storageReference= FirebaseStorage.getInstance().getReference();

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

        v_cargar_foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cargarFoto();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void cargarFoto() {
        Intent i= new Intent(Intent.ACTION_PICK);
        i.setType("image/*");
        startActivityForResult(i,COD_SEL_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode==RESULT_OK){
            if(requestCode==COD_SEL_IMAGE){
                assert data != null;
                image_url=data.getData();
                subirPhoto(image_url);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    private void subirPhoto(Uri image_url){

        progressDialog.setMessage("Actualizando Imagen");
        progressDialog.show();
        String storage_path_post = "User/*";
        String photo = "photo";
        String rute_storage_photo= storage_path_post +""+ photo +""+mAuth.getUid()+""+idGenerator();//Es una id diferente
        StorageReference reference=storageReference.child(rute_storage_photo);
        reference.putFile(image_url).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri>uriTask=taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isSuccessful());
                if(uriTask.isSuccessful()){
                    uriTask.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String download_uri=uri.toString();
                            Picasso.with(Register.this).load(download_uri)
                                    .fit()
                                    .into(v_foto_perfil);
                            intent.putExtra("userPhoto", download_uri);
                            progressDialog.dismiss();
                            Toast.makeText(Register.this,"Foto Actualizada",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("TAG","Error al subir Foto", e);
                progressDialog.dismiss();
                Toast.makeText(Register.this,"Error al subir Foto",Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Log.d("TAG","Error al subir Foto", e);
            progressDialog.dismiss();
            Toast.makeText(Register.this,"Error al subir Foto",Toast.LENGTH_SHORT).show();
        });

    }

    public void doRegistrar(View view){
        if(v_email.getText().toString().equals("")||v_clave.getText().toString().equals("")
                ||v_username.getText().toString().equals("")){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Por favor introducir todos los datos")
                    .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            builder.show();
        } else if (image_url==null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Es necesario subir una imagen de perfil")
                    .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            builder.show();
        } else {

            String email = v_email.getText().toString().trim();
            String password = v_clave.getText().toString().trim();
            String username = v_username.getText().toString().trim();


            progressDialog.setMessage("Realizando el registro...");
            progressDialog.show();

            mAuth.createUserWithEmailAndPassword(email, password).
                    addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                Log.d("TAG", "createUserWithEmail:success");
                                Toast.makeText(Register.this,
                                        "Se ha registrado los datos",
                                        Toast.LENGTH_LONG).show()
                                ;

                                v_email.setText("");
                                v_clave.setText("");
                                v_username.setText("");

                                intent.putExtra("username",username);
                                intent.putExtra("email",email);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                startActivity(intent);

                            } else {
                                Log.d("TAG", "createUserWithEmail:failure",
                                        task.getException());
                                Toast.makeText(Register.this,
                                        "No se pudo registrar los datos",
                                        Toast.LENGTH_LONG).show();
                            }
                            progressDialog.dismiss();
                        }
                    })
            ;
        }
    }

    public String idGenerator(){
        Random rnd = new Random();
        StringBuilder num = new StringBuilder();

        for (int i = 0; i < 16; i++) {
            if (i < 8) { //Obtiene los primeros 8 numeros.
                num.append(rnd.nextInt(8));
            } else {
                //Obtiene caracter aleatorio entre 65 y 90 ("A" y "Z").
                num.append((char) (rnd.nextInt(90 - 65) + 65));
            }
        }
        return num.toString();
    }

}