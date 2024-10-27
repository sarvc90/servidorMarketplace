package com.servidor.excepciones;

// UsuarioExistenteException Lanza esta excepción cuando una persona 
//intenta crear una cuenta con un numero de cedula que fue utilizado en otra cuenta.

public class UsuarioExistenteException extends Exception {
    public UsuarioExistenteException() {
        super("Ya existe un usuario registrado con el número de cédula proporcionado.");
    }
}

