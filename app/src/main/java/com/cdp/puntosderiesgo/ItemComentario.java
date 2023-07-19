package com.cdp.puntosderiesgo;

public class ItemComentario {
    String userProfile;
    String username;
    String horaComentario;
    String comentario;

    public ItemComentario(String userProfile, String username, String horaComentario, String comentario) {
        this.userProfile = userProfile;
        this.username = username;
        this.horaComentario = horaComentario;
        this.comentario = comentario;
    }

    public String getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(String userProfile) {
        this.userProfile = userProfile;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getHoraComentario() {
        return horaComentario;
    }

    public void setHoraComentario(String horaComentario) {
        this.horaComentario = horaComentario;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }
}
