package com.servidor.util;

import java.beans.XMLDecoder;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.List;
import java.util.logging.Level;

public class DeserializarTarea extends Thread {
    private String rutaArchivo;
    private boolean esXML;
    private List<Object> lista;
    private UtilLog utilLog;

    public DeserializarTarea(String rutaArchivo, boolean esXML, List<Object> lista) {
        this.rutaArchivo = rutaArchivo;
        this.esXML = esXML;
        this.lista = lista;
        this.utilLog = utilLog.getInstance();
    }

    @Override
    public void run() {
        try {
            if (esXML) {
                XMLDecoder decoder = new XMLDecoder(new FileInputStream(rutaArchivo));
                while (true) {
                    try {
                        Object obj = decoder.readObject();
                        lista.add(obj);
                    } catch (ArrayIndexOutOfBoundsException e) {
                        break; // Fin del archivo
                    }
                }
                decoder.close();
            } else {
                try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(rutaArchivo))) {
                    while (true) {
                        try {
                            Object obj = ois.readObject();
                            lista.add(obj);
                        } catch (EOFException e) {
                            break; // Fin del archivo
                        }
                    }
                }
            }
            utilLog.escribirLog("Modelo cargado exitosamente desde: " + rutaArchivo, Level.INFO);
        } catch (Exception e) {
            utilLog.escribirLog("Error al cargar el modelo: " + e.getMessage(), Level.SEVERE);
        }
    }
}