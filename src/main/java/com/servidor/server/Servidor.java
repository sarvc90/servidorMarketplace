package com.servidor.server;

import java.io.*;
import java.net.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;

import com.servidor.excepciones.*;
import com.servidor.modelo.*;
import com.servidor.util.UtilMarketPlace;

public class Servidor {
    private static final int PUERTO = 12345; // Puerto del servidor
    private static Set<ClienteHandler> clientes = new CopyOnWriteArraySet<>(); // Conjunto para manejar múltiples
                                                                               // clientes de forma concurrente
    private static MarketPlace marketPlace; // Instancia de MarketPlace

    public static void main(String[] args) {
        System.out.println("Servidor iniciado en el puerto " + PUERTO);
        marketPlace = new MarketPlace(new UtilMarketPlace()); // Inicializar MarketPlace

        try (ServerSocket serverSocket = new ServerSocket(PUERTO)) {
            while (true) {
                Socket socket = serverSocket.accept(); // Aceptar nuevas conexiones
                ClienteHandler clienteHandler = new ClienteHandler(socket);
                clientes.add(clienteHandler);
                new Thread(clienteHandler).start(); // Iniciar un nuevo hilo para manejar el cliente
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Clase interna para manejar cada cliente
    private static class ClienteHandler implements Runnable {
        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;

        public ClienteHandler(Socket socket) {
            this.socket = socket;
            try {
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            String mensaje;
            try {
                while ((mensaje = in.readLine()) != null) {
                    System.out.println("Mensaje recibido: " + mensaje);
                    procesarMensaje(mensaje); // Procesar el mensaje recibido
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                cerrarConexiones();
            }
        }

        private void procesarMensaje(String mensaje) {
            if (mensaje == null || mensaje.trim().isEmpty()) {
                out.println("Error: Mensaje vacío o mal formado.");
                return;
            }

            String[] partes = mensaje.split(" ", 2);
            if (partes.length < 2) {
                out.println("Error: Comando o datos faltantes.");
                return;
            }

            String comando = partes[0];

            try {
                switch (comando) {
                    case "LOGIN":
                        procesarLogin(partes[1]);
                        break;
                    case "REGISTER":
                        registrarVendedor(partes[1]);
                        break;
                    case "GET_VENDEDOR":
                        obtenerVendedor(partes[1]);
                        break;
                    case "UPDATE_VENDEDOR":
                        actualizarVendedor(partes[1]);
                        break;
                    case "OBTENER_PRODUCTOS":
                        obtenerProductos();
                        break;
                    case "EXPORTAR_ESTADISTICAS":
                        manejarExportarEstadisticas(partes[1]);
                        break;
                    case "GET_NOTIFICATIONS":
                        obtenerNotificaciones(partes[1]);
                        break;
                    case "SEARCH":
                        buscarProductos(partes[1]);
                        break;
                    case "COMPRAR":
                        comprarProducto(partes[1]);
                        break;
                    case "LIKE":
                        darLike(partes[1]);
                        break;
                    case "LEERLIKES":
                        leerLikes(partes[1]);
                        break;
                    case "OBTENER_VENDEDORES":
                        obtenerVendedores();
                        break;
                    case "ELIMINAR_VENDEDOR":
                        eliminarVendedor(partes[1]);
                        break;
                    case "ELIMINAR_PRODUCTO":
                        eliminarProducto(partes[1]);
                        break;
                    case "MENSAJES_ENTRE_VENDEDORES":
                        manejarMensajesEntreVendedores(partes[1]);
                        break;
                    case "PRODUCTOS_PUBLICADOS_RANGO":
                        manejarProductosPublicadosRango(partes[1]);
                        break;
                    case "PRODUCTOS_PUBLICADOS_POR_VENDEDOR":
                        manejarProductosPublicadosPorVendedor(partes[1]);
                        break;
                    case "CONTACTOS_POR_VENDEDOR":
                        manejarContactosPorVendedor(partes[1]);
                        break;
                    case "TOP_10_PRODUCTOS_POPULARES":
                        manejarTop10ProductosPopulares();
                        break;
                    case "CREAR_PRODUCTO":
                        crearProducto(partes);
                        break;
                    default:
                        out.println("Comando no reconocido.");
                }
            } catch (Exception e) {
                out.println("Error procesando el comando: " + e.getMessage());
            }
        }

        private void crearProducto(String[] datos) {
            if (datos.length < 9) { // Cambia a 9 porque ahora hay un ID de vendedor
                out.println("Error: Datos del producto incompletos.");
                return;
            }

            String vendedorId = datos[0]; // Obtener el ID del vendedor
            String nombre = datos[1];
            String descripcion = datos[2];
            LocalDateTime fechaPublicacion = LocalDateTime.parse(datos[3]);
            String fechaPublicacionString = fechaPublicacion.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            String imagenRuta = datos[4];
            int precio;
            try {
                precio = Integer.parseInt(datos[5]);
            } catch (NumberFormatException e) {
                out.println("Error: El precio debe ser un número válido.");
                return;
            }
            int meGustas = Integer.parseInt(datos[6]);
            String estado = datos[7];
            String categoriaString = datos[8];

            // Convertir el String de categoría a enum
            Categoria categoria = Categoria.fromString(categoriaString);
            if (categoria == null) {
                out.println("Error: Categoría no válida.");
                return;
            }
            Estado estado1 = Estado.fromString(categoriaString);
            if (estado1 == null) {
                out.println("Error: Estado no válida.");
                return;
            }

            // Aquí puedes buscar el vendedor por ID y crear el producto asociado
            Vendedor vendedor = buscarVendedorPorId(vendedorId);
            if (vendedor == null) {
                out.println("Error: Vendedor no encontrado.");
                return;
            }
            Set<String> vendedoresQueDieronLike = null;
            String id = null;
            // Crear el objeto ProductoDTO (o similar) y guardar en la base de datos
            Producto nuevoProducto = new Producto(
                    id, // id (puedes generarlo en la base de datos)
                    nombre,
                    descripcion,
                    fechaPublicacionString,
                    imagenRuta,
                    precio,
                    meGustas,
                    new ArrayList<>(), // comentarios
                    estado1,
                    categoria, // ahora es un enum,
                    vendedoresQueDieronLike);

            // Lógica para guardar el producto en la base de datos
            boolean exito = guardarProductoEnBaseDeDatos(nuevoProducto, vendedor);

            // Enviar respuesta al cliente
            out.println(exito ? "Éxito" : "Error al crear el producto.");
        }

        // Método para buscar el vendedor por ID
        private Vendedor buscarVendedorPorId(String vendedorId) {
            return marketPlace.buscarVendedorPorId(vendedorId); // Placeholder, cambia esto por la lógica real
        }

        // Método para guardar el producto en la base de datos
        private boolean guardarProductoEnBaseDeDatos(Producto producto, Vendedor vendedor) {
            try {
                vendedor.crearProducto(producto);
            } catch (ProductoYaExisteException e) {
                e.printStackTrace();
            }
            return true;
        }

        private void manejarExportarEstadisticas(String mensaje) {
            String[] partes = mensaje.split(" ", 3); // Dividir en 3 partes: comando, userId, ruta
            if (partes.length < 3) {
                out.println("Error: Mensaje incompleto.");
                return;
            }
        
            String userId = partes[1];
            String rutaSeleccionada = partes[2];
        
            // Aquí deberías implementar la lógica para obtener las estadísticas del usuario
            String nombre = marketPlace.buscarVendedorPorId(userId).getNombre();
            String estadisticas = marketPlace.generarReporte(nombre, "01/01/2000", "18/11/2024", userId);
        
            if (estadisticas != null) {
                // Devolver las estadísticas como respuesta al cliente
                out.println("Estadísticas para el usuario " + userId + ": " + estadisticas);
            } else {
                out.println("Error: No se encontraron estadísticas para el usuario " + userId);
            }
        }
        

        

        private void manejarMensajesEntreVendedores(String datos) {
            String[] vendedores = datos.split(",");
            if (vendedores.length < 2) {
                out.println("Error: Se necesitan dos vendedores.");
                return;
            }
            String vendedor1 = vendedores[0];
            String vendedor2 = vendedores[1];

            // Aquí deberías implementar la lógica para obtener los mensajes entre los dos
            // vendedores
            String mensajes = obtenerMensajesEntreVendedores(vendedor1, vendedor2);
            out.println(mensajes);
        }

        private void manejarProductosPublicadosRango(String datos) {
            String[] fechas = datos.split(",");
            LocalDateTime startDateTime = LocalDateTime.parse(fechas[0]);
            LocalDateTime endDateTime = LocalDateTime.parse(fechas[1]);

            // Convertir LocalDateTime a String
            String startDateStr = startDateTime.toString();
            String endDateStr = endDateTime.toString();

            // Lógica para obtener los productos publicados entre las fechas
            int productos = marketPlace.contarProductosPorRangoFecha(startDateStr, endDateStr);
            if (productos != 0) {
                StringBuilder sb = new StringBuilder();
                sb.append(productos).append("\n");
                out.println(sb.toString());
            } else {
                out.println("No hay productos publicados en este rango de fechas.");
            }
        }

        private void manejarProductosPublicadosPorVendedor(String datos) {
            // Lógica para obtener los productos publicados por el vendedor
            int productos = marketPlace.contarProductosPorVendedor(datos);
            if (productos != 0) {
                StringBuilder sb = new StringBuilder();
                sb.append(productos).append("\n");
                out.println(sb.toString());
            } else {
                out.println("No hay productos publicados por este vendedor.");
            }
        }

        private void manejarContactosPorVendedor(String datos) {
            // Lógica para obtener los contactos del vendedor
            int contactos = marketPlace.contarContactosPorVendedor(datos);
            if (contactos != 0) {
                StringBuilder sb = new StringBuilder();
                sb.append(contactos).append("\n");
                out.println(sb.toString());
            } else {
                out.println("No hay contactos disponibles para este vendedor.");
            }
        }

        private void manejarTop10ProductosPopulares() {
            // Lógica para obtener los 10 productos más populares
            List<Producto> productosPopulares = marketPlace.obtenerTop10ProductosPopulares();
            if (!productosPopulares.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                for (Producto producto : productosPopulares) {
                    sb.append(producto.toString()).append("\n");
                }
                out.println(sb.toString());
            } else {
                out.println("No hay productos populares disponibles.");
            }
        }

        private void obtenerVendedores() {
            List<Vendedor> vendedores = marketPlace.getVendedores();
            if (!vendedores.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                for (Vendedor vendedor : vendedores) {
                    sb.append(vendedor.toString()).append("\n"); // Formato de salida de vendedores
                }
                out.println(sb.toString());
            } else {
                out.println("No hay vendedores disponibles.");
            }
        }

        private String obtenerMensajesEntreVendedores(String vendedor1, String vendedor2) {
            // Implementa la lógica para obtener los mensajes entre los dos vendedores
            // Esto puede incluir consultas a la base de datos o a una lista de mensajes
            return "Mensajes entre " + vendedor1 + " y " + vendedor2;
        }

        private void eliminarProducto(String productoId) {
            //boolean exito = marketPlace.eliminarProducto(productoId); // Implementa la lógica en MarketPlace
            //out.println(exito ? "Producto eliminado con éxito." : "Error: Producto no encontrado.");
        }


        private void actualizarVendedor(String datos) {
            try {
                // Dividir los datos recibidos
                String[] partes = datos.split(",");
                if (partes.length < 6) {
                    out.println("ERROR");
                    return;
                }

                String userId = partes[0];
                String nombres = partes[1];
                String apellidos = partes[2];
                String cedula = partes[3];
                String contrasena = partes[4];
                String direccion = partes[5];

                // Crear un objeto Vendedor con los datos actualizados
                Vendedor vendedorActualizado = new Vendedor(
                        userId,
                        nombres,
                        apellidos,
                        cedula,
                        contrasena,
                        direccion,
                        null, // Mantener los valores existentes para los campos no actualizados
                        null,
                        null,
                        0,
                        0.0);

                // Intentar modificar el vendedor
                boolean success = marketPlace.modificarVendedor(vendedorActualizado);

                // Enviar respuesta al cliente
                out.println(success ? "SUCCESS" : "ERROR");

            } catch (Exception e) {
                out.println("ERROR");
                e.printStackTrace();
            }
        }

        private void procesarLogin(String datos) {
            String[] credenciales = datos.split(" ");
            if (credenciales.length < 2) {
                out.println("Error: Datos de login incompletos.");
                return;
            }
            String usuario = credenciales[0];
            String contrasena = credenciales[1];
            String userId = marketPlace.iniciarSesion(usuario, contrasena);
            out.println(userId != null ? userId : "Error: Usuario o contraseña incorrectos.");
        }

        private void registrarVendedor(String datos) {
            Vendedor nuevoVendedor = crearVendedorDesdeDatos(datos);
            try {
                marketPlace.crearVendedor(nuevoVendedor);
                out.println("Registro exitoso");
            } catch (UsuarioExistenteException e) {
                out.println("Error: El vendedor ya existe.");
            }
        }


        private void obtenerProductos() {
            List<Producto> productos = marketPlace.obtenerProductos();
            if (!productos.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                for (Producto producto : productos) {
                    sb.append(producto.toString()).append("\n"); // Formato de salida de productos
                }
                out.println(sb.toString());
            } else {
                out.println("No hay productos disponibles.");
            }
        }

        private void exportarEstadisticas(String userId) {
            // Lógica para exportar estadísticas (simulada)
            out.println("Estadísticas exportadas con éxito.");
        }

        private void obtenerVendedor(String userId) {
            try {
                Vendedor vendedor = marketPlace.buscarVendedorPorId(userId);
                if (vendedor != null) {
                    // Formatear la respuesta como en el cliente:
                    // nombres,apellidos,cedula,contrasena,direccion,reputacion
                    String respuesta = String.format("%s,%s,%s,%s,%s,%s",
                            vendedor.getNombre(),
                            vendedor.getApellido(),
                            vendedor.getCedula(),
                            vendedor.getContraseña(),
                            vendedor.getDireccion(),
                            vendedor.getPromedioCalificaciones()); 
                    out.println(respuesta);
                } else {
                    out.println("Error: Vendedor no encontrado.");
                }
            } catch (Exception e) {
                out.println("Error al obtener el vendedor: " + e.getMessage());
            }
        }

        private void eliminarVendedor(String vendedorId) {
            try {
                marketPlace.eliminarVendedor(vendedorId);
                out.println("Vendedor eliminado con éxito.");
            } catch (UsuarioNoEncontradoException e) {
                out.println("Error: Vendedor no encontrado.");
            }
        }

        private void obtenerNotificaciones(String userId) {
            // Lógica para obtener notificaciones (simulada)
            out.println("Notificación1; Notificación2; Notificación3");
        }

        private void buscarProductos(String datos) {
            // Lógica para buscar productos (simulada)
            out.println("Resultado de la búsqueda de productos.");
        }

        private void comprarProducto(String datos) {
            // Lógica para comprar productos (simulada)
            out.println("Compra realizada con éxito.");
        }

        private void darLike(String datos) {
            String[] partes = datos.split(":");
            if (partes.length < 2) {
                out.println("Error: Datos incompletos para dar like.");
                return;
            }
            
            String vendedorId = partes[0];
            String productoId = partes[1];
            // Lógica para dar like al producto
            boolean exito = marketPlace.darLike(marketPlace.buscarVendedorPorId(vendedorId), marketPlace.buscarProductoPorId(productoId)); // Implementa esta lógica en MarketPlace
            out.println(exito ? "Like dado con éxito." : "Error: No se pudo dar like.");
        }

        private void leerLikes(String vendedorId) {
            // Lógica para leer los likes de un vendedor (simulada)
            out.println("Lista de likes del vendedor.");
        }





        private void cerrarConexiones() {
            try {
                if (out != null)
                    out.close();
                if (in != null)
                    in.close();
                if (socket != null)
                    socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private Vendedor crearVendedorDesdeDatos(String datos) {
            String[] info = datos.split(",");
            String id = null; // Asignar un ID adecuado si es necesario
            return new Vendedor(id, info[0], info[1], info[2], info[3], info[4], null, null, null, 0, 0.0);
        }
    }
}