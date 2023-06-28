package com.cdp.puntosderiesgo;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
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

import org.json.JSONException;
import org.json.JSONObject;

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

                        //Obtenemos por separado la longitud y latitud
                        double latubicacion=location.getLatitude();
                        double lngubicacion=location.getLongitude();
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

                        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                            //Creamos un detector del mapa del circulo
                            @Override
                            public void onMapClick(@NonNull LatLng latLng) {
                                if(circle.isClickable()){
                                    googleMap.setOnCircleClickListener(new GoogleMap.OnCircleClickListener() {
                                        //Creamos un detector del click del circulo
                                        @Override
                                        public void onCircleClick(@NonNull Circle circle) {
                                            //Obtenemos la lat, y long del click
                                            double latitude= latLng.latitude;
                                            double longitude= latLng.longitude;

                                            String[] ciudadPais=getCiudadPais(latitude,longitude);

                                            //Creamos el enlace para pasar de este fragmento a la actividad de crear publicaciones
                                            Intent ns=new Intent(getView().getContext(),CrearPostActivity.class);
                                            ns.putExtra("latitude", latitude);
                                            ns.putExtra("longitude", longitude);
                                            ns.putExtra("ciudad", ciudadPais[0]);
                                            ns.putExtra("pais", ciudadPais[1]);
                                            startActivity(ns);
                                        }
                                    });
                                }
                            }
                        });

                    }
                });

        //Habilitamos la ubicación en tiempo real en Google Maps
        googleMap.setMyLocationEnabled(true);
        //Desabilitamos el botón de ir a la localización
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);

    }

    private String[] getCiudadPais(double latubicacion, double lngubicacion){
        String latString= String.valueOf(latubicacion);
        String lngString= String.valueOf(lngubicacion);
        final String[] ciudad = new String[2];
        //url de la API call
        String url =
                "https://api.openweathermap.org/data/2.5/weather?lat="+latString+"&lon="
                        +lngString+"&appid=adbcbb553555fc3bfebec807e947eb27&units=metric&lang=es";

        //Request del método GET(lectura de datos)
        StringRequest postRequest=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            //Capturamos la respuesta del GET en variables
            public void onResponse(String response) {
                //Conversión de json a objeto para extraer los datos
                try {

                    JSONObject jsonObject= new JSONObject(response);
                    JSONObject jsonObjectSys=jsonObject.getJSONObject("sys");

                    String cityName=jsonObject.getString("name");//Ciudad
                    String countryName=jsonObjectSys.getString("country");//Pais
                    ciudad[0] =cityName;
                    ciudad[1] =countryName;
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //enviar error si en caso hubiera
                Log.e("error",error.getMessage());
                ciudad[0] ="";
                ciudad[1]="";
            }
        });

        //enviar la petición
        RequestQueue requestQueue = Volley.newRequestQueue(getView().getContext());
        requestQueue.add(postRequest);
        return ciudad;
    }

}