package com.servidor.util;

public class UtilHilos {
    private static UtilHilos instancia;


    public UtilHilos getInstance() {
        if (instancia == null) {
            instancia = new UtilHilos();
        }
        return instancia;
    }
}
