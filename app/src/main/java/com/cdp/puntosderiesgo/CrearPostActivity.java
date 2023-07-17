package com.cdp.puntosderiesgo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.cdp.puntosderiesgo.clases.Categoria;
import com.cdp.puntosderiesgo.clases.Post;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

public class CrearPostActivity extends AppCompatActivity {

    EditText v_title,v_descripcion;
    Spinner v_categoria;
    Button v_btn_imagen, v_anadir;
    ImageView v_suibr_imagen;
    private static final int File=1;
    DatabaseReference myRef,spinnerRef;
    StorageReference storageReference;
    FirebaseAuth mAuth;
    String storage_path_post="Post/*";
    private static final int COD_SEL_IMAGE=300;

    private Uri image_url;
    String photo="photo";
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_post);
        v_suibr_imagen=findViewById((R.id.imagen));
        v_anadir=findViewById(R.id.btn_anadir);
        v_btn_imagen=findViewById(R.id.btn_imagen);
        v_title=findViewById(R.id.txtTitle);
        v_descripcion=findViewById(R.id.txtPRDescription);
        v_categoria=findViewById(R.id.spinner);
        mAuth=FirebaseAuth.getInstance();
        spinnerRef=FirebaseDatabase.getInstance().getReference().child("App");
        FirebaseDatabase database= FirebaseDatabase.getInstance();
        myRef= database.getReference("Usuarios").child(mAuth.getCurrentUser()
                .getUid()).child("publicaciones").child(idGenerator());
        progressDialog= new ProgressDialog(this);
        storageReference=FirebaseStorage.getInstance().getReference();


        cargarCategorias();
        double latitude = getIntent().getDoubleExtra("latitude",0);
        double longitude = getIntent().getDoubleExtra("longitude",0);

        v_btn_imagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cargarFoto();
            }
        });
        v_anadir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                crearPost(myRef.getKey(),latitude,longitude);
            }
        });
    }
    private void cargarCategorias(){

        ArrayList<Categoria> categorias=new ArrayList<>();
        spinnerRef.child("Categorias").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    categorias.clear();
                    for (DataSnapshot ds:snapshot.getChildren()){
                        String nombre=ds.child("nombre").getValue().toString();
                        categorias.add(new Categoria(nombre));
                    }
                    ArrayAdapter<Categoria> adapter;
                    adapter= new ArrayAdapter<>(CrearPostActivity.this, android.R.layout.simple_spinner_dropdown_item,categorias);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    v_categoria.setAdapter(adapter);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                throw error.toException();
            }
        });
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
                image_url=data.getData();
                subirPhoto(image_url);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    private void subirPhoto(Uri image_url){

        progressDialog.setMessage("Actualizando Imagen");
        progressDialog.show();
        String rute_storage_photo=storage_path_post+""+photo+""+mAuth.getUid()+""+idGenerator();//Es una id diferente
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
                            Picasso.with(CrearPostActivity.this).load(download_uri)
                                    .fit()
                                    .into(v_suibr_imagen);
                            HashMap<String,Object> map=new HashMap<>();
                            map.put("photo",download_uri);
                            myRef.updateChildren(map);
                            progressDialog.dismiss();
                            Toast.makeText(CrearPostActivity.this,"Foto Actualizada",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("TAG","Error al subir Foto", e);
                progressDialog.dismiss();
                Toast.makeText(CrearPostActivity.this,"Error al subir Foto",Toast.LENGTH_SHORT).show();
            }
        });

    }
    private void crearPost(String id, double latitude, double longitude) {
        if(v_title.getText().toString().equals("")||v_descripcion.getText().toString().equals("")){
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
            builder.setMessage("Es necesario subir una imagen del suceso")
                    .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            builder.show();
        } else {
            String title = v_title.getText().toString();
            String detalle = v_descripcion.getText().toString();
            String categoria = v_categoria.getSelectedItem().toString();
            final String[] pais = new String[1];
            final String[] ciudad = new String[1];
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            String FechaHora = simpleDateFormat.format(new Date());

            HashMap<String, Object> map = new HashMap<>();
            map.put("id", id);
            map.put("latitude", latitude);
            map.put("longitude", longitude);
            map.put("title", title);
            map.put("detalle", detalle);
            map.put("categoria", categoria);
            map.put("fecha", FechaHora);
            myRef.updateChildren(map);

            String latString = String.valueOf(latitude);
            String lngString = String.valueOf(longitude);
            //url de la API call
            String url =
                    "https://api.openweathermap.org/data/2.5/weather?lat=" + latString + "&lon="
                            + lngString + "&appid=adbcbb553555fc3bfebec807e947eb27&units=metric&lang=es";

            StringRequest postRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {

                        JSONObject jsonObject = new JSONObject(response);
                        JSONObject jsonObjectSys = jsonObject.getJSONObject("sys");

                        pais[0] = jsonObjectSys.getString("country");//Pais

                        ciudad[0] = jsonObject.getString("name");//Ciudad

                        DatabaseReference refPost;
                        refPost = FirebaseDatabase.getInstance().getReference("Publicaciones").child(pais[0]).child(ciudad[0]);
                        refPost.child(mAuth.getUid()).child(id).setValue("");

                    } catch (Exception e) {
                        Log.d("TAG", "Fallo GET Ciudad CrearPostActivity: " + e);
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(CrearPostActivity.this, error.toString().trim(), Toast.LENGTH_SHORT).show();
                    Log.d("TAG", "Fallo GET Ciudad CrearPostActivity: " + error);
                }
            });
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.add(postRequest);

            Toast.makeText(CrearPostActivity.this,"Punto de Riesgo creado exitosamente",Toast.LENGTH_SHORT).show();

            Intent ns = new Intent(CrearPostActivity.this, MainActivity.class);
            ns.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ns.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(ns);
        }
    }

    public String idGenerator(){
        Random rnd = new Random();
        String num ="";

        for (int i = 0; i < 16; i++) {
            if (i < 8) { //Obtiene los primeros 8 numeros.
                num += rnd.nextInt(8);
            } else {
                //Obtiene caracter aleatorio entre 65 y 90 ("A" y "Z").
                num += String.valueOf((char) (rnd.nextInt(90-65) + 65));
            }
        }
        return num;
    }

    @Override
    public void onBackPressed(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("¿Salir al menú principal? se perderán los datos")
                .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(CrearPostActivity.this,MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.show();
    }

}