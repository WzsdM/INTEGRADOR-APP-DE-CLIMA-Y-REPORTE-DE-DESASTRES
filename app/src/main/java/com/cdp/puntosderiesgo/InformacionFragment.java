package com.cdp.puntosderiesgo;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class InformacionFragment extends Fragment {

    public InformacionFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_clima, container, false);

        return view;
    }


    //Conexión para la API del clima (falta pasar variables de ubicación)
    private void leerClima() {

        //url de la API call
        String url =
                "https://api.openweathermap.org/data/2.5/weather?lat=44.34&lon=10.99&appid=6608712ab7defffe9f718655de838c58";

        //Request del método GET(lectura de datos)
        StringRequest postRequest=new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            //Capturamos la respuesta del GET en variables
            public void onResponse(String response) {
                //Conversión de json a objeto para extraer los datos
                try {

                    JSONObject jsonObject= new JSONObject(response);
                    int id=jsonObject.getInt("id");//Id de la consulta
                    String clima=jsonObject.getString("main");//Estado del clima actual
                    String description=jsonObject.getString("description");//Descripción del estado del clima actual
                    String icon=jsonObject.getString("icon");//Ícono representativo
                    int temp=jsonObject.getInt("temp");//Temperatura actual
                    int temp_like=jsonObject.getInt("feels_like");//Temperatura percibida
                    int pressure=jsonObject.getInt("pressure");//Presión
                    int humidity=jsonObject.getInt("humidity");//Humedad
                    int wind=jsonObject.getInt("speed");//Velocidad del viento

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
        Volley.newRequestQueue(getContext()).add(postRequest);

    }


}