package com.cdp.puntosderiesgo;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class PostView extends AppCompatActivity {

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_view);

        TextView v_title = findViewById(R.id.lblTitle);
        TextView v_category = findViewById(R.id.lblCategory);
        TextView v_detalle = findViewById(R.id.lblDescripcion);
        TextView v_fecha = findViewById(R.id.lblFecha);
        ImageView v_imagen = findViewById(R.id.imgPost);
        ImageView v_btnComment = findViewById(R.id.btnComment);
        ImageView v_imgAuthor = findViewById(R.id.imgAuthor);
        TextView v_nameAuthor = findViewById(R.id.nameAuthor);

        String categoria=getIntent().getStringExtra("categoria");
        String fechaHora=getIntent().getStringExtra("fechaHora");
        String title=getIntent().getStringExtra("title");
        String photo=getIntent().getStringExtra("photo");
        String detalle=getIntent().getStringExtra("detalle");
        String usuario=getIntent().getStringExtra("usuario");
        String idpost=getIntent().getStringExtra("idpost");

        v_title.setText(title);
        v_category.setText(categoria);
        v_detalle.setText(detalle);
        v_fecha.setText(fechaHora);
        Picasso.with(this)
                .load(photo)
                .into(v_imagen);

        DatabaseReference mUserProfile= FirebaseDatabase.getInstance().getReference().child("Usuarios")
                .child(usuario);
        mUserProfile.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String image_uri= String.valueOf(snapshot.child("userPhoto").getValue());
                    String username= String.valueOf(snapshot.child("username").getValue());

                    Picasso.with(PostView.this).load(image_uri)
                            .into(v_imgAuthor);
                    v_nameAuthor.setText(username);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        v_btnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                irComentarios(usuario,idpost);
            }
        });

    }

    private void irComentarios(String usuario, String idpost){
        Intent ns= new Intent(PostView.this,Comentarios.class);
        ns.putExtra("usuario",usuario);
        ns.putExtra("idpost",idpost);
        startActivity(ns);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if(keyCode== KeyEvent.KEYCODE_BACK){
                            Intent intent=new Intent(PostView.this,MainActivity.class);
                            startActivity(intent);
                            finish();
        }

        return super.onKeyDown(keyCode, event);
    }
}