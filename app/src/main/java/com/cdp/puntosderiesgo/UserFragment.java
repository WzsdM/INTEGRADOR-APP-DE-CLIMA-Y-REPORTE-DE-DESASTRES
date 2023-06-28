package com.cdp.puntosderiesgo;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;

public class UserFragment extends Fragment {

    TextView v_cerrarSesion;
    private FirebaseAuth mAuth;

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
        v_cerrarSesion=view.findViewById(R.id.cerrarSesion);
        // Inflate the layout for this fragment
        v_cerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                startActivity(new Intent(view.getContext(),Login.class));
                requireActivity().finish();
            }
        });
        return view;
    }
}