package com.servidor.excepciones;

public class ProductoNoEncontradoException extends Exception {
    public ProductoNoEncontradoException() {
        super("Producto inexistente");
    }
}
