package com.servidor.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import javax.xml.bind.annotation.XmlRootElement;

import com.servidor.excepciones.ProductoNoEncontradoException;
import com.servidor.excepciones.ProductoYaExisteException;
import com.servidor.modelo.MarketPlace;
import com.servidor.modelo.Producto;
import com.servidor.modelo.Solicitud;
import com.servidor.modelo.Vendedor;

//Clase UtilVendedor que proporciona métodos y utilidades para gestionar 
//operaciones relacionadas con los vendedores en la aplicación
//Implementa el patrón Singleton para garantizar una única 
//instancia en el sistema.
@XmlRootElement
public class UtilVendedor implements Serializable {
    private static UtilVendedor instancia;
    private UtilLog utilLog;
    private UtilPersistencia utilPersistencia;
    private UtilSerializar utilSerializar;

    // Constructor privado para implementar el patrón Singleton, inicializando
    // las instancias de UtilLog, UtilPersistencia y UtilSerializar.
    private UtilVendedor() {
        this.utilLog = UtilLog.getInstance();
        this.utilPersistencia = UtilPersistencia.getInstance();
        this.utilSerializar = UtilSerializar.getInstance();
    }

    // Devuelve la instancia única de UtilVendedor, siguiendo el patrón Singleton.
    public static UtilVendedor getInstance() {
        if (instancia == null) {
            instancia = new UtilVendedor();
        }
        return instancia;
    }

    // Crea un nuevo producto en el sistema.
    public boolean crearProducto(Producto producto, Vendedor vendedor) throws ProductoYaExisteException {
        if (utilPersistencia.buscarProductoPorId(producto.getId()) == null) {
            utilLog.escribirLog("Guardando producto en archivo: " + producto, Level.INFO);
            utilPersistencia.guardarProductoEnArchivo(producto);

            utilLog.escribirLog("Actualizando serialización de productos", Level.INFO);
            utilSerializar.actualizarSerializacionProductos();

            utilLog.escribirLog("Agregando producto al vendedor: " + vendedor, Level.INFO);
            utilPersistencia.agregarProductoAVendedor(producto, vendedor);

            utilLog.escribirLog("Gestionando archivos por estado", Level.INFO);
            utilPersistencia.gestionarArchivosPorEstado(utilPersistencia.leerProductosDesdeArchivo(),
                    utilPersistencia.leerSolicitudesDesdeArchivo());

            utilLog.registrarAccion("Vendedor ", "Producto agregado con éxito.", "Muro");
            return true;
        } else {
            // Si el producto con el ID especificado ya existe,
            // lanza una excepción de tipo ProductoYaExisteException.
            utilLog.registrarAccion("Vendedor ", "El producto no se pudo agregar con éxito", "Muro");
            throw new ProductoYaExisteException();
        }
    }

    // Elimina un producto del sistema utilizando su ID.
    public boolean eliminarProducto(String productoId) throws ProductoNoEncontradoException {
        if (utilPersistencia.buscarProductoPorId(productoId) != null) {
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

    // Modifica un producto existente en el sistema.
    public void modificarProducto(Producto productoModificado) {
        utilPersistencia.modificarProducto(productoModificado);
        utilSerializar.actualizarSerializacionProductos();
        utilPersistencia.gestionarArchivosPorEstado(utilPersistencia.leerProductosDesdeArchivo(),
                utilPersistencia.leerSolicitudesDesdeArchivo());
        // Actualiza la información del producto
        // y persiste los cambios en el archivo correspondiente
        utilLog.registrarAccion("Vendedor",
                " Se modifica el producto ", " Modificar.");
        // actualiza la serialización de los productos en el sistema.

    }

    // Obtiene una lista de solicitudes aceptadas asociadas a un vendedor
    // específico.
    public List<Solicitud> obtenerSolicitudesAceptadas(Vendedor vendedor) {
        return utilPersistencia.buscarSolicitudesAceptadasPorVendedor(vendedor);
    }

    // Obtiene una lista de solicitudes rechazadas asociadas a un vendedor
    // específico.
    public List<Solicitud> obtenerSolicitudesRechazadas(Vendedor vendedor) {
        return utilPersistencia.buscarSolicitudesRechazadasPorVendedor(vendedor);
    }

    // Obtiene una lista de solicitudes pendientes asociadas a un vendedor
    // específico.
    public List<Solicitud> obtenerSolicitudesPendientes(Vendedor vendedor) {
        return utilPersistencia.buscarSolicitudesPendientesPorVendedor(vendedor);
    }

    // Obtiene una lista de productos publicados asociados a un vendedor específico.
    public List<Producto> obtenerProductosPublicados(Vendedor vendedor) {
        utilPersistencia.gestionarArchivosPorEstado(utilPersistencia.leerProductosDesdeArchivo(),
                utilPersistencia.leerSolicitudesDesdeArchivo());
        List<Producto> productosVendedor = vendedor.getPublicaciones();
        List<Producto> productosPublicados = utilPersistencia.leerProductosPublicadosDesdeArchivo();

        List<Producto> productosPublicadosPorVendedor = new ArrayList<>();

        for (Producto productoPublicado : productosPublicados) {
            for (Producto productoVendedor : productosVendedor) {
                if (productoPublicado.getId().equals(productoVendedor.getId())) {
                    productosPublicadosPorVendedor.add(productoPublicado);
                    break;
                }
            }
        }

        if (productosPublicadosPorVendedor.isEmpty()) {
            utilLog.escribirLog("El vendedor " + vendedor.getId() + " no tiene productos publicados.", Level.WARNING);
        }

        return productosPublicadosPorVendedor;
    }

    public List<Producto> obtenerProductosVendidos(Vendedor vendedor) {
        utilPersistencia.gestionarArchivosPorEstado(utilPersistencia.leerProductosDesdeArchivo(), utilPersistencia.leerSolicitudesDesdeArchivo());
        List<Producto> productosVendedor = vendedor.getPublicaciones();
        List<Producto> productosVendidos = utilPersistencia.leerProductosVendidosDesdeArchivo();
    
        List<Producto> productosVendidosPorVendedor = new ArrayList<>();
    
        for (Producto productoVendido : productosVendidos) {
            for (Producto productoVendedor : productosVendedor) {
                if (productoVendido.getId().equals(productoVendedor.getId())) {
                    productosVendidosPorVendedor.add(productoVendido);
                    break;
                }
            }
        }
    
        if (productosVendidosPorVendedor.isEmpty()) {
            utilLog.escribirLog("El vendedor " + vendedor.getId() + " no tiene productos vendidos.", Level.WARNING);
        }
    
        return productosVendidosPorVendedor;
    }
    
    public List<Producto> obtenerProductosCancelados(Vendedor vendedor) {
        utilPersistencia.gestionarArchivosPorEstado(utilPersistencia.leerProductosDesdeArchivo(), utilPersistencia.leerSolicitudesDesdeArchivo());
        List<Producto> productosVendedor = vendedor.getPublicaciones();
        List<Producto> productosCancelados = utilPersistencia.leerProductosCanceladosDesdeArchivo();
    
        List<Producto> productosCanceladosPorVendedor = new ArrayList<>();
    
        for (Producto productoCancelado : productosCancelados) {
            for (Producto productoVendedor : productosVendedor) {
                if (productoCancelado.getId().equals(productoVendedor.getId())) {
                    productosCanceladosPorVendedor.add(productoCancelado);
                    break;
                }
            }
        }
    
        if (productosCanceladosPorVendedor.isEmpty()) {
            utilLog.escribirLog("El vendedor " + vendedor.getId() + " no tiene productos cancelados.", Level.WARNING);
        }
    
        return productosCanceladosPorVendedor;
    }
    

}
