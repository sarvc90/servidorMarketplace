package com.servidor.util;

import java.beans.XMLEncoder;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.logging.Level;



public class SerializarTarea extends Thread {
    private Object modelo;
    private String rutaArchivo;
    private boolean esXML;
    private UtilLog utilLog;

    public SerializarTarea(Object modelo, String rutaArchivo, boolean esXML) {
        this.modelo = modelo;
        this.rutaArchivo = rutaArchivo;
        this.esXML = esXML;
        this.utilLog = UtilLog.getInstance();
    }

    @Override
    public void run() {
        try {
            if (esXML) {
                try (FileOutputStream fos = new FileOutputStream(rutaArchivo);
                     XMLEncoder encoder = new XMLEncoder(fos)) {
                    encoder.writeObject(modelo);
                }
            } else {
                try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(rutaArchivo))) {
                    oos.writeObject(modelo);
                }
            }
            utilLog.escribirLog("Modelo guardado exitosamente en: " + rutaArchivo, Level.INFO);
        } catch (Exception e) {
            utilLog.escribirLog("Error al guardar el modelo: " + e.getMessage(), Level.SEVERE);
        }
    }
}