package com.cdp.puntosderiesgo;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class Comentarios extends AppCompatActivity {

    private DatabaseReference mRefPost;
    private FirebaseAuth mAuth;
    private final String idcoment=idGenerator();
    private TextView v_txtMyComment;
    private ImageView v_profileMyuser;
    private RecyclerView recyclerView;
    private final List<ItemComentario> items= new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comentarios);

        mAuth=FirebaseAuth.getInstance();
        v_txtMyComment=findViewById(R.id.txtMyComment);
        v_profileMyuser=findViewById(R.id.profileMyuser);

        DatabaseReference mUserProfile= FirebaseDatabase.getInstance().getReference().child("Usuarios")
                .child(Objects.requireNonNull(mAuth.getUid()));
        mUserProfile.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            String image_uri= String.valueOf(snapshot.child("userPhoto").getValue());
                            Picasso.with(Comentarios.this).load(image_uri)
                                    .into(v_profileMyuser);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        String usuario=getIntent().getStringExtra("usuario");
        String idpost=getIntent().getStringExtra("idpost");
        recyclerView=findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(Comentarios.this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);
        cargarComentarios(usuario,idpost);
        recyclerView.smoothScrollToPosition(0);

        ImageView v_btnSend = findViewById(R.id.btnSend);
        v_btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                publicarComentario(mRefPost,idcoment);
            }
        });

    }

    private void publicarComentario(DatabaseReference mRefPost,String idcoment){
        String username=mAuth.getUid();
        String comentario=v_txtMyComment.getText().toString();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        String hora = simpleDateFormat.format(new Date());

        if(comentario.equals("")){
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
            HashMap<String, Object> map = new HashMap<>();
            map.put("username",username);
            map.put("comentario",comentario);
            map.put("hora",hora);
            mRefPost.child(idcoment).updateChildren(map);
        }

    }

    private void cargarComentarios(String usuario, String idpost){
            mRefPost= FirebaseDatabase.getInstance().getReference().child("Usuarios")
                    .child(usuario).child("publicaciones").child(idpost)
                    .child("comentarios");

            mRefPost.addValueEventListener(new ValueEventListener() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        items.clear();
                        for (DataSnapshot comentario:snapshot.getChildren()){
                            String usuario= String.valueOf(comentario.child("username").getValue());
                            DatabaseReference mRefProfile= FirebaseDatabase.getInstance().getReference()
                                    .child("Usuarios").child(usuario);
                            mRefProfile.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(snapshot.exists()){
                                        String username= String.valueOf(snapshot.child("username").getValue());
                                        String imageProfile= String.valueOf(snapshot.child("userPhoto").getValue());
                                        String coment= String.valueOf(comentario.child("comentario").getValue());
                                        String hora= String.valueOf(comentario.child("hora").getValue());
                                        items.add(new ItemComentario(imageProfile,username,hora,coment));
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                        myComentAdapter adapter=new myComentAdapter(Comentarios.this,items);
                        recyclerView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("TAG","ERROR_CARGAR_COMENTARIOS : "+error);
                    Toast.makeText(Comentarios.this,"Error al cargar Comentarios",Toast.LENGTH_SHORT).show();
                }
            });
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