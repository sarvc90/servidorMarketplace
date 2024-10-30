package com.servidor.modelo;

import java.io.Serializable;
import java.util.List;

import com.servidor.excepciones.SolicitudExistenteException;
import com.servidor.excepciones.SolicitudNoExistenteException;
import com.servidor.excepciones.UsuarioExistenteException;
import com.servidor.excepciones.UsuarioNoEncontradoException;
import com.servidor.util.UtilMarketPlace;

public class MarketPlace implements Serializable {
    private List<Vendedor> vendedores;
    private List<Solicitud> solicitudes;
    private List<Producto> productos;
    private Admin administrador;
    private UtilMarketPlace utilMarketPlace;

    // Constructor
    public MarketPlace(UtilMarketPlace utilMarketPlace) {
        this.administrador = new Admin("1", "Juana", "Arias", "123", "direccion", "contraseña");
        this.utilMarketPlace = utilMarketPlace;
        this.vendedores = utilMarketPlace.obtenerVendedores();
        this.solicitudes = utilMarketPlace.obtenerSolicitudes();
        this.productos = utilMarketPlace.obtenerProductos();
    }

    public void setUtilMarketPlace(UtilMarketPlace utilMarketPlace) {
        this.utilMarketPlace = utilMarketPlace;
        this.vendedores = utilMarketPlace.obtenerVendedores();
        this.solicitudes = utilMarketPlace.obtenerSolicitudes();
        this.productos = utilMarketPlace.obtenerProductos();
    }

    public List<Vendedor> getVendedores() {
        return vendedores;
    }

    public void setVendedores(List<Vendedor> vendedores) {
        this.vendedores = vendedores;
    }

    public Admin getAdministrador() {
        return administrador;
    }

    public void setAdministrador(Admin administrador) {
        this.administrador = administrador;
    }

    public void setSolicitudes(List<Solicitud> solicitudes) {
        this.solicitudes = solicitudes;
    }

    public List<Solicitud> getSolicitudes() {
        return solicitudes;
    }

    public void setProductos(List<Producto> productos) {
        this.productos = productos;
    }

    public List<Producto> getProductos() {
        return productos;
    }

    public void crearVendedor(Vendedor vendedor) throws UsuarioExistenteException {
        boolean exito = utilMarketPlace.crearVendedor(vendedor);
        if (exito) {
            vendedores.add(vendedor);
        }
    }

    public void eliminarVendedor(String cedulaVendedor) throws UsuarioNoEncontradoException {
        boolean exito = utilMarketPlace.eliminarVendedor(cedulaVendedor);
        if (exito) {
            for (Vendedor vendedor : vendedores) {
                if (vendedor.getCedula().equals(cedulaVendedor)) {
                    vendedores.remove(vendedor);
                    break;
                }
            }
        }
    }

    public void modificarVendedor(Vendedor vendedorModificado) throws UsuarioNoEncontradoException {
        utilMarketPlace.modificarVendedor(vendedorModificado);
        for (Vendedor vendedor : vendedores) {
            if (vendedor.getCedula().equals(vendedorModificado.getCedula())) {
                int posicion = vendedores.indexOf(vendedor);
                vendedores.set(posicion, vendedorModificado);
                break;
            }
        }
    }

    public List<Solicitud> obtenerSolicitudes(){
        return utilMarketPlace.obtenerSolicitudes();
    }
    public List<Vendedor> obtenerVendedores(){
        return utilMarketPlace.obtenerVendedores();
    }
    public List<Producto> obtenerProductos(){
        return utilMarketPlace.obtenerProductos();
    }

    public void crearSolicitud(Solicitud solicitud) throws SolicitudExistenteException {
        boolean exito = utilMarketPlace.crearSolicitud(solicitud);
        if (exito) {
            solicitudes.add(solicitud);
        }
    }

    public void eliminarSolicitud(Vendedor emisor, Vendedor receptor) throws SolicitudNoExistenteException {
        boolean exito = utilMarketPlace.eliminarSolicitud(emisor, receptor);
        if (exito) {
            for (Solicitud solicitud : solicitudes) {
                if (solicitud.getEmisor().equals(emisor) && solicitud.getReceptor().equals(receptor)) {
                    solicitudes.remove(solicitud);
                    break;
                }
            }
        }
    }

    public void cambiarEstadoSolicitud(Solicitud solicitud1, EstadoSolicitud nuevoEstado, Vendedor vendedor) {
        utilMarketPlace.cambiarEstadoSolicitud(solicitud1, nuevoEstado, vendedor);
    }

    public void exportarEstadisticas(String ruta, String nombreUsuario, String fechaInicio, String fechaFin,
            String idVendedor) {
        utilMarketPlace.exportarEstadisticas(ruta, nombreUsuario, fechaInicio, fechaFin, idVendedor);
    }

    // Chat?
}
