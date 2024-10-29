package com.servidor.util;

import java.io.Serializable;

public class UtilHilos implements Serializable{
    private static UtilHilos instancia;


    public UtilHilos getInstance() {
        if (instancia == null) {
            instancia = new UtilHilos();
        }
        return instancia;
    }
}
