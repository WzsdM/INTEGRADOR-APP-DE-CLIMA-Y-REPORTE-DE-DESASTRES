package com.cdp.puntosderiesgo.clases;

public class Post {
    String id;
    String title;
    String detalle;
    String categoria;
    double latitude,longitude;
    String photo;

    public Post() {
    }

    public Post(String id, String title, String detalle, String categoria, double latitude, double longitude,String photo) {
        this.id = id;
        this.title = title;
        this.detalle = detalle;
        this.categoria = categoria;
        this.latitude = latitude;
        this.longitude = longitude;
        this.photo=photo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String toString(String nombre){
        return nombre;
    }
}
