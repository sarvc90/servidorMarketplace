package com.servidor.util;

import java.beans.XMLDecoder;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;
import java.util.logging.Level;

//Clase que representa una tarea para deserializar objetos desde un archivo.
public class DeserializarTarea extends Thread {
    private String rutaArchivo;
    private boolean esXML;
    private List<Object> lista;
    private UtilLog utilLog;

    // Constructor de la clase DeserializarTarea.
    public DeserializarTarea(String rutaArchivo, boolean esXML, List<Object> lista) {
        this.rutaArchivo = rutaArchivo;
        this.esXML = esXML;
        this.lista = lista;
        this.utilLog = UtilLog.getInstance();
    }

    @Override
    public void run() {
        try {
            // Si el archivo es XML, se utiliza XMLDecoder para deserializar
            if (esXML) {
                try (XMLDecoder decoder = new XMLDecoder(new FileInputStream(rutaArchivo))) {
                    while (true) {
                        try {
                            Object obj = decoder.readObject();
                            lista.add(obj);
                        } catch (ArrayIndexOutOfBoundsException e) {
                            break;
                        }
                    }
                } catch (FileNotFoundException e) {
                    utilLog.escribirLog("Archivo no encontrado: " + rutaArchivo, Level.SEVERE);
                    e.printStackTrace();
                } catch (IOException e) {
                    utilLog.escribirLog("Error de entrada/salida al leer el archivo XML: " + e.getMessage(),
                            Level.SEVERE);
                    e.printStackTrace();
                } catch (Exception e) {
                    utilLog.escribirLog("Error inesperado durante la deserialización XML: " + e.getMessage(),
                            Level.SEVERE);
                    e.printStackTrace();
                }
            } else {
                // Si el archivo es binario, se utiliza ObjectInputStream
                try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(rutaArchivo))) {
                    while (true) {
                        try {
                            Object obj = ois.readObject();
                            lista.add(obj);
                        } catch (EOFException e) {
                            break;
                        } catch (ClassNotFoundException e) {
                            utilLog.escribirLog("Clase no encontrada durante la deserialización: " + e.getMessage(),
                                    Level.SEVERE);
                            e.printStackTrace();
                        } catch (IOException e) {
                            utilLog.escribirLog("Error de entrada/salida al leer el archivo binario: " + e.getMessage(),
                                    Level.SEVERE);
                            e.printStackTrace();
                        } catch (Exception e) {
                            utilLog.escribirLog(
                                    "Error inesperado durante la deserialización binaria: " + e.getMessage(),
                                    Level.SEVERE);
                            e.printStackTrace();
                        }
                    }
                } catch (FileNotFoundException e) {
                    utilLog.escribirLog("Archivo no encontrado: " + rutaArchivo, Level.SEVERE);
                    e.printStackTrace();
                } catch (IOException e) {
                    utilLog.escribirLog("Error de entrada/salida al abrir el archivo binario: " + e.getMessage(),
                            Level.SEVERE);
                    e.printStackTrace();
                } catch (Exception e) {
                    utilLog.escribirLog("Error inesperado al abrir el archivo binario: " + e.getMessage(),
                            Level.SEVERE);
                    e.printStackTrace();
                }
            }
            utilLog.escribirLog("Modelo cargado exitosamente desde: " + rutaArchivo, Level.INFO);
        } catch (Exception e) {
            utilLog.escribirLog("Error general al cargar el modelo: " + e.getMessage(), Level.SEVERE);
            e.printStackTrace(); // Agrega esto para ver el stack trace
        }
    }
}