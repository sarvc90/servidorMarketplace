
package com.servidor;

import com.servidor.modelo.Categoria;
import com.servidor.modelo.Estado;
import com.servidor.modelo.EstadoSolicitud;
import com.servidor.modelo.MarketPlace;
import com.servidor.modelo.Producto;
import com.servidor.modelo.Reseña;
import com.servidor.modelo.Solicitud;
import com.servidor.modelo.Vendedor;
import com.servidor.util.UtilMarketPlace;
import com.servidor.util.UtilSerializar;

import java.util.List;
import java.util.ArrayList;

import com.servidor.excepciones.ProductoNoEncontradoException;
import com.servidor.excepciones.ReseñaExistenteException;
import com.servidor.excepciones.SolicitudExistenteException;
import com.servidor.excepciones.SolicitudNoExistenteException;
import com.servidor.excepciones.ReseñaNoEncontradaException;
import com.servidor.excepciones.UsuarioExistenteException;
import com.servidor.excepciones.UsuarioNoEncontradoException;

public class Main {
    public static void main(String[] args) {
        UtilMarketPlace utilMarketPlace = UtilMarketPlace.getInstance();

        MarketPlace marketPlace = utilMarketPlace.getMarketPlace();

        System.out.println("Lista de reseñas:");
        marketPlace.obtenerReseñas().forEach(r -> {
            System.out.println("Reseña: " + r.getTexto() + " de " + r.getAutor().getNombre());
        });

        }

        
        /*
        // CREAR Reseña
        try {
            marketPlace.crearReseña(reseña);
            System.out.println("Reseña creada exitosamente: " + reseña.getTexto());
        } catch (ReseñaExistenteException e) {
            System.out.println("La reseña ya existe.");
        } }
/*
        // LEER Reseñas
        System.out.println("Lista de reseñas:");
        marketPlace.getReseñas().forEach(r -> {
            System.out.println("Reseña: " + r.getTexto() + " de " + r.getAutor().getNombre());
        });

        // MODIFICAR Reseña
        try {
            Reseña reseñaModificada = new Reseña("1", autor, dueño, "todo excelente.");
            marketPlace.modificarReseña(reseñaModificada);
            System.out.println("Reseña modificada exitosamente.");
        } catch (ReseñaNoEncontradaException e) {
            System.out.println("No se encontró la reseña para modificar.");
        }

        // ELIMINAR Reseña
        try {
            String idReseñaAEliminar = "1";
            marketPlace.eliminarReseña(idReseñaAEliminar);
            System.out.println("Reseña eliminada exitosamente.");
        } catch (ReseñaNoEncontradaException e) {
            System.out.println("No se encontró la reseña para eliminar.");
        }
        
        // Leer nuevamente las reseñas
        System.out.println("Lista de reseñas después de la eliminación:");
        marketPlace.getReseñas().forEach(r -> {
            System.out.println("Reseña: " + r.getTexto() + " de " + r.getAutor().getNombre());
        });
    }
/* 

        // Vendedor
 
        // CREAR
        try {

            Vendedor nuevoVendedor = new Vendedor(null, "alfonso", "Cantinflas", "876", "Cll3-1010", "contraseña", null, null, null, 0, 0);
            marketPlace.crearVendedor(nuevoVendedor);
            System.out.println("Vendedor creado exitosamente.");
        } catch (UsuarioExistenteException e) {
            System.out.println("El vendedor ya existe.");
        }
     */

/* 
    public static void main(String[] args) {
        // Crear una lista de publicaciones (puede estar vacía inicialmente)
        List<Producto> publicaciones = new ArrayList<>();

        // Crear una lista de contactos (puede estar vacía inicialmente)
        List<Vendedor> redDeContactos = new ArrayList<>();

        // Crear una lista de calificaciones (puede estar vacía inicialmente)
        List<Integer> calificaciones = new ArrayList<>();

        // Crear instancias de Vendedor con todos los parámetros requeridos
        Vendedor autor = new Vendedor(
            "AutorID", 
            "Nombre del Autor", 
            "Apellido del Autor", 
            "123456789", // Cédula
            "Dirección del Autor", 
            "ContraseñaSegura", 
            publicaciones, 
            redDeContactos, 
            calificaciones, 
            0, // Contador de calificaciones
            0.0 // Promedio de calificaciones
        );

        Vendedor dueño = new Vendedor(
            "DueñoID", 
            "Nombre del Dueño", 
            "Apellido del Dueño", 
            "987654321", // Cédula
            "Dirección del Dueño", 
            "ContraseñaSegura", 
            publicaciones, 
            redDeContactos, 
            calificaciones, 
            0, // Contador de calificaciones
            0.0 // Promedio de calificaciones
        );

        // Crear una reseña (asumiendo que tienes una clase Reseña)
        Reseña reseña = new Reseña("1", autor, dueño, "muy bien todo.");

        // Imprimir la reseña
        System.out.println("Reseña creada: " + reseña.getTexto());
    }

        

/* 
        // ELIMINAR
        try {
            String cedulaVendedor = "12345";
            marketPlace.eliminarVendedor(cedulaVendedor);
        } catch (UsuarioNoEncontradoException e) {
            System.out.println("No se encontro el vendedor");
        }

        // LEER
        System.out.println("Lista de vendedores:");
        marketPlace.obtenerVendedores().forEach(v -> {
            System.out.println("Vendedor: " + v.getNombre() + " " + v.getApellido());
        });
/*  

        // MODIFICAR
        try {
            Vendedor vendedorModificado = new Vendedor(null, "Osvaldo", "Flores", "578", "dirección", "contraseña", null,
                    null);
            marketPlace.modificarVendedor(vendedorModificado);
        } catch (UsuarioNoEncontradoException e) {
            System.out.println("No se encontro el vendedor");
        }

        // Listar productos
        System.out.println("Lista de productos:");
        marketPlace.obtenerProductos().forEach(p -> {
            System.out.println("Producto: " + p.getNombre() + ", Precio: " + p.getPrecio());
        });
/* 
        // SOLICITUDES
        // CREAR
        try {
            List<Vendedor> vendedores = marketPlace.getVendedores();
            Vendedor emisor = null;
            Vendedor receptor = null;

            if (vendedores.size() > 0) {
                emisor = vendedores.get(0); // Asignar el primer vendedor a emisor
            }

            if (vendedores.size() > 1) {
                receptor = vendedores.get(1); // Asignar el segundo vendedor a receptor
            }
            Solicitud nuevaSolicitud = new Solicitud(null, emisor, receptor, null);
            marketPlace.crearSolicitud(nuevaSolicitud);
            System.out.println("Solicitud creado exitosamente.");
        } catch (SolicitudExistenteException e) {
            System.out.println("Ya hay solicitud existente entre los dos usuarios");

        }
    /*
        // ELIMINAR
 
        try {
            List<Vendedor> vendedores = marketPlace.getVendedores();
            Vendedor emisor = null;
            Vendedor receptor = null;

            if (vendedores.size() > 0) {
                emisor = vendedores.get(0); // Asignar el primer vendedor a emisor
            }

            if (vendedores.size() > 1) {
                receptor = vendedores.get(1); // Asignar el segundo vendedor a receptor
            }
            marketPlace.eliminarSolicitud(emisor, receptor);
            System.out.println("Solicitud eliminado exitosamente.");
        } catch (SolicitudNoExistenteException e) {
            System.out.println("No existe la solicitud");

        }

      // MODIFICAR
        List<Vendedor> vendedores = marketPlace.getVendedores();
        Vendedor emisor = null;
        Vendedor receptor = null;

        if (vendedores.size() > 0) {
            emisor = vendedores.get(0); // Asignar el primer vendedor a emisor
        }

        if (vendedores.size() > 1) {
            receptor = vendedores.get(1); // Asignar el segundo vendedor a receptor
        }
        Solicitud solicitud = new Solicitud("9647", emisor, receptor, EstadoSolicitud.PENDIENTE);
        marketPlace.cambiarEstadoSolicitud(solicitud, EstadoSolicitud.ACEPTADA, receptor);
        System.out.println("Solicitud modificada exitosamente.");
 /* 
        // LEER
        System.out.println("Lista de solicitudes:");
        marketPlace.obtenerSolicitudes().forEach(s -> {
            System.out.println("Solicitud de: " + s.getEmisor().getNombre() + " a " + s.getReceptor().getNombre());
        });

/* 
    //PRODUCTOS
        //CREAR
        List<Vendedor> vendedores = marketPlace.getVendedores();
        Vendedor vendedor1 = null;
        Vendedor vendedor2 = null;

        if (vendedores.size() > 0) {
            vendedor1 = vendedores.get(0); // Asignar el primer vendedor a emisor
        }

        if (vendedores.size() > 2) {
            vendedor2 = vendedores.get(2); // Asignar el segundo vendedor a receptor
        }

     
        Producto producto1 = new Producto(null, "Laptop", "descripcion", "2024-10-30T10:15:30", "ruta imagen", 750, 50, null, Estado.PUBLICADO, Categoria.TECNOLOGIA,null);
        Producto producto2 = new Producto(null, "Teléfono", "descripcion", "2024-10-30T10:15:30", "ruta imagen", 750, 50, null, Estado.VENDIDO, Categoria.TECNOLOGIA,null);
            
        try{
            System.out.println(vendedor1.getNombre());
            vendedor1.crearProducto(producto1);
            vendedor1.crearProducto(producto2);
        } catch(Exception e){

        }
        /*

    //ELIMINAR
    try {
        vendedor1.eliminarProducto("0790"); // Usa un ID que exista
        System.out.println("Producto eliminado");
    } catch (ProductoNoEncontradoException e) {
        System.out.println("Error: " + e.getMessage());
    }


    

//MODIFICAR
        Producto productoModificado = new Producto("6970", "Teléfono", "descripcion", "2024-10-30T10:15:30", "ruta imagen", 750, 50, null, Estado.VENDIDO, Categoria.TECNOLOGIA);
        vendedor1.modificarProducto(productoModificado);


        // LEER
        System.out.println("Lista de productos:");
        marketPlace.obtenerProductos().forEach(s -> {
            System.out.println("Producto" + s.getNombre());
        });
*//*
    utilMarketPlace.exportarEstadisticas("/persistencia/log","Usuario ejemplo", "01/01/2024", "31/12/2024", "4285");
*/
}


