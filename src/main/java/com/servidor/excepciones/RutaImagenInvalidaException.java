package com.servidor.excepciones;

//Lanza esta excepción cuando se intenta registrar un 
//producto con una ruta de imagen no válida o inexistente.

public class RutaImagenInvalidaException extends Exception {
    public RutaImagenInvalidaException() {
        super("Ruta de la imagen invalida o inexistente");
    }
}
