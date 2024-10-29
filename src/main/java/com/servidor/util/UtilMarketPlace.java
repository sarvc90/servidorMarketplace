package com.servidor.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Level;


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

public class UtilMarketPlace implements Serializable {
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

    // se crea la unica instancia de la clase
    public static UtilMarketPlace getInstance() {
        if (instancia == null) {
            instancia = new UtilMarketPlace();
        }
        return instancia;
    }

    // metodo que crea y registra vendedores nuevos
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

    // Se pedira que ingrese la cedula para confirmar la accion
    public boolean eliminarVendedor(String cedulaVendedor) throws UsuarioNoEncontradoException {
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

    // metodo para actualizar la informacion de un vendedor existente
    public void modificarVendedor(Vendedor vendedorModificado) {
        utilPersistencia.modificarVendedor(vendedorModificado);
        utilSerializar.actualizarSerializacionVendedores();
        int posicion = marketPlace.getVendedores().indexOf(vendedorModificado);
        marketPlace.getVendedores().set(posicion, vendedorModificado);
        utilLog.registrarAccion(("Vendedor " + vendedorModificado.getNombre() + " modificado. "),
                " Se modifica el vendedor ", " Modificar.");
    }

    // metodo que crea y registra una nueva solicitud
    public boolean crearSolicitud(Solicitud solicitud) throws SolicitudExistenteException {

        if ((utilPersistencia.buscarSolicitudPorEmisorYReceptor(solicitud.getEmisor().getId(),
                solicitud.getReceptor().getId()) == null)) {
            utilPersistencia.guardarSolicitudEnArchivo(solicitud);
            utilSerializar.actualizarSerializacionSolicitudes();
            marketPlace.getSolicitudes().add(solicitud);
            utilPersistencia.gestionarArchivosPorEstado(utilPersistencia.leerProductosDesdeArchivo(),
                    utilPersistencia.leerTodasLasSolicitudes());
            utilLog.registrarAccion("Vendedor", "Nueva solicitud", "Buscador");
            return true;
        } else {
            // Excepcion de solicitud existente
            utilLog.registrarAccion("Vendedor ", "Solicitud ya existente. ", "Buscador");
            throw new SolicitudExistenteException();
        }

    }
// metodo para eliminar una solicitud exsitente entre dos vendedores 
    public boolean eliminarSolicitud(Vendedor emisor, Vendedor receptor) throws SolicitudNoExistenteException {
        Solicitud solicitud = utilPersistencia.buscarSolicitudPorEmisorYReceptor(emisor.getId(), receptor.getId());
        if (solicitud != null) {
            utilPersistencia.eliminarSolicitud(solicitud.getId());
            utilSerializar.actualizarSerializacionSolicitudes();
            marketPlace.getSolicitudes().remove(solicitud);
            utilLog.registrarAccion("Vendedor",
                    " La solicitud mandada por el usuario :" + solicitud.getEmisor().getNombre()
                            + solicitud.getEmisor().getApellido() + ", al usuario: "
                            + solicitud.getReceptor().getNombre() + solicitud.getReceptor().getApellido()
                            + " ha sido eliminado. ",
                    " Muro propio del emisor.");
            return true;
        } else {
            // Excepcion de usuario no encontrado
            utilLog.registrarAccion("El vendedor no fue encontrado. ", " Eliminación fallida. ", " Eliminación.");
            throw new SolicitudNoExistenteException();
        }

    }
//metodo para actualizar el estado de la solicitud 
    public void cambiarEstadoSolicitud(Solicitud solicitud, EstadoSolicitud nuevoEstado, Vendedor vendedor) {
        utilPersistencia.cambiarEstadoSolicitud(solicitud.getId(), nuevoEstado, vendedor);
        utilSerializar.actualizarSerializacionSolicitudes();
        int posicion = marketPlace.getSolicitudes().indexOf(solicitud);
        solicitud.setEstado(nuevoEstado);
        marketPlace.getSolicitudes().set(posicion, solicitud);
        utilPersistencia.gestionarArchivosPorEstado(utilPersistencia.leerProductosDesdeArchivo(),
                utilPersistencia.leerSolicitudesDesdeArchivo());
        utilLog.registrarAccion(("El estado de la solicitud " + solicitud.getId() + " cambió. "),
                " Se modifica el estado de la solicitud ", " Modificar.");
    }

    public List<Solicitud> obtenerSolicitudes() {
        return utilPersistencia.leerSolicitudesDesdeArchivo();
    }

    public List<Vendedor> obtenerVendedores() {
        return utilPersistencia.leerVendedoresDesdeArchivo();
    }

    public List<Producto> obtenerProductos() {
        return utilPersistencia.leerProductosDesdeArchivo();
    }

    public String iniciarSesion(String cedula, String contraseña) throws UsuarioNoEncontradoException {
        Vendedor vendedor = utilPersistencia.buscarVendedorPorCedula(cedula);
        if (vendedor != null) {
            if (vendedor.getContraseña().equals(contraseña)) {
                utilLog.registrarAccion("Vendedor. ", "Ingreso de sesión exitoso. ", "Iniciar sesión.");
                return vendedor.getId();
            } else {
                utilLog.registrarAccion("Desconocido. ", "Ingreso de sesión fallido. ", "Iniciar sesión. ");
                return null;
            }
        } else {
            Admin administrador = marketPlace.getAdministrador();
            if (administrador.getCedula().equals(cedula) && administrador.getContraseña().equals(contraseña)) {
                utilLog.registrarAccion("Administrador. ", "Ingreso de sesión exitoso, bienvenido administrador ",
                        "Iniciar sesión. ");
                return administrador.getId();
            } else {
                throw new UsuarioNoEncontradoException();
            }
        }
    }

    public int contarProductosPorRangoFecha(String fechaInicio, String fechaFin) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        LocalDateTime inicio = LocalDateTime.parse(fechaInicio, formatter);
        LocalDateTime fin = LocalDateTime.parse(fechaFin, formatter);

        List<Producto> listaProductos = utilPersistencia.leerProductosDesdeArchivo();
        int contador = 0;

        for (Producto producto : listaProductos) {
            LocalDateTime fechaPublicacion = producto.getFechaPublicacion();
            if ((fechaPublicacion.isEqual(inicio) || fechaPublicacion.isAfter(inicio)) &&
                    (fechaPublicacion.isEqual(fin) || fechaPublicacion.isBefore(fin))) {
                contador++;
            }
        }

        utilLog.escribirLog(
                "Cantidad de productos publicados entre " + fechaInicio + " y " + fechaFin + ": " + contador,
                Level.INFO);
        return contador;
    }

    public int contarProductosPorVendedor(String cedulaVendedor) {
        List<Vendedor> listaVendedores = utilPersistencia.leerVendedoresDesdeArchivo();

        for (Vendedor vendedor : listaVendedores) {
            if (vendedor.getCedula().equals(cedulaVendedor)) {
                int cantidadProductos = vendedor.getPublicaciones().size();
                utilLog.escribirLog(
                        "Cantidad de productos publicados por el vendedor ID " + cedulaVendedor + ": "
                                + cantidadProductos,
                        Level.INFO);
                return cantidadProductos;
            }
        }

        utilLog.escribirLog("Vendedor no encontrado con ID: " + cedulaVendedor, Level.WARNING);
        return 0;
    }

    public int contarContactosPorVendedor(String cedulaVendedor) {
        Vendedor vendedor = utilPersistencia.buscarVendedorPorCedula(cedulaVendedor);

        if (vendedor != null) {
            int cantidadContactos = vendedor.getRedDeContactos().size();
            utilLog.escribirLog(
                    "Cantidad de contactos para el vendedor ID " + cedulaVendedor + ": " + cantidadContactos,
                    Level.INFO);
            return cantidadContactos;
        } else {
            utilLog.escribirLog("Vendedor no encontrado con ID: " + cedulaVendedor, Level.WARNING);
            return 0;
        }
    }

    /* 
    public List<Producto> obtenerTop10ProductosPopulares() {
        List<Producto> listaProductos = utilPersistencia.leerProductosDesdeArchivo();

        listaProductos.sort((p1, p2) -> Integer.compare(p2.getMeGustas(), p1.getMeGustas()));

        List<Producto> top10 = listaProductos.stream().limit(10).collect(Collectors.toList());

        utilLog.escribirLog("Top 10 productos más populares obtenidos.", Level.INFO);
        return top10;
    }
    */

        public void exportarEstadisticas(String ruta, String nombreUsuario, String fechaInicio, String fechaFin, String idVendedor) {
        // Formatear la fecha actual
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String fechaActual = LocalDateTime.now().format(formatter);
    
        // Obtener información para el reporte
        int cantidadProductosPublicados = contarProductosPorRangoFecha(fechaInicio, fechaFin);
        int cantidadProductosPorVendedor = contarProductosPorVendedor(idVendedor);
        int cantidadContactos = contarContactosPorVendedor(idVendedor);
        //List<Producto> top10Productos = obtenerTop10ProductosPopulares();
    
        // Crear el contenido del reporte
        StringBuilder reporte = new StringBuilder();
        reporte.append("<Título>Reporte de Listado de Clientes\n");
        reporte.append("<fecha>Fecha: ").append(fechaActual).append("\n");
        reporte.append("<Usuario>Reporte realizado por: ").append(nombreUsuario).append("\n\n");
        reporte.append("Información del reporte:\n");
        reporte.append("Cantidad de productos publicados entre ").append(fechaInicio.formatted(formatter)).append(" y ").append(fechaFin.formatted(formatter)).append(": ").append(cantidadProductosPublicados).append("\n");
        reporte.append("Cantidad de productos publicados por el vendedor ID ").append(idVendedor).append(": ").append(cantidadProductosPorVendedor).append("\n");
        reporte.append("Cantidad de contactos para el vendedor ID ").append(idVendedor).append(": ").append(cantidadContactos).append("\n");
        reporte.append("Top 10 productos con más 'me gusta':\n");
    
        //for (Producto producto : top10Productos) {
        //    reporte.append("- ").append(producto.getNombre()).append(" (ID: ").append(producto.getId()).append(") con ").append(producto.getMeGustas()).append(" me gusta(s)\n");
        //}
    
        reporte.append("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx\n");
        reporte.append("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx\n");
    
        // Escribir el reporte en el archivo
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ruta))) {
            writer.write(reporte.toString());
            utilLog.escribirLog("Reporte exportado exitosamente a: " + ruta, Level.INFO);
            utilLog.registrarAccion(nombreUsuario, "Exportó estadísticas. ", "Muro. ");
        } catch (IOException e) {
            utilLog.escribirLog("Error al exportar el reporte: " + e.getMessage(), Level.SEVERE);
        }
    }

    public void serializarModelo(MarketPlace marketPlace){
        utilSerializar.guardarModeloSerializadoBin(marketPlace);
        utilSerializar.guardarModeloSerializadoXML(marketPlace);
        utilLog.registrarAccion("Administrador", "Copia de modelo", "administrador");
    }

}
