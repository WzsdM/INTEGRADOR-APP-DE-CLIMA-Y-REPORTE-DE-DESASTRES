package com.cdp.puntosderiesgo;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MyComentViewHolder extends RecyclerView.ViewHolder{

    ImageView v_profile;
    TextView v_username;
    TextView v_hora;
    TextView v_comentario;

    public MyComentViewHolder(@NonNull View itemView) {
        super(itemView);

        v_profile=itemView.findViewById(R.id.userProfile);
        v_username=itemView.findViewById(R.id.nameUser);
        v_hora=itemView.findViewById(R.id.horaComentario);
        v_comentario=itemView.findViewById(R.id.userComent);

    }
}
