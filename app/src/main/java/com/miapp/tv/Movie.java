package com.miapp.tv;

import java.io.Serializable;

public class Movie implements Serializable {
    public String id;
    public String titulo;
    public String descripcion;
    public String portada;
    public String url_video;

    public Movie(String id, String titulo, String descripcion, String portada, String url_video) {
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.portada = portada;
        this.url_video = url_video;
    }

    @Override
    public String toString() {
        return titulo;
    }
}
