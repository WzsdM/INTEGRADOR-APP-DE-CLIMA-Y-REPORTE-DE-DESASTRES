package com.cdp.puntosderiesgo.clases;

public class Post {
    String id,titulo,detalle,categoria,ciudad,pais;
    double latitude,longitude;

    public Post() {
    }

    public Post(String id, String titulo, String detalle, String categoria, String ciudad, String pais, double latitude, double longitude) {
        this.id = id;
        this.titulo = titulo;
        this.detalle = detalle;
        this.categoria = categoria;
        this.ciudad = ciudad;
        this.pais = pais;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDetalle() {
        return detalle;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
    public String toString(String nombre){
        return nombre;
    }
}
