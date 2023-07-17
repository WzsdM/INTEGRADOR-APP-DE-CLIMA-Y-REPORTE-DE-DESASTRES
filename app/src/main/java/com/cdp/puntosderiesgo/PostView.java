package com.cdp.puntosderiesgo;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class PostView extends AppCompatActivity {

    TextView v_title,v_category,v_detalle,v_fecha;
    ImageView v_imagen;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_view);

        v_title=findViewById(R.id.lblTitle);
        v_category=findViewById(R.id.lblCategory);
        v_detalle=findViewById(R.id.lblDescripcion);
        v_fecha=findViewById(R.id.lblFecha);
        v_imagen=findViewById(R.id.imgPost);

        String categoria=getIntent().getStringExtra("categoria");
        String fechaHora=getIntent().getStringExtra("fechaHora");
        String title=getIntent().getStringExtra("title");
        String photo=getIntent().getStringExtra("photo");
        String detalle=getIntent().getStringExtra("detalle");

        v_title.setText(title);
        v_category.setText(categoria);
        v_detalle.setText(detalle);
        v_fecha.setText(fechaHora);
        Picasso.with(this)
                .load(photo)
                .fit()
                .into(v_imagen);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if(keyCode==event.KEYCODE_BACK){
                            Intent intent=new Intent(PostView.this,MainActivity.class);
                            startActivity(intent);
                            finish();
        }

        return super.onKeyDown(keyCode, event);
    }
}