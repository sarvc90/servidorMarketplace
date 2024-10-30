
package com.servidor;

import com.servidor.modelo.MarketPlace;
import com.servidor.modelo.Vendedor;
import com.servidor.util.UtilMarketPlace;
import com.servidor.util.UtilSerializar;
import com.servidor.excepciones.UsuarioExistenteException;
import com.servidor.excepciones.UsuarioNoEncontradoException;

public class Main {
    public static void main(String[] args) {
        // Inicializa la instancia de UtilMarketPlace
        UtilMarketPlace utilMarketPlace = UtilMarketPlace.getInstance();
        UtilSerializar utilSerializar = UtilSerializar.getInstance();
        
        // Asegúrate de que MarketPlace esté inicializado
        MarketPlace marketPlace = utilMarketPlace.getMarketPlace(); // Esto inicializa marketPlace si es null

        // Ahora puedes llamar a métodos en marketPlace
        /* 
        try {
            // Crear un nuevo vendedor
            Vendedor nuevoVendedor = new Vendedor(null, "Osvaldo", "Polania", "578", "Cll3-02", "contraseña", null, null );
            marketPlace.crearVendedor(nuevoVendedor);
            utilSerializar.serializarObjeto(nuevoVendedor, "persistencia/Vendedores.xml", true);
            System.out.println("Vendedor creado exitosamente.");
        } catch (UsuarioExistenteException e) {
            System.out.println("El vendedor ya existe.");
        }
*/
        
        try{
            String cedulaVendedor = "578";
            marketPlace.eliminarVendedor(cedulaVendedor);
        } catch (UsuarioNoEncontradoException e){
            System.out.println("No se encontro el vendedor");
        }
            
        // Listar vendedores
        System.out.println("Lista de vendedores:");
        marketPlace.obtenerVendedores().forEach(v -> {
            System.out.println("Vendedor: " + v.getNombre() + " " + v.getApellido());
        });

        // Listar productos
        //System.out.println("Lista de productos:");
        //marketPlace.obtenerProductos().forEach(p -> {
            //System.out.println("Producto: " + p.getNombre() + ", Precio: " + p.getPrecio());
        //});

        // Listar solicitudes
        //System.out.println("Lista de solicitudes:");
        //marketPlace.obtenerSolicitudes().forEach(s -> {
            //System.out.println("Solicitud de: " + s.getEmisor().getNombre() + " a " + s.getReceptor().getNombre());
        //});
    }
}