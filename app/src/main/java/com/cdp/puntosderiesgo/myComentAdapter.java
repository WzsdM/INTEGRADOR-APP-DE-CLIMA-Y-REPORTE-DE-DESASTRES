package com.cdp.puntosderiesgo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class myComentAdapter extends RecyclerView.Adapter<MyComentViewHolder> {

    Context context;
    List<ItemComentario> items;

    public myComentAdapter(Comentarios context, List<ItemComentario> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override

    public MyComentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyComentViewHolder(LayoutInflater.from(context).inflate(R.layout.comment_view,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyComentViewHolder holder, int position) {

        holder.v_username.setText(items.get(position).getUsername());
        holder.v_hora.setText(items.get(position).getHoraComentario());
        holder.v_comentario.setText(items.get(position).getComentario());
        Picasso.with(context)
                .load(items.get(position).getUserProfile()).into(holder.v_profile);

    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
