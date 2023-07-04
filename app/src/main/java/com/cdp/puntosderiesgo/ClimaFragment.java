package com.cdp.puntosderiesgo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
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

        return view;
    }



    //Acciones a ejecutar cuando el mapa se carga
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        leerPost();

        //Obtener última localización del dispositivo
        if (ActivityCompat.checkSelfPermission(getView().getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getView().getContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                    @Override
                    //Acciones a realizar cuando se haya obtenido la localización exitosamente
                    public void onSuccess(Location location) {
                        //Obtener latitud y longitud de la ubicación
                        LatLng primeraUbicacion = new LatLng(location.getLatitude(), location.getLongitude());

                        //Obtenemos por separado la longitud y latitud
                        double latubicacion = location.getLatitude();
                        double lngubicacion = location.getLongitude();
                        //creamos un bundle para pasar los datos
                        Bundle result = new Bundle();
                        //Añadimos la longitud y latitud al bundle
                        result.putDouble("latitud", latubicacion);
                        result.putDouble("longitud", lngubicacion);
                        //pasamos el bundle
                        getParentFragmentManager().setFragmentResult("requestKey", result);

                        //Movemos la cámara a la ubicación
                        googleMap.moveCamera(CameraUpdateFactory.newLatLng(primeraUbicacion));

                        //Crear el área circular al rededor de la última ubicación
                        Circle circle = googleMap.addCircle(new CircleOptions()
                                .center(primeraUbicacion)
                                .radius(500)
                                .strokeColor(Color.TRANSPARENT)
                                .fillColor(0x50ffd500)
                                .clickable(true));
                        googleMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                            @Override
                            public void onMyLocationChange(@NonNull Location location) {
                                LatLng nuevaubicacion = new LatLng(location.getLatitude(), location.getLongitude());
                                circle.setCenter(nuevaubicacion);
                            }
                        });


                        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                            //Creamos un detector del mapa del circulo
                            @Override
                            public void onMapClick(@NonNull LatLng latLng) {

                                googleMap.setOnCircleClickListener(new GoogleMap.OnCircleClickListener() {
                                    //Creamos un detector del click del circulo
                                    @Override
                                    public void onCircleClick(@NonNull Circle circle) {
                                        //Obtenemos la lat, y long del click
                                        double latitude = latLng.latitude;
                                        double longitude = latLng.longitude;

                                        Intent ns = new Intent(getView().getContext(), CrearPostActivity.class);
                                        ns.putExtra("latitude", latitude);
                                        ns.putExtra("longitude", longitude);

                                        //Creamos el enlace para pasar de este fragmento a la actividad de crear publicaciones

                                        startActivity(ns);
                                    }
                                });
                            }
                        });

                    }
                });

        //Habilitamos la ubicación en tiempo real en Google Maps
        googleMap.setMyLocationEnabled(true);

        //Desabilitamos el botón de ir a la localización
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);

    }

    private void leerPost(){

    }

}