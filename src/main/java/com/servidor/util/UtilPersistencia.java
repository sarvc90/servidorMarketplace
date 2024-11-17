package com.servidor.util;

import java.util.logging.*;
import java.util.stream.Collectors;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalDouble; // Para el uso de OptionalDouble
import java.util.Set;

import com.servidor.modelo.Categoria;
import com.servidor.modelo.Comentario;
import com.servidor.modelo.Estado;
import com.servidor.modelo.EstadoSolicitud;
import com.servidor.modelo.Producto;
import com.servidor.modelo.Solicitud;
import com.servidor.modelo.Vendedor;


public class UtilPersistencia implements Serializable {
    private static UtilPersistencia instancia;
    private UtilProperties utilProperties;
    private UtilLog utilLog;
    private List<Vendedor> listaVendedoresCache = new ArrayList<>();

    // se crea la unica instancia de la clase
    private UtilPersistencia() {
        this.utilProperties = UtilProperties.getInstance();
        this.utilLog = UtilLog.getInstance();
    }

    // metodo que se encarga de gestionar la escritura de las listas de los obj
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

    // metodo que verifica que solo exista una instancia de la clase
    public static UtilPersistencia getInstance() {
        if (instancia == null) {
            instancia = new UtilPersistencia();
        }
        return instancia;
    }

    // CREAR

    private void escribirListaEnArchivo(String ruta, List<?> lista) {
        utilLog.escribirLog("Escribir lista en archivo: " + ruta, Level.INFO);

        if (ruta == null) {
            utilLog.escribirLog("La ruta de archivo es nula.", Level.SEVERE);
            return;
        }

        if (lista == null) {
            utilLog.escribirLog("La lista es nula y no se puede escribir en el archivo.", Level.SEVERE);
            return;
        }

        File archivo = new File(ruta);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(archivo))) {
            for (Object objeto : lista) {
                if (objeto == null) {
                    utilLog.escribirLog("El objeto en la lista es nulo, saltando este elemento.", Level.WARNING);
                    continue;
                }

                if (objeto instanceof Vendedor) {
                    Vendedor vendedor = (Vendedor) objeto;
                    guardarVendedorEnArchivo(vendedor);
                } else if (objeto instanceof Producto) {
                    Producto producto = (Producto) objeto;
                    guardarProductoEnArchivo(producto);
                } else if (objeto instanceof Solicitud) {
                    Solicitud solicitud = (Solicitud) objeto;
                    guardarSolicitudEnArchivo(solicitud);
                } else {
                    utilLog.escribirLog("Tipo de objeto desconocido: " + objeto.getClass().getName(), Level.WARNING);
                }
            }
            utilLog.escribirLog("Lista escrita en archivo correctamente: " + ruta, Level.INFO);
        } catch (IOException e) {
            utilLog.escribirLog("Error al escribir en el archivo: " + ruta + ", " + e.getMessage(), Level.SEVERE);
        }
    }

    // Guarda la información de una solicitud en un archivo de texto especificado en
    // las propiedades de configuración.
    public void guardarSolicitudEnArchivo(Solicitud solicitud) {
        String rutaSolicitudes = utilProperties.obtenerPropiedad("rutaSolicitudes.txt");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(rutaSolicitudes, true))) {
            String emisorId = solicitud.getEmisor() != null ? solicitud.getEmisor().getId() : "";
            String receptorId = solicitud.getReceptor() != null ? solicitud.getReceptor().getId() : "";
            writer.write(solicitud.getId() + "%" + emisorId + "%" + receptorId + "%" + solicitud.getEstado());
            writer.newLine();
            // Cada línea en el archivo representa una solicitud con sus atributos separados
            // por el carácter "%".
            utilLog.escribirLog("Solicitud guardada exitosamente: " + solicitud, Level.INFO);
            // Si ocurre un error al guardar, se registra un mensaje de error en el log.
        } catch (IOException e) {
            utilLog.escribirLog("Error al guardar la solicitud: " + solicitud, Level.SEVERE);
        }
    }

    // Guarda la información de un vendedor en un archivo de texto, incluyendo sus
    // publicaciones y contactos
    public void guardarVendedorEnArchivo(Vendedor vendedor) {
        if (vendedor == null) {
            utilLog.escribirLog("El vendedor es null, no se puede guardar.", Level.SEVERE);
            return; // Salir del método si el vendedor es null
        }

        String rutaVendedores = utilProperties.obtenerPropiedad("rutaVendedores.txt");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(rutaVendedores, true))) {
            // Manejar publicaciones
            String publicacionesStr = Optional.ofNullable(vendedor.getPublicaciones())
                    .map(publicaciones -> publicaciones.stream()
                            .filter(Objects::nonNull) // Filtrar elementos nulos
                            .map(Producto::getId) // Asegúrate de que Producto tenga el método getId()
                            .collect(Collectors.joining(",")) // Usar joining para crear una cadena
                    ).orElse("");

            // Manejar contactos
            String contactosStr = Optional.ofNullable(vendedor.getRedDeContactos())
                    .map(contactos -> contactos.stream()
                            .filter(Objects::nonNull) // Filtrar elementos nulos
                            .map(Vendedor::getCedula) // Cambiar a getCedula si es necesario
                            .collect(Collectors.joining(",")) // Usar joining para crear una cadena
                    ).orElse("");

            // Manejar calificaciones
            String calificacionesStr = Optional.ofNullable(vendedor.getCalificaciones())
                    .map(calificaciones -> calificaciones.stream()
                            .filter(Objects::nonNull) // Filtrar elementos nulos
                            .map(String::valueOf) // Asegúrate de que calificaciones sean convertibles a String
                            .collect(Collectors.joining(",")) // Usar joining para crear una cadena
                    ).orElse("");

            // Escribir en el archivo
            writer.write(vendedor.getId() + "%" + vendedor.getNombre() + "%" + vendedor.getApellido() + "%" +
                    vendedor.getCedula() + "%" + vendedor.getDireccion() + "%" + vendedor.getContraseña() + "%" +
                    publicacionesStr + "%" + contactosStr + "%" + calificacionesStr);
            writer.newLine();
        } catch (IOException e) {
            utilLog.escribirLog("Error al guardar el vendedor en el archivo: " + e.getMessage(), Level.SEVERE);
        }
    }

    // Guarda la información de un producto en un archivo de texto especificado en
    // las propiedades de configuración.
    public void guardarProductoEnArchivo(Producto producto) {
        String rutaProductos = utilProperties.obtenerPropiedad("rutaProductos.txt");
        utilLog.escribirLog("Ruta del archivo: " + rutaProductos, Level.INFO);

        // Convertir la lista de comentarios a una lista de strings usando toString de
        // Comentario
        List<String> comentariosStrings = producto.getComentarios().stream()
                .map(Comentario::toString)
                .collect(Collectors.toList());
        String comentariosString = String.join(";", comentariosStrings);
        // Manejar vendedores que dieron like
        String vendedoreslikeStr = Optional.ofNullable(producto.getVendedoresQueDieronLike())
                .map(vendedores -> vendedores.stream()
                        .reduce((c1, c2) -> c1 + "," + c2).orElse(""))
                .orElse("");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(rutaProductos, true))) { // 'true' para añadir al
                                                                                                // final del archivo
            writer.write(producto.getId() + "%" + producto.getNombre() + "%" + producto.getDescripcion() + "%"
                    + producto.getFechaPublicacion() + "%" + producto.getImagenRuta() + "%" + producto.getPrecio() + "%"
                    + producto.getMeGustas() + "%" + comentariosString + "%" + producto.getEstado() + "%"
                    + producto.getCategoria() + "%" + vendedoreslikeStr);
            writer.newLine();
            utilLog.escribirLog("Producto guardado exitosamente: " + producto, Level.INFO);
        } catch (IOException e) {
            utilLog.escribirLog("Error al guardar el producto: " + producto, Level.SEVERE);
        }
    }

    // Gestiona la escritura de IDs de productos y solicitudes en archivos
    // específicos según el estado actual de cada elemento.
    public void gestionarArchivosPorEstado(List<Producto> listaProductos, List<Solicitud> listaSolicitudes) {
        // Obtener rutas desde utilProperties
        String rutaProductosVendidos = utilProperties.obtenerPropiedad("rutaProductosVendidos.txt");
        String rutaProductosPublicados = utilProperties.obtenerPropiedad("rutaProductosPublicados.txt");
        String rutaProductosCancelados = utilProperties.obtenerPropiedad("rutaProductosCancelados.txt");

        String rutaSolicitudesPendientes = utilProperties.obtenerPropiedad("rutaSolicitudesPendientes.txt");
        String rutaSolicitudesAceptadas = utilProperties.obtenerPropiedad("rutaSolicitudesAceptadas.txt");
        String rutaSolicitudesRechazadas = utilProperties.obtenerPropiedad("rutaSolicitudesRechazadas.txt");

        // Gestionar productos
        // Borrar contenido de los archivos de productos
        borrarContenidoArchivo(rutaProductosVendidos);
        borrarContenidoArchivo(rutaProductosPublicados);
        borrarContenidoArchivo(rutaProductosCancelados);

        if (listaProductos.isEmpty()) {
            utilLog.escribirLog("La lista de productos está vacía. Se han borrado los archivos correspondientes.",
                    Level.INFO);
        } else {
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
        }

        // Gestionar solicitudes
        // Borrar contenido de los archivos de solicitudes
        borrarContenidoArchivo(rutaSolicitudesPendientes);
        borrarContenidoArchivo(rutaSolicitudesAceptadas);
        borrarContenidoArchivo(rutaSolicitudesRechazadas);

        if (listaSolicitudes.isEmpty()) {
            utilLog.escribirLog("La lista de solicitudes está vacía. Se han borrado los archivos correspondientes.",
                    Level.INFO);
        } else {
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
        }

        utilLog.escribirLog("Archivos gestionados por estado correctamente", Level.INFO);
    }

    // Escribe el ID especificado en el archivo ubicado en la ruta indicada.
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
        listaVendedoresCache.clear(); // Limpiar la lista anterior
        List<Vendedor> listaVendedores = new ArrayList<>();
        String rutaVendedores = utilProperties.obtenerPropiedad("rutaVendedores.txt");
        utilLog.logInfo("Ruta de vendedores: " + rutaVendedores);

        if (rutaVendedores == null) {
            utilLog.escribirLog("La ruta de vendedores es nula.", Level.SEVERE);
            return listaVendedores;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(rutaVendedores))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] datos = linea.split("%");

                // Validar el número de campos en 'datos'
                if (datos.length < 6) {
                    utilLog.escribirLog("Formato incorrecto en la línea: " + linea, Level.WARNING);
                    continue; // Saltar esta línea si no tiene el formato correcto
                }

                List<Producto> publicaciones = new ArrayList<>();
                if (datos.length > 8) {
                    String[] idsPublicaciones = datos[8].split(",");
                    for (String id : idsPublicaciones) {
                        Producto producto = buscarProductoPorId(id);
                        if (producto != null) { // Verificar que el producto no sea nulo
                            publicaciones.add(producto);
                        }
                    }
                }

                List<Vendedor> contactos = new ArrayList<>();
                if (datos.length > 7) {
                    String[] idsContactos = datos[7].split(",");
                    for (String cedula : idsContactos) {
                        Vendedor contacto = buscarVendedorPorCedula(cedula);
                        if (contacto != null) { // Verificar que el contacto no sea nulo
                            contactos.add(contacto);
                        }
                    }
                }

                // Manejar calificaciones
                List<Integer> calificaciones = new ArrayList<>();
                if (datos.length > 8 && !datos[8].isEmpty()) { // Asegurarse de que existe el índice 8
                    String[] calificacionesArray = datos[8].split(",");
                    for (String calificacionStr : calificacionesArray) {
                        try {
                            int calificacion = Integer.parseInt(calificacionStr);
                            calificaciones.add(calificacion);
                        } catch (NumberFormatException e) {
                            utilLog.escribirLog(
                                    "Error al convertir calificación: " + calificacionStr + " en la línea: " + linea,
                                    Level.WARNING);
                        }
                    }
                }

                // Promedio de calificaciones
                double promedioCalificaciones = 0;
                int contadorCalificaciones = 0;

                if (!calificaciones.isEmpty()) {
                    int suma = 0; // Inicializa la suma
                    // Sumar todas las calificaciones
                    for (int calificacion : calificaciones) {
                        suma += calificacion; // Sumar cada calificación
                    }
                    // Calcular el promedio
                    promedioCalificaciones = (double) suma / calificaciones.size(); // Convertir a double para obtener
                                                                                    // un promedio correcto
                    contadorCalificaciones = calificaciones.size(); // Contar las calificaciones válidas
                } else {
                    promedioCalificaciones = 0.0; // Si no hay calificaciones, el promedio es 0
                    contadorCalificaciones = 0; // No hay calificaciones, así que el contador es 0
                }

                Vendedor vendedor = new Vendedor(
                        datos[0], // ID
                        datos[1], // Nombre
                        datos[2], // Apellido
                        datos[3], // Cedula
                        datos[4], // Direccion
                        datos[5], // Contraseña
                        publicaciones, // Publicaciones
                        contactos, // Red de contactos
                        calificaciones, // Calificaciones
                        contadorCalificaciones, // Contador de calificaciones
                        promedioCalificaciones // Promedio de calificaciones
                );
                listaVendedores.add(vendedor);
                listaVendedoresCache.add(vendedor);
            }
            utilLog.escribirLog("Vendedores leídos desde el archivo correctamente.", Level.INFO);
        } catch (IOException e) {
            utilLog.escribirLog("Error al leer vendedores desde el archivo: " + rutaVendedores, Level.SEVERE);
        }
        return listaVendedores;
    }

    // Método para leer productos desde archivo
    // Método para leer productos desde archivo
    public List<Producto> leerProductosDesdeArchivo() {
        List<Producto> listaProductos = new ArrayList<>();
        String rutaProductos = utilProperties.obtenerPropiedad("rutaProductos.txt");

        try (BufferedReader reader = new BufferedReader(new FileReader(rutaProductos))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] datos = linea.split("%");
                // Verificar que el array tiene suficientes elementos
                if (datos.length < 11) {
                    utilLog.escribirLog("Línea mal formada en el archivo: " + linea, Level.WARNING);
                    continue; // Saltar a la siguiente línea
                }

                // Validar y parsear los enteros
                int precio = 0;
                int meGustas = 0;
                try {
                    precio = Integer.parseInt(datos[5]);
                    meGustas = Integer.parseInt(datos[6]);
                } catch (NumberFormatException e) {
                    utilLog.escribirLog("Error al convertir precios en la línea: " + linea, Level.WARNING);
                    continue; // Saltar a la siguiente línea si hay un error de formato
                }

                // Manejar lista de comentarios
                List<Comentario> comentarios = datos[7].isEmpty() || datos[7].equals("[]")
                        ? new ArrayList<>()
                        : Arrays.stream(datos[7].split(";"))
                                .map(Comentario::fromString) // Suponiendo que hay un método estático `fromString` en
                                                             // Comentario
                                .collect(Collectors.toList());

                // Manejar vendedores que dieron like
                Set<String> vendedoresQueDieronLike = Optional.ofNullable(datos[10])
                        .filter(vendedoresStr -> !vendedoresStr.isEmpty())
                        .map(vendedoresStr -> new HashSet<>(Arrays.asList(vendedoresStr.split(","))))
                        .orElse(new HashSet<>());

                // Manejar la conversión de Estado y Categoria con manejo de errores
                Estado estado;
                Categoria categoria;
                try {
                    estado = Estado.valueOf(datos[8]); // Convertir a Enum Estado
                    categoria = Categoria.valueOf(datos[9]); // Convertir a Enum Categoria
                } catch (IllegalArgumentException e) {
                    utilLog.escribirLog("Error al convertir Estado o Categoria en la línea: " + linea, Level.WARNING);
                    continue; // Saltar a la siguiente línea si hay un error de conversión
                }

                // Crear el objeto Producto
                Producto producto = new Producto(
                        datos[0], // ID
                        datos[1], // Nombre
                        datos[2], // Descripcion
                        datos[3], // Fecha Publicacion (string)
                        datos[4], // Imagen Ruta
                        precio, // Precio
                        meGustas, // Me Gustas
                        comentarios, // Comentarios
                        estado, // Estado (convertido a Enum)
                        categoria, // Categoria (convertido a Enum)
                        vendedoresQueDieronLike); // Vendedores que dieron like

                // Agregar el producto a la lista
                listaProductos.add(producto);
            }
            utilLog.escribirLog("Productos leídos desde el archivo correctamente.", Level.INFO);
        } catch (IOException e) {
            utilLog.escribirLog("Error al leer productos desde el archivo: " + rutaProductos, Level.SEVERE);
        }

        return listaProductos; // Retornar la lista de productos
    }

    // Método para leer solicitudes desde archivo
    public List<Solicitud> leerSolicitudesDesdeArchivo() {
        List<Solicitud> listaSolicitudes = new ArrayList<>();
        String rutaSolicitudes = utilProperties.obtenerPropiedad("rutaSolicitudes.txt");

        // Log the path for debugging
        utilLog.escribirLog("Ruta de solicitudes: " + rutaSolicitudes, Level.INFO);

        // Check if the path is valid
        if (rutaSolicitudes == null) {
            utilLog.escribirLog("La ruta de solicitudes es nula.", Level.SEVERE);
            return listaSolicitudes;
        }

        File file = new File(rutaSolicitudes);
        if (!file.exists()) {
            utilLog.escribirLog("El archivo no existe: " + rutaSolicitudes, Level.SEVERE);
            return listaSolicitudes;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(rutaSolicitudes))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                utilLog.escribirLog("Leyendo línea: " + linea, Level.INFO);

                String[] datos = linea.split("%");
                utilLog.escribirLog("Datos separados: " + Arrays.toString(datos), Level.INFO);

                if (datos.length < 4) {
                    utilLog.escribirLog("Datos incompletos en la línea: " + linea, Level.SEVERE);
                    continue;
                }

                String id = datos[0];
                String emisorId = datos[1];
                String receptorId = datos[2];
                EstadoSolicitud estado = null;
                try {
                    estado = EstadoSolicitud.valueOf(datos[3]);
                } catch (IllegalArgumentException e) {
                    utilLog.escribirLog("Estado no válido en la línea: " + linea, Level.SEVERE);
                    continue;
                }

                Vendedor emisor = buscarVendedorPorId(emisorId);
                Vendedor receptor = buscarVendedorPorId(receptorId);

                if (emisor == null || receptor == null) {
                    utilLog.escribirLog("Emisor o receptor no encontrado para la línea: " + linea, Level.SEVERE);
                    continue;
                }

                Solicitud solicitud = new Solicitud(id, emisor, receptor, estado);
                utilLog.escribirLog("Solicitud creada: " + solicitud.toString(), Level.INFO);

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
    public void cambiarEstadoSolicitud(String idSolicitud, EstadoSolicitud nuevoEstado, Vendedor vendedor) {
        List<Solicitud> listaSolicitudes = leerSolicitudesDesdeArchivo();
        utilLog.escribirLog("Lista de solicitudes cargada: " + listaSolicitudes.size() + " solicitudes", Level.INFO);

        boolean solicitudEncontrada = false;
        for (int i = 0; i < listaSolicitudes.size(); i++) {
            if (listaSolicitudes.get(i).getId().equals(idSolicitud)) {
                solicitudEncontrada = true;
                Vendedor receptor = listaSolicitudes.get(i).getReceptor();
                utilLog.escribirLog("Receptor de la solicitud: " + receptor.toString(), Level.INFO);
                utilLog.escribirLog("Vendedor actual: " + vendedor.toString(), Level.INFO);
                if (!receptor.getCedula().equals(vendedor.getCedula())) {
                    utilLog.escribirLog("El vendedor no tiene permiso para cambiar el estado de esta solicitud.",
                            Level.WARNING);
                    return; // Salir del método sin cambiar el estado
                }
                listaSolicitudes.get(i).setEstado(nuevoEstado);
                utilLog.escribirLog("Estado de la solicitud cambiado en memoria: " + idSolicitud, Level.INFO);
                if (listaSolicitudes.get(i).getEstado().equals(nuevoEstado)) {
                    utilLog.escribirLog("SI SE MODIFICO EL ESTADO DE SOLICITUD EN LISTA", Level.INFO);
                }
                break;
            }
        }

        if (!solicitudEncontrada) {
            utilLog.escribirLog("No se encontró la solicitud con ID: " + idSolicitud, Level.WARNING);
            return;
        }

        gestionarArchivos(leerVendedoresDesdeArchivo(), leerProductosDesdeArchivo(), listaSolicitudes);
        utilLog.escribirLog("Lista de solicitudes guardada con éxito", Level.INFO);
    }

    // Método para modificar un vendedor
    public void modificarVendedor(Vendedor vendedorModificado) {
        List<Vendedor> listaVendedores = leerVendedoresDesdeArchivo();
        for (int i = 0; i < listaVendedores.size(); i++) {
            if (listaVendedores.get(i).getCedula().equals(vendedorModificado.getCedula())) {
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
        for (Vendedor vendedor : listaVendedoresCache) {
            if (vendedor.getCedula().equals(cedula)) {
                return vendedor; // Retornar el vendedor encontrado
            }
        }
        return null; // Retornar null si no se encuentra
    }

    public Vendedor buscarVendedorPorId(String id) {
        List<Vendedor> listaVendedores = leerVendedoresDesdeArchivo();
        for (Vendedor vendedor : listaVendedores) {
            if (vendedor.getId().equals(id)) {
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

    // metodo que se encarga de buscar solicitud por emisor especifico
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

    // metodo que busca y devuelve la lista de solicitues de un receptor especifico
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

    // metodo que busca las solicitudes especifica entre receptor y emisor
    public Solicitud buscarSolicitudPorEmisorYReceptor(String emisorId, String receptorId) {
        List<Solicitud> listaSolicitudes = leerSolicitudesDesdeArchivo();
        for (Solicitud solicitud : listaSolicitudes) {
            if (solicitud.getReceptor().getId().equals(receptorId) && solicitud.getEmisor().getId().equals(emisorId)) {
                utilLog.logInfo("Solicitud ya existente entre los dos usuarios.");
                return solicitud;
            }
        }
        return null;
    }

    public List<Producto> leerProductosPublicadosDesdeArchivo() {
        List<Producto> productosPublicados = new ArrayList<>();
        String rutaProductosPublicados = utilProperties.obtenerPropiedad("rutaProductosPublicados.txt");
        try (BufferedReader reader = new BufferedReader(new FileReader(rutaProductosPublicados))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] datos = linea.split("%");
                String id = datos[0];
                Producto producto = buscarProductoPorId(id);
                productosPublicados.add(producto);
                utilLog.escribirLog("Productos publicados encontrado para el producto ID: " + producto.getId(),
                        Level.INFO);
            }
        } catch (IOException e) {
            utilLog.escribirLog("Error al leer productos publicados desde el archivo: " + rutaProductosPublicados,
                    Level.SEVERE);
        }
        return productosPublicados;
    }

    public List<Producto> leerProductosVendidosDesdeArchivo() {
        List<Producto> productosVendidos = new ArrayList<>();
        String rutaProductosVendidos = utilProperties.obtenerPropiedad("rutaProductosVendidos.txt");

        try (BufferedReader reader = new BufferedReader(new FileReader(rutaProductosVendidos))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] datos = linea.split("%");
                String id = datos[0];
                Producto producto = buscarProductoPorId(id);
                productosVendidos.add(producto);
                utilLog.escribirLog("Producto vendido encontrado para el producto ID: " + producto.getId(), Level.INFO);
            }
        } catch (IOException e) {
            utilLog.escribirLog("Error al leer productos vendidos desde el archivo: " + rutaProductosVendidos,
                    Level.SEVERE);
        }

        return productosVendidos;
    }

    public List<Producto> leerProductosCanceladosDesdeArchivo() {
        List<Producto> productosCancelados = new ArrayList<>();
        String rutaProductosCancelados = utilProperties.obtenerPropiedad("rutaProductosCancelados.txt");

        try (BufferedReader reader = new BufferedReader(new FileReader(rutaProductosCancelados))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] datos = linea.split("%");
                String id = datos[0];
                Producto producto = buscarProductoPorId(id);
                productosCancelados.add(producto);
                utilLog.escribirLog("Producto cancelado encontrado para el producto ID: " + producto.getId(),
                        Level.INFO);
            }
        } catch (IOException e) {
            utilLog.escribirLog("Error al leer productos cancelados desde el archivo: " + rutaProductosCancelados,
                    Level.SEVERE);
        }

        return productosCancelados;
    }

    // Método para buscar solicitudes pendientes por un vendedor
    public List<Solicitud> buscarSolicitudesPendientesPorVendedor(Vendedor vendedor) {
        List<Solicitud> solicitudesEncontradas = new ArrayList<>();
        String rutaSolicitudesPendientes = utilProperties.obtenerPropiedad("rutaSolicitudesPendientes.txt");

        try (BufferedReader reader = new BufferedReader(new FileReader(rutaSolicitudesPendientes))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] datos = linea.split("%");
                String id = datos[0];
                String emisorCedula = datos[1];
                String receptorCedula = datos[2];
                EstadoSolicitud estado = EstadoSolicitud.PENDIENTE; // Estado fijo para este método

                // Buscar los Vendedores por ID
                Vendedor emisor = buscarVendedorPorCedula(emisorCedula);
                Vendedor receptor = buscarVendedorPorCedula(receptorCedula);

                // Crear la solicitud
                Solicitud solicitud = new Solicitud(id, emisor, receptor, estado);

                // Verificar si el vendedor es emisor o receptor
                if (solicitud.getEmisor() != null && solicitud.getEmisor().getId().equals(vendedor.getId())) {
                    solicitudesEncontradas.add(solicitud);
                } else if (solicitud.getReceptor() != null
                        && solicitud.getReceptor().getId().equals(vendedor.getId())) {
                    solicitudesEncontradas.add(solicitud);
                }
            }
            utilLog.escribirLog("Solicitudes pendientes encontradas para el vendedor ID: " + vendedor.getId(),
                    Level.INFO);
        } catch (IOException e) {
            utilLog.escribirLog("Error al leer solicitudes pendientes desde el archivo: " + rutaSolicitudesPendientes,
                    Level.SEVERE);
        }

        return solicitudesEncontradas;
    }

    // Método para buscar solicitudes aceptadas por un vendedor
    public List<Solicitud> buscarSolicitudesAceptadasPorVendedor(Vendedor vendedor) {
        List<Solicitud> solicitudesEncontradas = new ArrayList<>();
        String rutaSolicitudesAceptadas = utilProperties.obtenerPropiedad("rutaSolicitudesAceptadas.txt");

        try (BufferedReader reader = new BufferedReader(new FileReader(rutaSolicitudesAceptadas))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] datos = linea.split("%");

                // Verificar que el array tiene suficientes elementos
                if (datos.length < 3) {
                    utilLog.escribirLog("Línea mal formada en el archivo: " + linea, Level.WARNING);
                    continue; // Saltar a la siguiente línea
                }

                String id = datos[0];
                String emisorCedula = datos[1];
                String receptorCedula = datos[2];
                EstadoSolicitud estado = EstadoSolicitud.ACEPTADA; // Estado fijo para este método

                // Buscar los Vendedores por ID
                Vendedor emisor = buscarVendedorPorCedula(emisorCedula);
                Vendedor receptor = buscarVendedorPorCedula(receptorCedula);

                // Crear la solicitud
                Solicitud solicitud = new Solicitud(id, emisor, receptor, estado);

                // Verificar si el vendedor es emisor o receptor
                if (solicitud.getEmisor() != null && solicitud.getEmisor().getId().equals(vendedor.getId())) {
                    solicitudesEncontradas.add(solicitud);
                } else if (solicitud.getReceptor() != null
                        && solicitud.getReceptor().getId().equals(vendedor.getId())) {
                    solicitudesEncontradas.add(solicitud);
                }
            }
            utilLog.escribirLog("Solicitudes aceptadas encontradas para el vendedor ID: " + vendedor.getId(),
                    Level.INFO);
        } catch (IOException e) {
            utilLog.escribirLog("Error al leer solicitudes aceptadas desde el archivo: " + rutaSolicitudesAceptadas,
                    Level.SEVERE);
        }

        return solicitudesEncontradas;
    }

    // Método para buscar solicitudes rechazadas por un vendedor
    public List<Solicitud> buscarSolicitudesRechazadasPorVendedor(Vendedor vendedor) {
        List<Solicitud> solicitudesEncontradas = new ArrayList<>();
        String rutaSolicitudesRechazadas = utilProperties.obtenerPropiedad("rutaSolicitudesRechazadas.txt");

        try (BufferedReader reader = new BufferedReader(new FileReader(rutaSolicitudesRechazadas))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] datos = linea.split("%");
                String id = datos[0];
                String emisorCedula = datos[1];
                String receptorCedula = datos[2];
                EstadoSolicitud estado = EstadoSolicitud.RECHAZADA; // Estado fijo para este método

                // Buscar los Vendedores por ID
                Vendedor emisor = buscarVendedorPorCedula(emisorCedula);
                Vendedor receptor = buscarVendedorPorCedula(receptorCedula);

                // Crear la solicitud
                Solicitud solicitud = new Solicitud(id, emisor, receptor, estado);

                // Verificar si el vendedor es emisor o receptor
                if (solicitud.getEmisor() != null && solicitud.getEmisor().getId().equals(vendedor.getId())) {
                    solicitudesEncontradas.add(solicitud);
                } else if (solicitud.getReceptor() != null
                        && solicitud.getReceptor().getId().equals(vendedor.getId())) {
                    solicitudesEncontradas.add(solicitud);
                }
            }
            utilLog.escribirLog("Solicitudes rechazadas encontradas para el vendedor ID: " + vendedor.getId(),
                    Level.INFO);
        } catch (IOException e) {
            utilLog.escribirLog("Error al leer solicitudes rechazadas desde el archivo: " + rutaSolicitudesRechazadas,
                    Level.SEVERE);
        }

        return solicitudesEncontradas;
    }

    public void borrarContenidoArchivo(String rutaArchivo) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(rutaArchivo))) {
            // Simplemente crear un nuevo archivo vacío
            writer.write(""); // Esto borra el contenido del archivo
            utilLog.escribirLog("Se borro correctamente el contenido del archivo: " + rutaArchivo, Level.INFO);
        } catch (IOException e) {
            utilLog.escribirLog("Error al borrar el contenido del archivo: " + rutaArchivo, Level.SEVERE);
        }
    }

    public void agregarProductoAVendedor(Producto producto, Vendedor vendedor) {
        List<Vendedor> vendedores = leerVendedoresDesdeArchivo();
        for (int i = 0; i < vendedores.size(); i++) {
            if (vendedores.get(i).getCedula().equals(vendedor.getCedula())) {
                List<Producto> productos = vendedores.get(i).getPublicaciones();
                productos.add(producto);
                vendedores.get(i).setPublicaciones(productos);
                break;
            }
        }
        gestionarArchivos(vendedores, leerProductosDesdeArchivo(), leerSolicitudesDesdeArchivo());
    }

    public void agregarSolicitudAVendedor(Solicitud solicitud, Vendedor vendedor) {
        List<Vendedor> vendedores = leerVendedoresDesdeArchivo();
        for (int i = 0; i < vendedores.size(); i++) {
            if (vendedores.get(i).getCedula().equals(vendedor.getCedula())) {
                List<Solicitud> solicitudes = vendedores.get(i).obtenerSolicitudesAceptadas();
                solicitudes.add(solicitud);
                List<Vendedor> redDeContactos = new ArrayList<>();
                for (Solicitud solicitud1 : solicitudes) {
                    if (solicitud1.getEmisor().getCedula() == vendedor.getCedula()) {
                        redDeContactos.add(solicitud1.getReceptor());
                    } else {
                        redDeContactos.add(solicitud1.getEmisor());
                    }
                }

                // Obtener emisor o receptor independientemente solo que sea igual al vendedor,
                // agregue al vendedor a una lista y ahí si set red de contactos
                vendedores.get(i).setRedDeContactos(redDeContactos);
                break;
            }
        }
        gestionarArchivos(vendedores, leerProductosDesdeArchivo(), leerSolicitudesDesdeArchivo());
    }

}
