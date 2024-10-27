package com.servidor.excepciones;

// AutenticacionFallidaException Se activa cuando un vendedor 
//o admin no logra autenticarse correctamente (credenciales incorrectas).

public class AutenticacionFallidaException extends Exception {
    public AutenticacionFallidaException() {
        super("Usuario o contraseña incorrecta");
    }
}

