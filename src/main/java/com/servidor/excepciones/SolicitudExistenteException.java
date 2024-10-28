package com.servidor.excepciones;

//Lanza esta excepción cuando se intenta enviar 
//una solicitud que ya existe en el sistema.

public class SolicitudExistenteException extends Exception {
    public SolicitudExistenteException() {
        super("no se puede reenviar solicitud");
    }
}
