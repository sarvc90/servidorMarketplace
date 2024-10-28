package com.servidor.excepciones;

//Esta excepción se lanza cuando ocurre un error al intentar guardar o 
//cargar datos desde el sistema de persistencia.

public class ErrorDePersistenciaException extends Exception {
    public ErrorDePersistenciaException() {
        super("Error de persistencia");
    }
}