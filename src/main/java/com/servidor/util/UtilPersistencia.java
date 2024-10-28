package com.servidor.util;

import java.util.logging.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.servidor.modelo.Categoria;
import com.servidor.modelo.Estado;
import com.servidor.modelo.EstadoSolicitud;
import com.servidor.modelo.Producto;
import com.servidor.modelo.Solicitud;
import com.servidor.modelo.Vendedor;

public class UtilPersistencia {
    private static UtilPersistencia instancia;
    private UtilProperties utilProperties;
    private UtilLog utilLog;

    private UtilPersistencia() {
        this.utilProperties = utilProperties.getInstance();
        this.utilLog = utilLog.getInstance();
    }

    public void gestionarArchivos(List<Vendedor> listaVendedores, List<Producto> listaProductos,
            List<Solicitud> listaSolicitudes) {
        String rutaVendedores = utilProperties.obtenerPropiedad("rutaVendedores.txt");
        String rutaProductos = utilProperties.obtenerPropiedad("rutaProductos.txt");
        String rutaSolicitudes = utilProperties.obtenerPropiedad("rutaSolicitudes.txt");
        escribirListaEnArchivo(rutaVendedores, listaVendedores);
        escribirListaEnArchivo(rutaProductos, listaProductos);
        escribirListaEnArchivo(rutaSolicitudes, listaSolicitudes);
        utilLog.escribirLog("Archivos gestionados correctamente", Level.INFO);
    }

    public UtilPersistencia getInstance() {
        if (instancia == null) {
            instancia = new UtilPersistencia();
        }
        return instancia;
    }

    // CREAR

    private void escribirListaEnArchivo(String ruta, List<?> lista) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ruta))) {
            for (Object objeto : lista) {
                if (objeto instanceof Vendedor) {
                    Vendedor vendedor = (Vendedor) objeto;
                    guardarVendedorEnArchivo(vendedor);
                } else if (objeto instanceof Producto) {
                    Producto producto = (Producto) objeto;
                    guardarProductoEnArchivo(producto);
                } else if (objeto instanceof Solicitud) {
                    Solicitud solicitud = (Solicitud) objeto;
                    guardarSolicitudEnArchivo(solicitud);
                }
            }
            utilLog.logInfo("Lista escrita en archivo correctamente");
        } catch (IOException e) {
            utilLog.logSevere("Error al escribir en el archivo: " + ruta);
        }
    }

    public void guardarSolicitudEnArchivo(Solicitud solicitud) {
        String rutaSolicitudes = utilProperties.obtenerPropiedad("rutaSolicitudes.txt");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(rutaSolicitudes, true))) {
            String emisorId = solicitud.getEmisor() != null ? solicitud.getEmisor().getId() : "";
            String receptorId = solicitud.getReceptor() != null ? solicitud.getReceptor().getId() : "";
            writer.write(solicitud.getId() + "%" + emisorId + "%" + receptorId + "%" + solicitud.getEstado());
            writer.newLine();
            utilLog.escribirLog("Solicitud guardada exitosamente: " + solicitud, Level.INFO);
        } catch (IOException e) {
            utilLog.escribirLog("Error al guardar la solicitud: " + solicitud, Level.SEVERE);
        }
    }

    public void guardarVendedorEnArchivo(Vendedor vendedor) {
        String rutaVendedores = utilProperties.obtenerPropiedad("rutaVendedores.txt");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(rutaVendedores, true))) {
            String publicacionesStr = vendedor.getPublicaciones().stream()
                    .map(Producto::getId)
                    .reduce((p1, p2) -> p1 + "," + p2).orElse("");

            String contactosStr = vendedor.getRedDeContactos().stream()
                    .map(Vendedor::getId)
                    .reduce((c1, c2) -> c1 + "," + c2).orElse("");

            writer.write(vendedor.getId() + "%" + vendedor.getNombre() + "%" + vendedor.getApellido() + "%"
                    + vendedor.getCedula() + "%" + vendedor.getDireccion() + "%" + vendedor.getContraseña() + "%"
                    + publicacionesStr + "%" + contactosStr + "%");
            writer.newLine();
            utilLog.escribirLog("Vendedor guardado exitosamente: " + vendedor, Level.INFO);
        } catch (IOException e) {
            utilLog.escribirLog("Error al guardar el vendedor: " + vendedor, Level.SEVERE);
        }
    }

    public void guardarProductoEnArchivo(Producto producto) {
        String rutaProductos = utilProperties.obtenerPropiedad("rutaProductos.txt");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(rutaProductos))) {
            writer.write(producto.getId() + "%" + producto.getNombre() + "%" + producto.getDescripcion() + "%"
                    + producto.getFechaPublicacion() + "%" + producto.getImagenRuta() + "%" + producto.getPrecio()
                    + producto.getMeGustas() + "%" + producto.getComentarios() + "%" + producto.getEstado() + "%"
                    + producto.getCategoria());
            writer.newLine();
            utilLog.escribirLog("Producto guardado exitosamente: " + producto, Level.INFO);
        } catch (IOException e) {
            utilLog.escribirLog("Error al guardar el vendedor: " + producto, Level.SEVERE);
        }
    }

    public void gestionarArchivosPorEstado(List<Producto> listaProductos, List<Solicitud> listaSolicitudes) {
        // Obtener rutas desde utilProperties
        String rutaProductosVendidos = utilProperties.obtenerPropiedad("rutaProductosVendidos.txt");
        String rutaProductosPublicados = utilProperties.obtenerPropiedad("rutaProductosPublicados.txt");
        String rutaProductosCancelados = utilProperties.obtenerPropiedad("rutaProductosCancelados.txt");

        String rutaSolicitudesPendientes = utilProperties.obtenerPropiedad("rutaSolicitudesPendientes.txt");
        String rutaSolicitudesAceptadas = utilProperties.obtenerPropiedad("rutaSolicitudesAceptadas.txt");
        String rutaSolicitudesRechazadas = utilProperties.obtenerPropiedad("rutaSolicitudesRechazadas.txt");

        // Escribir productos en archivos según su estado
        for (Producto producto : listaProductos) {
            String estado = producto.getEstado().toString(); // Suponiendo que el estado es un enum
            switch (estado) {
                case "VENDIDO":
                    escribirIdEnArchivo(rutaProductosVendidos, producto.getId());
                    break;
                case "PUBLICADO":
                    escribirIdEnArchivo(rutaProductosPublicados, producto.getId());
                    break;
                case "CANCELADO":
                    escribirIdEnArchivo(rutaProductosCancelados, producto.getId());
                    break;
            }
        }

        // Escribir solicitudes en archivos según su estado
        for (Solicitud solicitud : listaSolicitudes) {
            String estado = solicitud.getEstado().toString(); // Suponiendo que el estado es un enum
            switch (estado) {
                case "PENDIENTE":
                    escribirIdEnArchivo(rutaSolicitudesPendientes, solicitud.getId());
                    break;
                case "ACEPTADA":
                    escribirIdEnArchivo(rutaSolicitudesAceptadas, solicitud.getId());
                    break;
                case "RECHAZADA":
                    escribirIdEnArchivo(rutaSolicitudesRechazadas, solicitud.getId());
                    break;
            }
        }

        utilLog.escribirLog("Archivos gestionados por estado correctamente", Level.INFO);
    }

    private void escribirIdEnArchivo(String ruta, String id) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ruta, true))) { // true para append
            writer.write(id);
            writer.newLine();
        } catch (IOException e) {
            utilLog.logSevere("Error al escribir en el archivo: " + ruta);
        }
    }

    // LEER

    public List<Vendedor> leerVendedoresDesdeArchivo() {
        List<Vendedor> listaVendedores = new ArrayList<>();
        String rutaVendedores = utilProperties.obtenerPropiedad("rutaVendedores.txt");

        try (BufferedReader reader = new BufferedReader(new FileReader(rutaVendedores))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] datos = linea.split("%");
                List<Producto> publicaciones = new ArrayList<>();
                if (datos.length > 6) {
                    String[] idsPublicaciones = datos[6].split(",");
                    for (String id : idsPublicaciones) {
                        Producto producto = buscarProductoPorId(id);
                        publicaciones.add(producto);
                    }
                }

                List<Vendedor> contactos = new ArrayList<>();
                if (datos.length > 7) {
                    String[] idsContactos = datos[7].split(",");
                    for (String cedula : idsContactos) {
                        Vendedor contacto = buscarVendedorPorCedula(cedula);
                        contactos.add(contacto);
                    }
                }

                Vendedor vendedor = new Vendedor(
                        datos[0], // ID
                        datos[1], // Nombre
                        datos[2], // Apellido
                        datos[3], // Cedula
                        datos[4], // Direccion
                        datos[5], // Contraseña
                        publicaciones, // Publicaciones
                        contactos // Red de contactos
                );
                listaVendedores.add(vendedor);
            }
            utilLog.escribirLog("Vendedores leídos desde el archivo correctamente.", Level.INFO);
        } catch (IOException e) {
            utilLog.escribirLog("Error al leer vendedores desde el archivo: " + rutaVendedores, Level.SEVERE);
        }
        return listaVendedores;
    }

    // Método para leer productos desde archivo
    public List<Producto> leerProductosDesdeArchivo() {
        List<Producto> listaProductos = new ArrayList<>();
        String rutaProductos = utilProperties.obtenerPropiedad("rutaProductos.txt");

        try (BufferedReader reader = new BufferedReader(new FileReader(rutaProductos))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] datos = linea.split("%");
                Producto producto = new Producto(
                        datos[0], // ID
                        datos[1], // Nombre
                        datos[2], // Descripcion
                        datos[3], // Fecha Publicacion (string)
                        datos[4], // Imagen Ruta
                        Integer.parseInt(datos[5]), // Precio
                        Integer.parseInt(datos[6]), // Me Gustas
                        Estado.valueOf(datos[7]), // Estado (convertido a Enum)
                        Categoria.valueOf(datos[8]) // Categoria (convertido a Enum)
                );
                listaProductos.add(producto);
            }
            utilLog.escribirLog("Productos leídos desde el archivo correctamente.", Level.INFO);
        } catch (IOException e) {
            utilLog.escribirLog("Error al leer productos desde el archivo: " + rutaProductos, Level.SEVERE);
        }
        return listaProductos;
    }

    // Método para leer solicitudes desde archivo
    public List<Solicitud> leerSolicitudesDesdeArchivo() {
        List<Solicitud> listaSolicitudes = new ArrayList<>();
        String rutaSolicitudes = utilProperties.obtenerPropiedad("rutaSolicitudes.txt");

        try (BufferedReader reader = new BufferedReader(new FileReader(rutaSolicitudes))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] datos = linea.split("%");
                // Suponiendo que el archivo tiene el formato: id%emisorId%receptorId%estado
                String id = datos[0];
                String emisorCedula = datos[1];
                String receptorCedula = datos[2];
                EstadoSolicitud estado = EstadoSolicitud.valueOf(datos[3]); // Convertir a Enum

                // Buscar los Vendedores por ID
                Vendedor emisor = buscarVendedorPorCedula(emisorCedula);
                Vendedor receptor = buscarVendedorPorCedula(receptorCedula);

                // Crear la solicitud
                Solicitud solicitud = new Solicitud(id, emisor, receptor, estado);
                listaSolicitudes.add(solicitud);
            }
            utilLog.escribirLog("Solicitudes leídas desde el archivo correctamente.", Level.INFO);
        } catch (IOException e) {
            utilLog.escribirLog("Error al leer solicitudes desde el archivo: " + rutaSolicitudes, Level.SEVERE);
        }
        return listaSolicitudes;
    }

    // Método para leer todas las solicitudes
    public List<Solicitud> leerTodasLasSolicitudes() {
        return leerSolicitudesDesdeArchivo();
    }

    // MODIFICAR

    // Método para cambiar el estado de una solicitud
    public void cambiarEstadoSolicitud(String idSolicitud, EstadoSolicitud nuevoEstado) {
        List<Solicitud> listaSolicitudes = leerSolicitudesDesdeArchivo();
        for (Solicitud solicitud : listaSolicitudes) {
            if (solicitud.getId().equals(idSolicitud)) {
                solicitud.setEstado(nuevoEstado);
                utilLog.escribirLog("Estado de la solicitud cambiado exitosamente: " + solicitud, Level.INFO);
                break;
            }
        }
        gestionarArchivos(leerVendedoresDesdeArchivo(), leerProductosDesdeArchivo(), listaSolicitudes);
    }

    // Método para modificar un vendedor
    public void modificarVendedor(Vendedor vendedorModificado) {
        List<Vendedor> listaVendedores = leerVendedoresDesdeArchivo();
        for (int i = 0; i < listaVendedores.size(); i++) {
            if (listaVendedores.get(i).getId().equals(vendedorModificado.getId())) {
                listaVendedores.set(i, vendedorModificado);
                break;
            }
        }
        gestionarArchivos(listaVendedores, leerProductosDesdeArchivo(), leerSolicitudesDesdeArchivo());
        utilLog.escribirLog("Vendedor modificado exitosamente: " + vendedorModificado, Level.INFO);
    }

    // Método para modificar un producto
    public void modificarProducto(Producto productoModificado) {
        List<Producto> listaProductos = leerProductosDesdeArchivo();
        for (int i = 0; i < listaProductos.size(); i++) {
            if (listaProductos.get(i).getId().equals(productoModificado.getId())) {
                listaProductos.set(i, productoModificado);
                break;
            }
        }
        gestionarArchivos(leerVendedoresDesdeArchivo(), listaProductos, leerSolicitudesDesdeArchivo());
        utilLog.escribirLog("Producto modificado exitosamente: " + productoModificado, Level.INFO);
    }

    // ELIMINAR

    // Método para eliminar solicitud
    public void eliminarSolicitud(String idSolicitud) {
        List<Solicitud> listaSolicitudes = leerSolicitudesDesdeArchivo();
        listaSolicitudes.removeIf(solicitud -> solicitud.getId().equals(idSolicitud));
        gestionarArchivos(leerVendedoresDesdeArchivo(), leerProductosDesdeArchivo(), listaSolicitudes);
        utilLog.escribirLog("Solicitud eliminada exitosamente con ID: " + idSolicitud, Level.INFO);
    }

    // Método para eliminar un vendedor
    public void eliminarVendedor(String cedulaVendedor) {
        List<Vendedor> listaVendedores = leerVendedoresDesdeArchivo();
        listaVendedores.removeIf(vendedor -> vendedor.getCedula().equals(cedulaVendedor));
        gestionarArchivos(listaVendedores, leerProductosDesdeArchivo(), leerSolicitudesDesdeArchivo());
        utilLog.escribirLog("Vendedor eliminado exitosamente con cedula: " + cedulaVendedor, Level.INFO);
    }

    // Método para eliminar un producto
    public void eliminarProducto(String idProducto) {
        List<Producto> listaProductos = leerProductosDesdeArchivo();
        listaProductos.removeIf(producto -> producto.getId().equals(idProducto));
        gestionarArchivos(leerVendedoresDesdeArchivo(), listaProductos, leerSolicitudesDesdeArchivo());
        utilLog.escribirLog("Producto eliminado exitosamente con ID: " + idProducto, Level.INFO);
    }

    // BUSCAR

    // Método para buscar un vendedor por cedula
    public Vendedor buscarVendedorPorCedula(String cedula) {
        List<Vendedor> listaVendedores = leerVendedoresDesdeArchivo();
        for (Vendedor vendedor : listaVendedores) {
            if (vendedor.getCedula().equals(cedula)) {
                return vendedor; // Retorna el vendedor encontrado
            }
        }
        return null; // Retorna null si no se encuentra el vendedor
    }

    // Método para buscar un vendedor por nombre
    public Vendedor buscarVendedorPorNombre(String nombre) {
        List<Vendedor> listaVendedores = leerVendedoresDesdeArchivo();
        for (Vendedor vendedor : listaVendedores) {
            if (vendedor.getNombre().equals(nombre)) {
                return vendedor; // Retorna el vendedor encontrado
            }
        }
        return null; // Retorna null si no se encuentra el vendedor
    }

    // Método para buscar un producto por ID
    public Producto buscarProductoPorId(String id) {
        List<Producto> listaProductos = leerProductosDesdeArchivo();
        for (Producto producto : listaProductos) {
            if (producto.getId().equals(id)) {
                return producto; // Retorna el producto encontrado
            }
        }
        return null; // Retorna null si no se encuentra el producto
    }

    // Método para buscar un producto por nombre
    public Producto buscarProductoPorNombre(String nombre) {
        List<Producto> listaProductos = leerProductosDesdeArchivo();
        for (Producto producto : listaProductos) {
            if (producto.getNombre().equals(nombre)) {
                return producto; // Retorna el producto encontrado
            }
        }
        return null; // Retorna null si no se encuentra el producto
    }

    public List<Solicitud> buscarSolicitudPorEmisor(String emisorId) {
        List<Solicitud> solicitudesEncontradas = new ArrayList<>();
        List<Solicitud> listaSolicitudes = leerSolicitudesDesdeArchivo();

        for (Solicitud solicitud : listaSolicitudes) {
            if (solicitud.getEmisor() != null && solicitud.getEmisor().getId().equals(emisorId)) {
                solicitudesEncontradas.add(solicitud);
            }
        }

        utilLog.escribirLog("Solicitudes encontradas para el emisor ID: " + emisorId, Level.INFO);
        return solicitudesEncontradas;
    }

    public List<Solicitud> buscarSolicitudPorReceptor(String receptorId) {
        List<Solicitud> solicitudesEncontradas = new ArrayList<>();
        List<Solicitud> listaSolicitudes = leerSolicitudesDesdeArchivo();

        for (Solicitud solicitud : listaSolicitudes) {
            if (solicitud.getReceptor() != null && solicitud.getReceptor().getId().equals(receptorId)) {
                solicitudesEncontradas.add(solicitud);
            }
        }

        utilLog.escribirLog("Solicitudes encontradas para el receptor ID: " + receptorId, Level.INFO);
        return solicitudesEncontradas;
    }

    public Solicitud buscarSolicitudPorEmisorYReceptor(String emisorId, String receptorId){
        List<Solicitud> listaSolicitudes = leerSolicitudesDesdeArchivo();
        for(Solicitud solicitud : listaSolicitudes){
            if (solicitud.getReceptor().getId().equals(receptorId) && solicitud.getEmisor().getId().equals(emisorId)) {
                utilLog.logInfo("Solicitud ya existente entre los dos usuarios.");
                return solicitud;
            }
        }
        return null;
    }

}
