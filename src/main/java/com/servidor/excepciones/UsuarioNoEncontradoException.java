package com.servidor.excepciones;

// UsuarioNoEncontradoException Lanza esta excepción cuando 
//un vendedor intenta buscar a otro vendedor que no existe en el sistema.

public class UsuarioNoEncontradoException extends Exception {
    public UsuarioNoEncontradoException(String mensaje) {
        super("Usuario no encontrado en el sistema");
    }
}
