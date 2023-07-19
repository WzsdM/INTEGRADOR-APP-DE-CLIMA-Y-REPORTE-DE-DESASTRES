package com.cdp.puntosderiesgo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.SphericalUtil;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ClimaFragment extends Fragment implements OnMapReadyCallback {

    // Proveedor de la locación actual del cliente.
    private FusedLocationProviderClient fusedLocationClient;
    private ImageView climahoy;
    private TextView deschoy;

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

        climahoy=view.findViewById(R.id.iconclimahoy);
        deschoy=view.findViewById(R.id.estadoclimahoy);

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

        //Obtener última localización del dispositivo
        if (ActivityCompat.checkSelfPermission(getView().getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getView().getContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        //Acciones a realizar cuando se haya obtenido la localización exitosamente
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(requireActivity(), new OnSuccessListener<Location>() {
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
                        String latString = String.valueOf(latubicacion);
                        String lngString = String.valueOf(lngubicacion);
                        //url de la API call
                        String url =
                                "https://api.openweathermap.org/data/2.5/weather?lat=" + latString + "&lon="
                                        + lngString + "&appid=adbcbb553555fc3bfebec807e947eb27&units=metric&lang=es";

                        //Request del método GET(lectura de datos)
                        StringRequest postRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                            @Override
                            //Capturamos la respuesta del GET en variables
                            public void onResponse(String response) {
                                //Conversión de json a objeto para extraer los datos
                                try {

                                    JSONObject jsonObject = new JSONObject(response);
                                    JSONObject jsonObjectSys = jsonObject.getJSONObject("sys");
                                    JSONArray jsonArray = jsonObject.getJSONArray("weather");
                                    JSONObject jsonObjectWeather = jsonArray.getJSONObject(0);
                                    String description = jsonObjectWeather.getString("description");
                                    String icon = jsonObjectWeather.getString("icon");

                                    String countryName = jsonObjectSys.getString("country");//Pais
                                    String cityName = jsonObject.getString("name");//Ciudad

                                    String urlImg = "http://openweathermap.org/img/wn/" + icon + "@2x.png";

                                    deschoy.setText(description);
                                    Picasso.with(getView().getContext())
                                            .load(urlImg)
                                            .into(climahoy);
                                    leerPost(googleMap, countryName, cityName);


                                    googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                        @Override
                                        public boolean onMarkerClick(@NonNull Marker marker) {

                                            String idMark = String.valueOf(marker.getTag());
                                            detalleMarker(idMark, countryName, cityName);
                                            return false;
                                        }
                                    });

                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }

                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                //enviar error si en caso hubiera
                                Log.e("error", error.getMessage());

                            }
                        });

                        //enviar la petición
                        RequestQueue requestQueue = Volley.newRequestQueue(getView().getContext());
                        requestQueue.add(postRequest);
                        //pasamos el bundle
                        getParentFragmentManager().setFragmentResult("requestKey", result);

                        //Movemos la cámara a la ubicación
                        googleMap.moveCamera(CameraUpdateFactory.newLatLng(primeraUbicacion));

                        //Crear el área circular al rededor de la última ubicación
                        Circle circle = googleMap.addCircle(new CircleOptions()
                                .center(primeraUbicacion)
                                .radius(500)
                                .strokeColor(Color.TRANSPARENT)
                                .fillColor(0x50ffd500));
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

                                double latitude = latLng.latitude;
                                double longitude = latLng.longitude;

                                double radius = circle.getRadius();

                                double distance = SphericalUtil.computeDistanceBetween(latLng, circle.getCenter());

                                boolean circleClicked = distance < radius;

                                if (circleClicked) {
                                    Intent ns = new Intent(getView().getContext(), CrearPostActivity.class);
                                    ns.putExtra("latitude", latitude);
                                    ns.putExtra("longitude", longitude);
                                    startActivity(ns);
                                }
                            }
                        });//Detectar click


                    }
                });

        //Habilitamos la ubicación en tiempo real en Google Maps
        googleMap.setMyLocationEnabled(true);

        //Desabilitamos el botón de ir a la localización
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);


    }

    private void detalleMarker(String idPost, String pais, String ciudad){
        DatabaseReference refId= FirebaseDatabase.getInstance().getReference("Publicaciones")
                .child(pais).child(ciudad);
        DatabaseReference refUsers=FirebaseDatabase.getInstance().getReference("Usuarios");
        refId.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {

                    for(DataSnapshot user:snapshot.getChildren()){
                        String username=user.getKey();
                        assert username != null;
                        refId.child(username).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    for (DataSnapshot ignored : snapshot.getChildren()) {

                                        refUsers.child(username).child("publicaciones")
                                                .addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        if(snapshot.exists()){

                                                            String categoria= String.valueOf(snapshot.child(idPost).child("categoria")
                                                                    .getValue());
                                                            String fechaHora= String.valueOf(snapshot.child(idPost).child("fecha")
                                                                    .getValue());
                                                            String title= String.valueOf(snapshot.child(idPost).child("title")
                                                                    .getValue());
                                                            String photo= String.valueOf(snapshot.child(idPost).child("photo")
                                                                    .getValue());
                                                            String detalle= String.valueOf(snapshot.child(idPost).child("detalle")
                                                                    .getValue());

                                                            Intent intent=new Intent(getView().getContext(),PostView.class);
                                                            intent.putExtra("categoria",categoria);
                                                            intent.putExtra("fechaHora",fechaHora);
                                                            intent.putExtra("title",title);
                                                            intent.putExtra("photo",photo);
                                                            intent.putExtra("detalle",detalle);
                                                            intent.putExtra("usuario",username);
                                                            intent.putExtra("idpost",idPost);
                                                            startActivity(intent);
                                                        }

                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                    }
                                                });
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void leerPost(GoogleMap googleMap,String pais,String ciudad){

        DatabaseReference refId= FirebaseDatabase.getInstance().getReference("Publicaciones")
                .child(pais).child(ciudad);
        DatabaseReference refUsers=FirebaseDatabase.getInstance().getReference("Usuarios");
        refId.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {

                for(DataSnapshot user:snapshot.getChildren()){
                    String username=user.getKey();
                    assert username != null;
                    refId.child(username).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                for (DataSnapshot post : snapshot.getChildren()) {
                                    String postid = post.getKey();
                                    Log.d("IDPOST",postid);

                                    refUsers.child(username).child("publicaciones")
                                            .addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    if(snapshot.exists()){

                                                        assert postid != null;
                                                        String categoria= String.valueOf(snapshot.child(postid).child("categoria")
                                                                .getValue());
                                                        String fechaHora= String.valueOf(snapshot.child(postid).child("fecha")
                                                                .getValue());
                                                        String title= String.valueOf(snapshot.child(postid).child("title")
                                                                .getValue());
                                                        double latitude= (double) snapshot.child(postid).child("latitude")
                                                                .getValue();
                                                        double longitude= (double) snapshot.child(postid).child("longitude")
                                                                .getValue();

                                                        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                                                        String fechaHoraActual = simpleDateFormat.format(new Date());

                                                        try {
                                                            //Lo primero que tienes que hacer es establecer el formato que tiene tu fecha para que puedas obtener un objeto de tipo Date el cual es el que se utiliza para obtener la diferencia.

                                                            //Parceas tus fechas en string a variables de tipo date se agrega un try catch porque si el formato declarado anteriormente no es igual a tu fecha obtendrás una excepción
                                                            Date dateStart = simpleDateFormat.parse(fechaHora);
                                                            Date dateEnd = simpleDateFormat.parse(fechaHoraActual);

                                                            //obtienes la diferencia de las fechas
                                                            assert dateEnd != null;
                                                            assert dateStart != null;
                                                            long difference = Math.abs(dateEnd.getTime() - dateStart.getTime());

                                                            //obtienes la diferencia en horas ya que la diferencia anterior está en milisegundos
                                                            difference = difference / (60 * 60 * 1000);

                                                            if (difference >= 5 || difference <= -1) {//mayor o igual a 5 horas

                                                                refUsers.child(username).child("publicaciones").child(postid).removeValue();
                                                                refId.child(username).child(postid).removeValue();

                                                            } else {
                                                                LatLng posMarker = new LatLng(latitude, longitude);
                                                                BitmapDescriptor icon;
                                                                if (categoria.equals("Desastre Natural")) {
                                                                    icon = bitmapDescriptorFromVector(getActivity(), R.drawable.desastre);
                                                                } else if (categoria.equals("Accidente")) {
                                                                    icon = bitmapDescriptorFromVector(getActivity(), R.drawable.accidente);
                                                                } else {
                                                                    icon = bitmapDescriptorFromVector(getActivity(), R.drawable.entorno);
                                                                }

                                                                Marker post = googleMap.addMarker(new MarkerOptions()
                                                                        .position(posMarker)
                                                                        .title(title)
                                                                        .icon(icon)
                                                                );

                                                                assert post != null;
                                                                post.setTag(postid);
                                                                googleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {

                                                                    @Override
                                                                    public void onCameraChange(@NonNull CameraPosition newPosition) {
                                                                        float currentZoom = 12;
                                                                        post.setVisible(!(newPosition.zoom < currentZoom));
                                                                    }
                                                                });
                                                            }

                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                }
                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        assert vectorDrawable != null;
        vectorDrawable.setBounds(0, 0,100, 100);
        Bitmap bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

}