package com.servidor.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;
   

public class UtilRespaldo {
    private static UtilRespaldo instancia;
    private UtilLog utilLog;

    
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

        // Crea el directorio de destino si no existe
        File directorio = new File(directorioDestino);
        if (!directorio.exists()) {
            directorio.mkdirs();
        }

        // Genera un nuevo nombre con la fecha y hora actual
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


}