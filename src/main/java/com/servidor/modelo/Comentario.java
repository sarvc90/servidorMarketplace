package com.servidor.modelo;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import com.servidor.util.UtilPersistencia;



public class Comentario implements Serializable{
    private String id;
    private Vendedor autor;
    private LocalDateTime fechaPublicacion;
    private String texto;
    private static UtilPersistencia utilPersistencia;
    
        // Constructor
        public Comentario(String id, Vendedor autor, LocalDateTime fechaPublicacion, String texto) {
            this.id = id;
            this.autor = autor;
            this.fechaPublicacion = fechaPublicacion;
            this.texto = texto;
            this.utilPersistencia = UtilPersistencia.getInstance();
        }
    
        @Override
        public String toString() {
            return id + ";" + autor.getId() + ";" + fechaPublicacion.toString() + ";" + texto;
        }
        
    
        public Vendedor getAutor() {
            return autor;
        }
    
        public void setAutor(Vendedor autor) {
            this.autor = autor;
        }
    
        public LocalDateTime getFechaPublicacion() {
            return fechaPublicacion;
        }
    
        public void setFechaPublicacion(LocalDateTime fechaPublicacion) {
            this.fechaPublicacion = fechaPublicacion;
        }
    
        public String getTexto() {
            return texto;
        }
    
        public void setTexto(String texto) {
            this.texto = texto;
        }
    
        public String getId() {
            return id;
        }
    
        public void setId(String id) {
            this.id = id;
        }
    
        public static Comentario fromString(String cadena) {
            String[] datos = cadena.split(";");
            
            if (datos.length != 4) {
                throw new IllegalArgumentException("Cadena de Comentario mal formada: " + cadena);
            }
        
            String id = datos[0];
            String autorId = datos[1];
            LocalDateTime fechaPublicacion = LocalDateTime.parse(datos[2]);
            String texto = datos[3];
        
            // Aquí supongo que puedes obtener un objeto Vendedor a partir de su id
            Vendedor autor = obtenerVendedorPorId(autorId); // Implementar este método según tu lógica de negocio
        
            return new Comentario(id, autor, fechaPublicacion, texto);
        }
        
        // Método ficticio para obtener un Vendedor por su ID (debes implementar esto según tu lógica)
        private static Vendedor obtenerVendedorPorId(String id) {
            List<Vendedor> listaVendedores = utilPersistencia.leerVendedoresDesdeArchivo();
        for (Vendedor vendedor : listaVendedores) {
            if (vendedor.getId().equals(id)) {
                return vendedor; // Retorna el vendedor encontrado
            }
        }
        return null;
    }
    
    
}
