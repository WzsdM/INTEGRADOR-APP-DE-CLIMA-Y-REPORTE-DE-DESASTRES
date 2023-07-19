package com.cdp.puntosderiesgo;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

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