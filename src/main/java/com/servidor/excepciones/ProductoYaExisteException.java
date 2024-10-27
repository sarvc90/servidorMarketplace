package com.servidor.excepciones;

//Lanza esta excepción cuando un vendedor intenta agregar 
//un producto que ya existe en su lista de productos.

public class ProductoYaExisteException extends Exception {
    public ProductoYaExisteException(String mensaje) {
        super(mensaje);
    }
}
