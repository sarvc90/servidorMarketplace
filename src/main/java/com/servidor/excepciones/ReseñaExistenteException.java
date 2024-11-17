package com.servidor.excepciones;

public class ReseñaExistenteException extends Exception {
    public ReseñaExistenteException() {
        super("Ya existe una reseña con este ID.");
    }
}
