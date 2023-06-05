package com.cdp.puntosderiesgo;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity{

    //Crear clases de los fragmentos
    ClimaFragment climaFragment= new ClimaFragment();
    UserFragment userFragment= new UserFragment();
    InformacionFragment informacionFragment= new InformacionFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Vincular el menú de navegación
        BottomNavigationView navigation = findViewById(R.id.bottom_navigation);
        //habilitar el detector cuando se seleccione un item de la lista del menú de navegación
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        //Cargar fragmento por defecto al abrir la aplicación
        if(savedInstanceState == null) {
            navigation.setSelectedItemId(R.id.añadir);
            loadFragment(climaFragment);
        }
    }

    //crear variable del detector cuando un item es seleccionado de la lista del menú de navegación
    private final BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener= new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        //Acciones cuando un item es seleccionado
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            //Dependiendo de qué item se seleccione te redirige a un fragmento o a otro
            if (item.getItemId() == R.id.informacion) {
                loadFragment(informacionFragment);
                return true;
            } else if (item.getItemId() == R.id.añadir) {
                loadFragment(climaFragment);
                return true;
            } else if (item.getItemId() == R.id.usuario) {
                loadFragment(userFragment);
                return true;
            } else {
                return false;
            }
        }
    };

    //Método para reemplazar de un fragmento a otro
    public void loadFragment(Fragment fragment){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container,fragment);
        transaction.commit();
    }
}