package com.servidor.modelo;

import java.util.ArrayList;
import java.util.List;

import com.servidor.excepciones.ProductoNoEncontradoException;
import com.servidor.excepciones.ProductoYaExisteException;
import com.servidor.util.UtilVendedor;

public class Vendedor extends Persona {
    private List<Producto> publicaciones;
    private List<Vendedor> redDeContactos;
    private UtilVendedor utilVendedor;

    public Vendedor() {
        super();
    }

    public Vendedor(String id, String nombre, String apellido, String cedula, String direccion, String contraseña,
            List<Producto> publicaciones, List<Vendedor> redDeContactos) {
        super(id, nombre, apellido, cedula, direccion, contraseña);
        this.publicaciones = publicaciones;
        this.redDeContactos = new ArrayList<>();
        this.utilVendedor = utilVendedor.getInstance();
        inicializarRedDeContactos(); 
    }

    public List<Producto> getPublicaciones() {
        return publicaciones;
    }

    public void setPublicaciones(List<Producto> publicaciones) {
        this.publicaciones = publicaciones;
    }

    public List<Vendedor> getRedDeContactos() {
        return redDeContactos;
    }

    public void setRedDeContactos(List<Vendedor> redDeContactos) {
        this.redDeContactos = redDeContactos;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Productos Propios (Ordenados por Fecha de Publicación):\n");
        List<Producto> productosPropiosOrdenados = getPublicaciones();
        for (Producto producto : productosPropiosOrdenados) {
            sb.append(producto.toString()).append("\n"); // Asumiendo que Producto tiene un método toString
        }
        return sb.toString();
    }

    public void crearProducto(Producto producto) throws ProductoYaExisteException {
        boolean exito = utilVendedor.crearProducto(producto);
        if (exito) {
            publicaciones.add(producto);
        }
    }

    public void eliminarProducto(String productoId) throws ProductoNoEncontradoException {
        boolean exito = utilVendedor.eliminarProducto(productoId);
        if (exito) {
            // Buscar el producto en la lista de publicaciones y removerlo
            for (Producto producto : publicaciones) {
                if (producto.getId().equals(productoId)) {
                    publicaciones.remove(producto);
                    break;
                }
            }
        }
    }

    public void modificarProducto(Producto productoModificado) {

        // Buscar el producto en la lista de publicaciones y removerlo
        for (Producto producto : publicaciones) {
            if (producto.getId().equals(productoModificado.getId())) {
                int posicion = publicaciones.indexOf(producto);
                publicaciones.set(posicion, productoModificado);
                break;
            }
        }
    }

    private void inicializarRedDeContactos() {
        List<Solicitud> solicitudesAceptadas = utilVendedor.obtenerSolicitudesAceptadas(this);
        for (Solicitud solicitud : solicitudesAceptadas) {
            Vendedor receptor = solicitud.getReceptor();
            if (!redDeContactos.contains(receptor)) {
                redDeContactos.add(receptor);
            }
        }
    }

    public List<Solicitud> obtenerSolicitudesPendientes(){
        return utilVendedor.obtenerSolicitudesPendientes(this);
    }

    public List<Solicitud> obtenerSolicitudesRechazadas(){
        return utilVendedor.obtenerSolicitudesRechazadas(this);
    }
}

