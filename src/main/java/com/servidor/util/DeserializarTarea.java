package com.servidor.util;

import java.beans.XMLDecoder;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;
import java.util.logging.Level;

import com.servidor.modelo.Producto;
import com.servidor.modelo.Solicitud;
import com.servidor.modelo.Vendedor;

public class DeserializarTarea extends Thread {
    private String rutaArchivo;
    private boolean esXML;
    private List<Object> lista;
    private UtilLog utilLog;

    public DeserializarTarea(String rutaArchivo, boolean esXML, List<Object> lista) {
        this.rutaArchivo = rutaArchivo;
        this.esXML = esXML;
        this.lista = lista;
        this.utilLog = UtilLog.getInstance();
    }

    @Override
    public void run() {
        try {
            if (esXML) {
                try (XMLDecoder decoder = new XMLDecoder(new FileInputStream(rutaArchivo))) {
                    Object obj = decoder.readObject();
                    if (obj instanceof List<?>) {
                        for (Object item : (List<?>) obj) {
                            if (item instanceof Vendedor || item instanceof Producto || item instanceof Solicitud) {
                                lista.add(item);
                            } else {
                                utilLog.escribirLog("Objeto deserializado no es una instancia válida: " + item.getClass().getName(), Level.WARNING);
                            }
                        }
                    } else {
                        // Manejo normal si no es una lista
                        if (obj instanceof Vendedor || obj instanceof Producto || obj instanceof Solicitud) {
                            lista.add(obj);
                        } else {
                            utilLog.escribirLog("Objeto deserializado no es una instancia válida: " + obj.getClass().getName(), Level.WARNING);
                        }
                    }
                } catch (FileNotFoundException e) {
                    utilLog.escribirLog("Archivo no encontrado: " + rutaArchivo, Level.SEVERE);
                } catch (IOException e) {
                    utilLog.escribirLog("Error de entrada/salida al leer el archivo XML: " + e.getMessage(), Level.SEVERE);
                }
            } else {
                try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(rutaArchivo))) {
                    Object obj;
                    while ((obj = ois.readObject()) != null) {
                        if (obj instanceof Vendedor || obj instanceof Producto || obj instanceof Solicitud) {
                            lista.add(obj);
                        } else {
                            utilLog.escribirLog("Objeto deserializado no es una instancia válida: " + obj.getClass().getName(), Level.WARNING);
                        }
                    }
                } catch (EOFException e) {
                    // Fin de archivo alcanzado
                } catch (FileNotFoundException e) {
                    utilLog.escribirLog("Archivo no encontrado: " + rutaArchivo, Level.SEVERE);
                } catch (IOException | ClassNotFoundException e) {
                    utilLog.escribirLog("Error durante la deserialización: " + e.getMessage(), Level.SEVERE);
                }
            }
            utilLog.escribirLog("Modelo cargado exitosamente desde: " + rutaArchivo, Level.INFO);
        } catch (Exception e) {
            utilLog.escribirLog("Error general al cargar el objeto deserializado: " + e.getMessage(), Level.SEVERE);
        }
    }
}    