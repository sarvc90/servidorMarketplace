package com.servidor.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;


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
import com.servidor.modelo.Comentario;

public class UtilMarketPlace implements Serializable {
    private static UtilMarketPlace instancia;
    private MarketPlace marketPlace;
    private UtilLog utilLog;
    private UtilPersistencia utilPersistencia;
    private UtilRespaldo utilRespaldo;
    private UtilSerializar utilSerializar;

    private UtilMarketPlace() {
        this.utilLog = UtilLog.getInstance();
        this.utilPersistencia = UtilPersistencia.getInstance();
        this.utilRespaldo = UtilRespaldo.getInstance();
        this.utilSerializar = UtilSerializar.getInstance();
        utilPersistencia.gestionarArchivosPorEstado(utilPersistencia.leerProductosDesdeArchivo(),
                utilPersistencia.leerSolicitudesDesdeArchivo());

    }

    public void setMarketPlace(MarketPlace marketPlace) {
        this.marketPlace = marketPlace;
    }

    public MarketPlace getMarketPlace() {
        if (marketPlace == null) {
            marketPlace = new MarketPlace(this); // Pass this instance
        }
        return marketPlace;
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
    public void modificarVendedor(Vendedor vendedorModificado) throws UsuarioNoEncontradoException {
        utilPersistencia.modificarVendedor(vendedorModificado);
        utilSerializar.actualizarSerializacionVendedores();

        // Obtener la lista de vendedores
        List<Vendedor> vendedores = utilPersistencia.leerVendedoresDesdeArchivo();

        // Verificar si la lista de vendedores es nula
        if (vendedores == null) {
            utilLog.registrarAccion("La lista de vendedores es nula.",
                    "Error crítico.", "Modificar.");
            throw new UsuarioNoEncontradoException(); // O maneja como sea apropiado
        }

        // Buscar la posición del vendedor en la lista usando la cédula
        int posicion = -1;
        for (int i = 0; i < vendedores.size(); i++) {
            if (vendedores.get(i).getCedula().equals(vendedorModificado.getCedula())) {
                posicion = i;
                break;
            }
        }

        if (posicion != -1) {
            vendedores.set(posicion, vendedorModificado);
            utilLog.registrarAccion(("Vendedor " + vendedorModificado.getNombre() + " modificado."),
                    " Se modifica el vendedor ", " Modificar.");
        } else {
            // Manejar el caso donde el vendedor no fue encontrado
            utilLog.registrarAccion("Vendedor no encontrado para modificar: " + vendedorModificado.getNombre(),
                    "Modificación fallida.", "Modificar.");
            throw new UsuarioNoEncontradoException(); // O maneja como sea apropiado
        }
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
            marketPlace.obtenerSolicitudes().remove(solicitud);
            utilPersistencia.gestionarArchivosPorEstado(utilPersistencia.leerProductosDesdeArchivo(),
                    marketPlace.obtenerSolicitudes());

            utilLog.registrarAccion("Vendedor",
                    " La solicitud mandada por el usuario :" + solicitud.getEmisor().getNombre()
                            + solicitud.getEmisor().getApellido() + ", al usuario: "
                            + solicitud.getReceptor().getNombre() + solicitud.getReceptor().getApellido()
                            + " ha sido eliminado. ",
                    " Muro propio del emisor.");
            return true;
        } else {
            // Excepcion de usuario no encontrado

            utilLog.registrarAccion("La solicitud no fue encontrada. ", " Eliminación fallida. ", " Eliminación.");
            throw new SolicitudNoExistenteException();
        }

    }

    // metodo para actualizar el estado de la solicitud
    public void cambiarEstadoSolicitud(Solicitud solicitud, EstadoSolicitud nuevoEstado, Vendedor vendedor) {
        // Cambiar estado en la persistencia
        utilPersistencia.cambiarEstadoSolicitud(solicitud.getId(), nuevoEstado, vendedor);

        utilPersistencia.agregarSolicitudAVendedor(solicitud, vendedor);

        // Actualizar la serialización
        utilSerializar.actualizarSerializacionSolicitudes();

        // Cargar solicitudes desde el archivo
        List<Solicitud> solicitudes = utilPersistencia.leerSolicitudesDesdeArchivo();

        // Buscar la solicitud en la nueva lista
        int posicion = -1;
        for (int i = 0; i < solicitudes.size(); i++) {
            Solicitud s = solicitudes.get(i);
            if (s.getId().equals(solicitud.getId())) {
                posicion = i;
                break;
            }
        }

        if (posicion == -1) {
            utilLog.registrarAccion("No se encontró la solicitud con ID: " + solicitud.getId(),
                    "Error al modificar el estado", "Modificar");
            return;
        }

        // Cambiar el estado de la solicitud
        solicitudes.get(posicion).setEstado(nuevoEstado);

        // Reemplazar la solicitud en la lista original
        marketPlace.getSolicitudes().set(posicion, solicitudes.get(posicion));

        // Gestionar archivos
        utilPersistencia.gestionarArchivosPorEstado(utilPersistencia.leerProductosDesdeArchivo(),
                solicitudes);

        // Registrar la acción realizada
        utilLog.registrarAccion("Estado cambiado a " + nuevoEstado + " para la solicitud con ID: " + solicitud.getId(),
                "Modificación exitosa", "Modificar");
    }

    // obtiene la lista de solicitudes deserializada desde un archivo.
    public List<Solicitud> obtenerSolicitudes() {
        utilPersistencia.gestionarArchivos(utilPersistencia.leerVendedoresDesdeArchivo(),
                utilPersistencia.leerProductosDesdeArchivo(), utilSerializar.deserializarSolicitudes(true));
        return utilSerializar.deserializarSolicitudes(true);
    }

    // obtiene la lista de vendedores deserializada desde un archivo.
    public List<Vendedor> obtenerVendedores() {
        utilPersistencia.gestionarArchivos(utilSerializar.deserializarVendedores(true),
                utilPersistencia.leerProductosDesdeArchivo(), utilPersistencia.leerSolicitudesDesdeArchivo());
        return utilSerializar.deserializarVendedores(true);
    }

    // obtiene la lista de sproductos deserializada desde un archivo.
    public List<Producto> obtenerProductos() {
        utilPersistencia.gestionarArchivos(utilPersistencia.leerVendedoresDesdeArchivo(),
                utilSerializar.deserializarProductos(true), utilPersistencia.leerSolicitudesDesdeArchivo());
        return utilSerializar.deserializarProductos(true);
    }

    // Inicia sesión para un usuario (vendedor o administrador) basado en la cédula
    // y contraseña proporcionadas.
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
                // Excepcion de usuario no encontrado
                throw new UsuarioNoEncontradoException();
            }
        }
    }

    // Cuenta la cantidad de productos publicados dentro de un rango de fechas en
    // específico.
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

    // Cuenta la cantidad de productos publicados por un vendedor específico, se
    // busca por su cédula.
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

    // Cuenta la cantidad de contactos en la red de un vendedor específico, se busca
    // por su cedula.
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

    // Obtiene una lista de los 10 productos más populares basados en el número de
    // "me gusta".

    public List<Producto> obtenerTop10ProductosPopulares() {
        List<Producto> listaProductos = utilPersistencia.leerProductosDesdeArchivo();

        listaProductos.sort((p1, p2) -> Integer.compare(p2.getMeGustas(),
                p1.getMeGustas()));

        List<Producto> top10 = listaProductos.stream().limit(10).collect(Collectors.toList());

        utilLog.escribirLog("Top 10 productos más populares obtenidos.", Level.INFO);
        return top10;
    }

    // Exporta las estadísticas de un vendedor específico en un rango de fechas
    // determinado, guardándolas en una ruta especificada.
    public void exportarEstadisticas(String ruta, String nombreUsuario, String fechaInicio, String fechaFin,
            String idVendedor) {
        // Formatear la fecha actual
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String fechaActual = LocalDateTime.now().format(formatter);

        // Obtiene información para el reporte
        int cantidadProductosPublicados = contarProductosPorRangoFecha(fechaInicio, fechaFin);
        int cantidadProductosPorVendedor = contarProductosPorVendedor(idVendedor);
        int cantidadContactos = contarContactosPorVendedor(idVendedor);
        // List<Producto> top10Productos = obtenerTop10ProductosPopulares();

        // Crea el contenido del reporte
        StringBuilder reporte = new StringBuilder();
        reporte.append("<Título>Reporte de Listado de Clientes\n");
        reporte.append("<fecha>Fecha: ").append(fechaActual).append("\n");
        reporte.append("<Usuario>Reporte realizado por: ").append(nombreUsuario).append("\n\n");
        reporte.append("Información del reporte:\n");
        reporte.append("Cantidad de productos publicados entre ").append(fechaInicio.formatted(formatter)).append(" y ")
                .append(fechaFin.formatted(formatter)).append(": ").append(cantidadProductosPublicados).append("\n");
        reporte.append("Cantidad de productos publicados por el vendedor ID ").append(idVendedor).append(": ")
                .append(cantidadProductosPorVendedor).append("\n");
        reporte.append("Cantidad de contactos para el vendedor ID ").append(idVendedor).append(": ")
                .append(cantidadContactos).append("\n");
        reporte.append("Top 10 productos con más 'me gusta':\n");

        // for (Producto producto : top10Productos) {
        // reporte.append("- ").append(producto.getNombre()).append(" (ID:
        // ").append(producto.getId()).append(") con
        // ").append(producto.getMeGustas()).append(" me gusta(s)\n");
        // }

        reporte.append("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx\n");
        reporte.append("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx\n");

        // Escribe el reporte en el archivo
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ruta))) {
            writer.write(reporte.toString());
            utilLog.escribirLog("Reporte exportado exitosamente a: " + ruta, Level.INFO);
            utilLog.registrarAccion(nombreUsuario, "Exportó estadísticas. ", "Muro. ");
        } catch (IOException e) {
            utilLog.escribirLog("Error al exportar el reporte: " + e.getMessage(), Level.SEVERE);
        }
    }
    public Vendedor obtenerVendedorPorId(String id){
        return utilPersistencia.buscarVendedorPorId(id);
    }

    // Serializa el modelo de datos del Marketplace en formatos binario y XML,
    // permitiendo guardar
    // una copia de seguridad del estado actual del sistema.
    public void serializarModelo(MarketPlace marketPlace) {

        // Este método invoca el proceso de respaldo
        // definido en la utilidad de respaldo.
        // utilSerializar.guardarModeloSerializadoBin(marketPlace);
        utilSerializar.guardarModeloSerializadoXML(marketPlace);
        utilLog.registrarAccion("Administrador", "Copia de modelo", "administrador");
    }

    public void respaldoGeneral() {
        utilRespaldo.respaldoGeneral();
    }

    public List<Comentario> obtenerComentariosDeProducto(Producto producto) {
        if (producto == null) {
            throw new IllegalArgumentException("El producto no puede ser null");
        }

        try {
            return producto.getComentarios(); // Asumiendo que este método no lanza excepciones
        } catch (Exception e) {
            // Manejo de excepciones específico si es necesario
            // Puedes lanzar una excepción personalizada o registrar el error
            throw new RuntimeException("Error al obtener comentarios del producto", e);
        }
    }

    public Vendedor obtenerAutorDeProducto(String idProducto) {
        List<Vendedor> vendedores = obtenerVendedores(); // Obtener la lista de vendedores

        for (Vendedor vendedor : vendedores) {
            // Suponiendo que el vendedor tiene un método getProductos que devuelve una
            // lista de productos
            for (Producto producto : vendedor.getPublicaciones()) {
                if (producto.getId().equals(idProducto)) {
                    return vendedor; // Retorna el vendedor que tiene el producto
                }
            }
        }
        return null; // Si no se encuentra el vendedor, retorna null
    }
     // Método para contar la cantidad de productos publicados por un vendedor específico
    public int contarProductosPorVendedor(List<Producto> productos, Vendedor vendedorBuscado) {
        int contador = 0;

        // Iterar sobre la lista de productos y contar los que pertenecen al vendedor buscado
        for (Producto producto : productos) {
            Vendedor vendedor = obtenerAutorDeProducto(producto.getId());
            if (vendedor.getId().equals(vendedorBuscado.getId())) {
                contador++;
            }
        }

        return contador; // Devolver la cantidad de productos encontrados
    }

// Método para contar la cantidad de contactos de un vendedor específico desde un archivo
public int contarContactosDesdeArchivo(String nombreArchivo, String cedulaVendedor) {
    int contador = 0;

    try (BufferedReader br = new BufferedReader(new FileReader(nombreArchivo))) {
        String linea;
        while ((linea = br.readLine()) != null) {
            // Suponemos que cada línea tiene el formato "VendedorCedula, Contacto"
            String[] partes = linea.split(",\\s*"); // Dividir por coma y posible espacio

            if (partes.length >= 2) {
                String vendedorCedula = partes[0]; // Primer elemento es la cédula del vendedor
                // El segundo elemento es el contacto (no lo usamos aquí, pero puede ser útil)
                String contacto = partes[1];

                // Comparar la cédula del vendedor en la línea con la cédula buscada
                if (vendedorCedula.equalsIgnoreCase(cedulaVendedor)) {
                    contador++; // Incrementar contador si encontramos el vendedor
                }
            }
        }
    } catch (IOException e) {
        utilLog.escribirLog("Error al leer el archivo de contactos: " + e.getMessage(), Level.SEVERE);
    }

    return contador; // Devolver la cantidad de contactos encontrados
    }

}
