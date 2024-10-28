package com.servidor.excepciones;

//Lanza esta excepción cuando se intenta eliminar 
//una solicitud que no se encuentra en el sistema.

public class SolicitudNoExistenteException extends Exception {
    public SolicitudNoExistenteException() {
        super("No se puede eliminar una solicitud inexistente");
    }
}


