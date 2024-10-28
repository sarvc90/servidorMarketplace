package com.servidor.excepciones;

//Lanza esta excepción cuando un vendedor intenta comentar en un producto de otro vendedor 
//que no está en su red de contactos o cuando el producto no está en estado "publicado".

public class ComentarioNoPermitidoException extends Exception {
    public ComentarioNoPermitidoException() {
        super("No puede comentar productos publicados por su red de contactos");
    }
}
