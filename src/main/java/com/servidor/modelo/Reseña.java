package com.servidor.modelo;

import java.io.Serializable;


import com.servidor.util.UtilId;

public class Reseña implements Serializable{
    private String id;
    private Vendedor autor;
    private Vendedor dueño;
    private String texto;

    public Reseña (String id, Vendedor autor, Vendedor dueño, String texto){
        this.id=(id == null || id.isEmpty()) ? UtilId.generarIdAleatorio() : id;
        this.autor=autor;
        this.dueño = dueño;
        this.texto = texto;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Vendedor getAutor() {
        return autor;
    }

    public void setAutor(Vendedor autor) {
        this.autor = autor;
    }

    public Vendedor getDueño() {
        return dueño;
    }

    public void setDueño(Vendedor dueño) {
        this.dueño = dueño;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }
}
