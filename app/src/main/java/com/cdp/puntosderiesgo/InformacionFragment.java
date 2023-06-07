package com.cdp.puntosderiesgo;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

public class InformacionFragment extends Fragment {

    TextView v_result;
    String output;
    DecimalFormat df= new DecimalFormat("#.##");

    public InformacionFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_clima, container, false);

        v_result=view.findViewById(R.id.txtResult);
                getParentFragmentManager().setFragmentResultListener("requestKey", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                double latubicacion = result.getDouble("latitud");
                double lngubicacion = result.getDouble("longitud");
                leerClima(latubicacion,lngubicacion, view);
            }
        });
        return view;
    }


    //Conexión para la API del clima (falta pasar variables de ubicación)
    private void leerClima(double latubicacion, double lngubicacion, View view) {

        String latString= String.valueOf(latubicacion);
        String lngString= String.valueOf(lngubicacion);
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
                    JSONArray jsonArray = jsonObject.getJSONArray("weather");
                    JSONObject jsonObjectWeather=jsonArray.getJSONObject(0);
                    JSONObject jsonObjectMain=jsonObject.getJSONObject("main");
                    JSONObject jsonObjectWind=jsonObject.getJSONObject("wind");
                    JSONObject jsonObjectClouds=jsonObject.getJSONObject("clouds");
                    JSONObject jsonObjectSys=jsonObject.getJSONObject("sys");

                    double id=jsonObjectWeather.getDouble("id");//Id de la consulta
                    String clima=jsonObjectWeather.getString("main");//Estado del clima actual
                    String description=jsonObjectWeather.getString("description");//Descripción del estado del clima actual
                    String icon=jsonObjectWeather.getString("icon");//Ícono representativo

                    double temp=jsonObjectMain.getDouble("temp");//Temperatura actual
                    double temp_like=jsonObjectMain.getDouble("feels_like");//Temperatura percibida
                    double pressure=jsonObjectMain.getDouble("pressure");//Presión
                    double humidity=jsonObjectMain.getDouble("humidity");//Humedad

                    String countryName=jsonObjectSys.getString("country");//Pais

                    String cityName=jsonObject.getString("name");//Ciudad

                    String clouds=jsonObjectClouds.getString("all");//Nubes

                    double wind=jsonObjectWind.getDouble("speed");//Velocidad del viento

                    output="Clima actual de "+cityName+"("+countryName+")"
                    +"\n Estado : "+clima
                    +"\n Descripción : "+description
                    +"\n Temperatura : "+temp
                    +"\n Temperatura Sentida : "+temp_like
                    +"\n Presión : "+pressure
                    +"\n Humedad : "+humidity
                    +"\n Nubes : "+clouds
                    +"\n Velocidad del Viento : "+wind;

                    v_result.setText("");
                    v_result.setText(output);

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //enviar error si en caso hubiera
                Log.e("error",error.getMessage());

            }
        });

        //enviar la petición
        RequestQueue requestQueue = Volley.newRequestQueue(view.getContext());
        requestQueue.add(postRequest);
    }


}