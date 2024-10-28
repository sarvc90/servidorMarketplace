package com.servidor.excepciones;

//Esta excepción se lanza cuando un vendedor intenta realizar 
//una acción que requiere tener contactos, pero su lista de contactos está vacía.

public class RedDeContactosVaciaException extends Exception {
    public RedDeContactosVaciaException() {
        super("Su red de contactos esta vacia");
    }
}