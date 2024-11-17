package com.servidor.excepciones;

public class ReseñaNoEncontradaException extends Exception {
    public ReseñaNoEncontradaException() {
        super("No se encontró una reseña con el ID especificado.");
    }
}

