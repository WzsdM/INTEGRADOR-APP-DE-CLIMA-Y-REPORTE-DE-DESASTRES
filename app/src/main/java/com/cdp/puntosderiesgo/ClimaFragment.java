package com.cdp.puntosderiesgo;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;

public class ClimaFragment extends Fragment implements OnMapReadyCallback {

    // Proveedor de la locación actual del cliente.
    private FusedLocationProviderClient fusedLocationClient;

    public ClimaFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Creación de la vista
        View view = inflater.inflate(R.layout.fragment_mapa, container, false);
        //Valor de la locación actual del cliente
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(view.getContext());
        //Conexión del fragmento del mapa Google
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        //Sincronizar mapa
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
        //iniciar el método para obtener los permisos de localización
        getLocalizacion(view);

        return view;
    }



    private void getLocalizacion(View view) {
        //Pedir los permisos de localización
        int permiso = ContextCompat.checkSelfPermission(view.getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION);
        if(permiso == PackageManager.PERMISSION_DENIED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION)){
            }else{
                ActivityCompat.requestPermissions(requireActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
    }



    @Override
    //Acciones a ejecutar cuando el mapa se carga
    public void onMapReady(@NonNull GoogleMap googleMap) {
        //revisar permisos de localización
        if (ActivityCompat.checkSelfPermission(requireView().getContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireView().getContext(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
        }
        //Obtener última localización del dispositivo
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                    @Override
                    //Acciones a realizar cuando se haya obtenido la localización exitosamente
                    public void onSuccess(Location location) {
                        //Obtener latitud y longitud de la ubicación
                        LatLng primeraUbicacion = new LatLng(location.getLatitude(), location.getLongitude());
                        //Movemos la cámara a la ubicación
                        googleMap.moveCamera(CameraUpdateFactory.newLatLng(primeraUbicacion));
                        //Crear el área circular al rededor de la última ubicación
                        Circle circle = googleMap.addCircle(new CircleOptions()
                                .center(primeraUbicacion)
                                .radius(500)
                                .strokeColor(Color.TRANSPARENT)
                                .fillColor(0x50ffd500));

                    }
                });

        //Habilitamos la ubicación en tiempo real en Google Maps
        googleMap.setMyLocationEnabled(true);
        //Desabilitamos el botón de ir a la localización
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);

    }
}