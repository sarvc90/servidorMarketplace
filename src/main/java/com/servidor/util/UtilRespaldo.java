package com.servidor.util;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

public class UtilRespaldo implements Serializable {
    private static UtilRespaldo instancia;
    private UtilLog utilLog;
    private UtilProperties utilProperties; // Instancia de UtilProperties

    private UtilRespaldo() {
        this.utilLog = utilLog.getInstance();
    }

    // Método para obtener la instancia (Singleton)
    public UtilRespaldo getInstance() {
        if (instancia == null) {
            instancia = new UtilRespaldo();
        }
        return instancia;
    }

    public void crearCopiaArchivo(String rutaArchivoOriginal, String directorioDestino) {
        File archivoOriginal = new File(rutaArchivoOriginal);

        // Verifica si el archivo original existe
        if (!archivoOriginal.exists()) {
            utilLog.logSevere("El archivo original no existe.");
            return;
        }

        File directorio = new File(directorioDestino);
        if (!directorio.exists()) {
            directorio.mkdirs();
        }

        String nombreArchivo = archivoOriginal.getName();
        String nombreSinExtension = nombreArchivo.substring(0, nombreArchivo.lastIndexOf('.'));
        String extension = nombreArchivo.substring(nombreArchivo.lastIndexOf('.'));
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String nuevoNombre = nombreSinExtension + "_" + timestamp + extension;

        File archivoCopia = new File(directorio, nuevoNombre);

        // Copia el archivo
        try {
            Files.copy(archivoOriginal.toPath(), archivoCopia.toPath(), StandardCopyOption.REPLACE_EXISTING);
            utilLog.logInfo("Copia creada: " + archivoCopia.getAbsolutePath());
            return;
        } catch (IOException e) {
            utilLog.logSevere("Error al copiar el archivo: " + e.getMessage());
            return;
        }
    }

    public List<String> obtenerRutasArchivosProperties() {

        List<String> rutas = new ArrayList<>();

        for (String key : utilProperties.getAllKeys()) {
            if (!key.contains("log")) {
                String ruta = utilProperties.getProperty(key);
                rutas.add(ruta);
            }
        }
        return rutas;
    }

    public void respaldoGeneral() {
        
        String directorioDestino = utilProperties.getProperty("ruta.respaldo");

        
        if (directorioDestino == null || directorioDestino.isEmpty()) {
            utilLog.logSevere("La ruta de respaldo no está configurada en las propiedades.");
            return;
        }

        List<String> rutasArchivos = obtenerRutasArchivosProperties();

        for (String ruta : rutasArchivos) {
            crearCopiaArchivo(ruta, directorioDestino);
        }
    }
}