package com.servidor.util;

import java.io.Serializable;
import java.util.List;

import com.servidor.excepciones.ProductoNoEncontradoException;
import com.servidor.excepciones.ProductoYaExisteException;
import com.servidor.modelo.Producto;
import com.servidor.modelo.Solicitud;
import com.servidor.modelo.Vendedor;


public class UtilVendedor implements Serializable{
    private static UtilVendedor instancia;
    private UtilLog utilLog;
    private UtilPersistencia utilPersistencia;
    private UtilSerializar utilSerializar;

    private UtilVendedor() {
        this.utilLog = UtilLog.getInstance();
        this.utilPersistencia = UtilPersistencia.getInstance();
        this.utilSerializar = UtilSerializar.getInstance();
    }

    public static UtilVendedor getInstance() {
        if (instancia == null) {
            instancia = new UtilVendedor();
        }
        return instancia;
    }

    public boolean crearProducto(Producto producto) throws ProductoYaExisteException {
        if (utilPersistencia.buscarProductoPorId(producto.getId()) == null) {
            utilPersistencia.guardarProductoEnArchivo(producto);
            utilSerializar.actualizarSerializacionProductos();
            utilLog.registrarAccion("Vendedor ", "Producto agregado con exito.", "Muro");
            return true;
        } else {
            // Excepcion de usuario existente
            utilLog.registrarAccion("Vendedor ", "El producto no pudo agregar con éxito ", "Muro");
            throw new ProductoYaExisteException();
        }

    }
//Se pedira que ingrese el id para confirmar
    public boolean eliminarProducto(String productoId) throws ProductoNoEncontradoException{
        if (utilPersistencia.buscarProductoPorId(productoId) == null) {
            utilPersistencia.eliminarProducto(productoId);
            utilSerializar.actualizarSerializacionProductos();
            utilLog.registrarAccion("Vendedor",
                    " El producto con id " + productoId + " ha sido eliminado. ", " Eliminación.");
            return true;
        } else {
            // Excepcion de usuario no encontrado
            utilLog.registrarAccion("El producto no fue encontrado. ", " Eliminación fallida. ", " Eliminación.");
            throw new ProductoNoEncontradoException();
        }

    }

    public void modificarProducto(Producto productoModificado) {
        utilPersistencia.modificarProducto(productoModificado);
        utilSerializar.actualizarSerializacionProductos();
        utilLog.registrarAccion("Vendedor",
                " Se modifica el producto ", " Modificar.");
        
    }

    public List<Solicitud> obtenerSolicitudesAceptadas(Vendedor vendedor){
        return utilPersistencia.buscarSolicitudesAceptadasPorVendedor(vendedor);
    }

    
    public List<Solicitud> obtenerSolicitudesRechazadas(Vendedor vendedor){
        return utilPersistencia.buscarSolicitudesRechazadasPorVendedor(vendedor);
    }

    public List<Solicitud> obtenerSolicitudesPendientes(Vendedor vendedor){
        return utilPersistencia.buscarSolicitudesPendientesPorVendedor(vendedor);
    }
}
