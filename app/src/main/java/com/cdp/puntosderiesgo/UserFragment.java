package com.cdp.puntosderiesgo;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class UserFragment extends Fragment {

    ImageView v_cerrarSesion;
    private FirebaseAuth mAuth;
    TextView v_Username,v_email;
    ImageView profile;

    public UserFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_user, container, false);
        mAuth= FirebaseAuth.getInstance();
        v_cerrarSesion=view.findViewById(R.id.btnCerrarSesion);
        v_Username=view.findViewById(R.id.nombreUsuario);
        v_email=view.findViewById(R.id.emailProfile);
        profile=view.findViewById(R.id.imagenPerfil);

        cargarDatos(view);

        // Inflate the layout for this fragment
        v_cerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                startActivity(new Intent(view.getContext().getApplicationContext(),Login.class));
                requireActivity().finish();
            }
        });
        return view;
    }

    private void cargarDatos(View v){
        DatabaseReference getData= FirebaseDatabase.getInstance().getReference().child("Usuarios")
                .child(Objects.requireNonNull(mAuth.getUid()));
        getData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    v_Username.setText(Objects.requireNonNull(snapshot.child("username").getValue()).toString());
                    v_email.setText(Objects.requireNonNull(snapshot.child("email").getValue()).toString());
                    Picasso.with(v.getContext().getApplicationContext())
                            .load(Objects.requireNonNull(snapshot.child("userPhoto").getValue()).toString())
                            .into(profile);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}