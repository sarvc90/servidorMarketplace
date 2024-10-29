package com.servidor.util;

import java.io.Serializable;

public class UtilHilos implements Serializable{
    private static UtilHilos instancia;

// Se crea la unica intancia de la clase 
    public UtilHilos getInstance() {
        if (instancia == null) {
            instancia = new UtilHilos();
        }
        return instancia;
    }
}
