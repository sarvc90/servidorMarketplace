package com.servidor.util;

import java.io.Serializable;
import java.util.List;

import com.servidor.excepciones.SolicitudExistenteException;
import com.servidor.excepciones.SolicitudNoExistenteException;
import com.servidor.excepciones.UsuarioExistenteException;
import com.servidor.excepciones.UsuarioNoEncontradoException;
import com.servidor.modelo.Admin;
import com.servidor.modelo.EstadoSolicitud;
import com.servidor.modelo.MarketPlace;
import com.servidor.modelo.Producto;
import com.servidor.modelo.Solicitud;
import com.servidor.modelo.Vendedor;

public class UtilMarketPlace implements Serializable{
    private static UtilMarketPlace instancia;
    private MarketPlace marketPlace;
    private UtilLog utilLog;
    private UtilPersistencia utilPersistencia;
    private UtilRespaldo utilRespaldo;
    private UtilSerializar utilSerializar;
    private UtilHilos utilHilos;

    private UtilMarketPlace() {
        this.utilLog = utilLog.getInstance();
        this.utilPersistencia = utilPersistencia.getInstance();
        this.utilRespaldo = utilRespaldo.getInstance();
        this.utilSerializar = utilSerializar.getInstance();
        this.utilHilos = utilHilos.getInstance();
    }
 
    public static UtilMarketPlace getInstance() {
        if (instancia == null) {
            instancia = new UtilMarketPlace();
        }
        return instancia;
    }

    public boolean crearVendedor(Vendedor vendedor) throws UsuarioExistenteException {

        if (utilPersistencia.buscarVendedorPorCedula(vendedor.getCedula()) == null) {
            utilPersistencia.guardarVendedorEnArchivo(vendedor);
            utilSerializar.actualizarSerializacionVendedores();
            marketPlace.getVendedores().add(vendedor);
            utilLog.registrarAccion("Vendedor nuevo ", "Registro exitoso.", "Registro.");
            return true;
        } else {
            // Excepcion de usuario existente
            utilLog.registrarAccion("Desconocido ", "Registro fallido. ", "Registro");
            throw new UsuarioExistenteException();
        }

    }
//Se pedira que ingrese la cedula para confirmar la accion
    public boolean eliminarVendedor(String cedulaVendedor) throws UsuarioNoEncontradoException{
        Vendedor vendedor = utilPersistencia.buscarVendedorPorCedula(cedulaVendedor);
        if (vendedor != null) {
            utilPersistencia.eliminarVendedor(cedulaVendedor);
            utilSerializar.actualizarSerializacionVendedores();
            marketPlace.getVendedores().remove(vendedor);
            utilLog.registrarAccion("Vendedor eliminado",
                    " El vendedor con cédula " + cedulaVendedor + " ha sido eliminado. ", " Eliminación.");
            return true;
        } else {
            // Excepcion de usuario no encontrado
            utilLog.registrarAccion("El vendedor no fue encontrado. ", " Eliminación fallida. ", " Eliminación.");
            throw new UsuarioNoEncontradoException();
        }

    }

    public void modificarVendedor(Vendedor vendedorModificado) {
        utilPersistencia.modificarVendedor(vendedorModificado);
        utilSerializar.actualizarSerializacionVendedores();
        int posicion = marketPlace.getVendedores().indexOf(vendedorModificado);
        marketPlace.getVendedores().set(posicion, vendedorModificado);
        utilLog.registrarAccion(("Vendedor " + vendedorModificado.getNombre() + " modificado. "),
                " Se modifica el vendedor ", " Modificar.");
    }


    public boolean crearSolicitud(Solicitud solicitud) throws SolicitudExistenteException{

        if ((utilPersistencia.buscarSolicitudPorEmisorYReceptor(solicitud.getEmisor().getId(), solicitud.getReceptor().getId()) == null)) {
            utilPersistencia.guardarSolicitudEnArchivo(solicitud);
            utilSerializar.actualizarSerializacionSolicitudes();
            marketPlace.getSolicitudes().add(solicitud);
            utilPersistencia.gestionarArchivosPorEstado(utilPersistencia.leerProductosDesdeArchivo(), utilPersistencia.leerTodasLasSolicitudes());
            utilLog.registrarAccion("Vendedor", "Nueva solicitud", "Buscador");
            return true;
        } else {
            // Excepcion de solicitud existente
            utilLog.registrarAccion("Vendedor ", "Solicitud ya existente. ", "Buscador");
            throw new SolicitudExistenteException();
        }

    }

    public boolean eliminarSolicitud(Vendedor emisor, Vendedor receptor) throws SolicitudNoExistenteException{
        Solicitud solicitud = utilPersistencia.buscarSolicitudPorEmisorYReceptor(emisor.getId(), receptor.getId());
        if (solicitud != null) {
            utilPersistencia.eliminarSolicitud(solicitud.getId());
            utilSerializar.actualizarSerializacionSolicitudes();
            marketPlace.getSolicitudes().remove(solicitud);
            utilLog.registrarAccion("Vendedor",
                    " La solicitud mandada por el usuario :" + solicitud.getEmisor().getNombre() + solicitud.getEmisor().getApellido() + ", al usuario: " + solicitud.getReceptor().getNombre() + solicitud.getReceptor().getApellido() + " ha sido eliminado. ",
                     " Muro propio del emisor.");
            return true;
        } else {
            // Excepcion de usuario no encontrado
            utilLog.registrarAccion("El vendedor no fue encontrado. ", " Eliminación fallida. ", " Eliminación.");
            throw new SolicitudNoExistenteException();
        }

    }

    public void cambiarEstadoSolicitud(Solicitud solicitud,EstadoSolicitud nuevoEstado) {
        utilPersistencia.cambiarEstadoSolicitud(solicitud.getId(), nuevoEstado);
        utilSerializar.actualizarSerializacionSolicitudes();
        int posicion = marketPlace.getSolicitudes().indexOf(solicitud);
        solicitud.setEstado(nuevoEstado);
        marketPlace.getSolicitudes().set(posicion, solicitud);
        utilPersistencia.gestionarArchivosPorEstado(utilPersistencia.leerProductosDesdeArchivo(), utilPersistencia.leerSolicitudesDesdeArchivo());
        utilLog.registrarAccion(("El estado de la solicitud " + solicitud.getId() + " cambió. "),
                " Se modifica el estado de la solicitud ", " Modificar.");
    }

    public List<Solicitud> obtenerSolicitudes(){
        return utilPersistencia.leerSolicitudesDesdeArchivo();
    }

    public List<Vendedor> obtenerVendedores(){
        return utilPersistencia.leerVendedoresDesdeArchivo();
    }

    public List<Producto> obtenerProductos(){
        return utilPersistencia.leerProductosDesdeArchivo();
    }

    public String iniciarSesion(String cedula, String contraseña) throws UsuarioNoEncontradoException{
        Vendedor vendedor = utilPersistencia.buscarVendedorPorCedula(cedula);
        if(vendedor != null){
            if(vendedor.getContraseña().equals(contraseña)){
                utilLog.registrarAccion("Vendedor. ", "Ingreso de sesión exitoso. ", "Iniciar sesión.");
                return vendedor.getId();
            } else{
                utilLog.registrarAccion("Desconocido. ", "Ingreso de sesión fallido. ", "Iniciar sesión. ");
                return null;
            }
        }else{
            Admin administrador = marketPlace.getAdministrador();
            if(administrador.getCedula().equals(cedula) && administrador.getContraseña().equals(contraseña)){
                utilLog.registrarAccion("Administrador. ", "Ingreso de sesión exitoso, bienvenido administrador ", "Iniciar sesión. ");
                return administrador.getId();
            }
            else{
                throw new UsuarioNoEncontradoException();
            }
        }
    }


    // Mirar logica de marketplace para ver como se va a obtener la lusta
    // si es un metodo de aqui mismo o como
}
