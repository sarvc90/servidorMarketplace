package com.servidor.excepciones;

//Lanza esta excepción cuando se intenta acceder 
//a un contacto que no existe en la red del vendedor.

public class ContactoNoEncontradoException extends Exception {
    public ContactoNoEncontradoException(String mensaje) {
        super("Usuario inexistente en su red de contactos");
    }
}
