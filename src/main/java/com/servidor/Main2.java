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

import com.servidor.excepciones.ProductoYaExisteException;
import com.servidor.excepciones.ProductoNoEncontradoException;
import com.servidor.excepciones.SolicitudExistenteException;
import com.servidor.excepciones.SolicitudNoExistenteException;
import com.servidor.excepciones.UsuarioExistenteException;
import com.servidor.excepciones.UsuarioNoEncontradoException;

public class Main2 {
    public static void main(String[] args) {
        UtilMarketPlace utilMarketPlace = UtilMarketPlace.getInstance();
        UtilSerializar utilSerializar = UtilSerializar.getInstance();

        MarketPlace marketPlace = utilMarketPlace.getMarketPlace();



        // Productos
        try {
            // CREAR productos
            Producto producto1 = new Producto("111", "Laptop", "descripcion", "fecha", "ruta imagen", 750, 50, Estado.PUBLICADO, Categoria.TECNOLOGIA);
            Producto producto2 = new Producto(null, "Teléfono", "descripcion", "fecha", "ruta imagen", 750, 50, Estado.VENDIDO, Categoria.TECNOLOGIA);
            List<Vendedor> vendedores=marketPlace.obtenerVendedores();
            Vendedor vendedor1 = vendedores.get(0);
            vendedor1.crearProducto(producto1);
            vendedor1.crearProducto(producto2);
            System.out.println("Productos creados exitosamente.");

            // LISTAR productos
            System.out.println("Lista de productos:");
            marketPlace.obtenerProductos().forEach(p -> {
                System.out.println("Producto: " + p.getNombre() + ", Precio: " + p.getPrecio());
            });

            System.out.println("Lista de productos:");
            vendedor1.getPublicaciones().forEach(p -> {
                System.out.println("Producto: " + p.getNombre() + ", Precio: " + p.getPrecio());
            });


            // MODIFICAR producto
            Producto productoModificado = new Producto("111", "Iphone", "descripcion", "fecha", "ruta imagen", 750, 50, Estado.VENDIDO, Categoria.TECNOLOGIA);;
            vendedor1.modificarProducto(productoModificado);
            System.out.println("Producto modificado exitosamente.");

            // ELIMINAR producto
            vendedor1.eliminarProducto("101");
            System.out.println("Producto eliminado exitosamente.");

            // LISTAR productos después de eliminar
            System.out.println("Lista de productos después de eliminar:");
            marketPlace.obtenerProductos().forEach(p -> {
                System.out.println("Producto: " + p.getNombre() + ", Precio: " + p.getPrecio());
            });
        } catch (ProductoYaExisteException e) {
            System.out.println("El producto ya existe.");
        } catch (ProductoNoEncontradoException e) {
            System.out.println("No se encontró el producto.");
        }

        // Solicitudes
        try {
            List<Vendedor> vendedores = marketPlace.getVendedores();
            if (vendedores.size() > 1) {
                Vendedor emisor = vendedores.get(0);
                Vendedor receptor = vendedores.get(1);
                Solicitud nuevaSolicitud = new Solicitud(null, emisor, receptor, null);
                marketPlace.crearSolicitud(nuevaSolicitud);
                System.out.println("Solicitud creada exitosamente.");

                // LISTAR solicitudes
                System.out.println("Lista de solicitudes:");
                marketPlace.obtenerSolicitudes().forEach(s -> {
                    System.out.println("Solicitud de: " + s.getEmisor().getNombre() + " a " + s.getReceptor().getNombre());
                });

                // ELIMINAR solicitud
                marketPlace.eliminarSolicitud(emisor, receptor);
                System.out.println("Solicitud eliminada exitosamente.");
            }
        } catch (SolicitudExistenteException e) {
            System.out.println("Ya hay solicitud existente entre los dos usuarios.");
        } catch (SolicitudNoExistenteException e) {
            System.out.println("No existe la solicitud.");
        }
    }
}
