package com.servidor.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.logging.Level;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

public class SerializarTarea extends Thread {
    private Object modelo;
    private String rutaArchivo;
    private boolean esXML;
    private UtilLog utilLog;

    public SerializarTarea(Object modelo, String rutaArchivo, boolean esXML) {
        this.modelo = modelo;
        this.rutaArchivo = rutaArchivo;
        this.esXML = esXML;
        this.utilLog = utilLog.getInstance();
    }

    @Override
    public void run() {
        try {
            if (esXML) {
                JAXBContext contexto = JAXBContext.newInstance(modelo.getClass());
                Marshaller marshaller = contexto.createMarshaller();
                marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
                marshaller.marshal(modelo, new File(rutaArchivo));
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