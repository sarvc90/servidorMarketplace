
package com.servidor;

import com.servidor.modelo.Categoria;
import com.servidor.modelo.Estado;
import com.servidor.modelo.EstadoSolicitud;
import com.servidor.modelo.MarketPlace;
import com.servidor.modelo.Producto;
import com.servidor.modelo.Solicitud;
import com.servidor.modelo.Vendedor;
import com.servidor.util.UtilMarketPlace;
import com.servidor.util.UtilSerializar;

import java.util.List;

import com.servidor.excepciones.ProductoNoEncontradoException;
import com.servidor.excepciones.SolicitudExistenteException;
import com.servidor.excepciones.SolicitudNoExistenteException;
import com.servidor.excepciones.UsuarioExistenteException;
import com.servidor.excepciones.UsuarioNoEncontradoException;

public class Main {
    public static void main(String[] args) {
        UtilMarketPlace utilMarketPlace = UtilMarketPlace.getInstance();
        UtilSerializar utilSerializar = UtilSerializar.getInstance();

        MarketPlace marketPlace = utilMarketPlace.getMarketPlace();

        // Vendedor
/* 
        // CREAR
        try {

            Vendedor nuevoVendedor = new Vendedor(null, "Maria", "Arias", "579", "Cll3-02", "contraseña", null,
                    null);
            marketPlace.crearVendedor(nuevoVendedor);
            System.out.println("Vendedor creado exitosamente.");
        } catch (UsuarioExistenteException e) {
            System.out.println("El vendedor ya existe.");
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
            Vendedor vendedorModificado = new Vendedor(null, "Rosa", "Pérez", "12345", "dirección", "contraseña", null,
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
/*  
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
        Solicitud solicitud = new Solicitud("3621", emisor, receptor, EstadoSolicitud.PENDIENTE);
        marketPlace.cambiarEstadoSolicitud(solicitud, EstadoSolicitud.RECHAZADA, receptor);
        System.out.println("VSolicitud modificada exitosamente.");
 /* 
        // LEER
        System.out.println("Lista de solicitudes:");
        marketPlace.obtenerSolicitudes().forEach(s -> {
            System.out.println("Solicitud de: " + s.getEmisor().getNombre() + " a " + s.getReceptor().getNombre());
        });

/* */
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

     /* 
        Producto producto1 = new Producto(null, "Laptop", "descripcion", "2024-10-30T10:15:30", "ruta imagen", 750, 50, null, Estado.PUBLICADO, Categoria.TECNOLOGIA);
        Producto producto2 = new Producto(null, "Teléfono", "descripcion", "2024-10-30T10:15:30", "ruta imagen", 750, 50, null, Estado.VENDIDO, Categoria.TECNOLOGIA);
            
        try{
            System.out.println(vendedor1.getNombre());
            vendedor1.crearProducto(producto1);
            vendedor1.crearProducto(producto2);
        } catch(Exception e){

        }
        /**/

    //ELIMINAR
    try {
        vendedor1.eliminarProducto("0790"); // Usa un ID que exista
        System.out.println("Producto eliminado");
    } catch (ProductoNoEncontradoException e) {
        System.out.println("Error: " + e.getMessage());
    }


        



    }


}
