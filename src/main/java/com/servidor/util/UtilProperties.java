package com.servidor.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class UtilProperties implements Serializable{
    private static UtilProperties instancia;
    private Properties propiedades;
    private UtilLog utilLog;

    public UtilProperties(String rutaArchivo) {
        this.utilLog = utilLog.getInstance();
        propiedades = new Properties();
        try (FileInputStream fis = new FileInputStream(rutaArchivo)) {
            propiedades.load(fis);
        } catch (IOException e) {
            utilLog.logSevere("Error al cargar propiedades: " + e.getMessage());
        }
    }

    public String obtenerPropiedad(String llave) {
        return propiedades.getProperty(llave);
    }
    
    public UtilProperties getInstance() {
        if (instancia == null) {
            String ruta = "resources/config.properties";
            instancia = new UtilProperties(ruta);
        }
        return instancia;
    }

    public List<String> getAllKeys() {
        return new ArrayList<>(propiedades.stringPropertyNames());
    }

    public String getProperty(String llave) {
        return propiedades.getProperty(llave);
    }
}