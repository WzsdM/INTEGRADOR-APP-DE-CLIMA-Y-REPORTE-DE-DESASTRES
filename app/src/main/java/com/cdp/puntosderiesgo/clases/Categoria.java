package com.cdp.puntosderiesgo.clases;

public class Categoria {
    String nombre;

    public Categoria(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public String toString(){
        return nombre;
    }
}
