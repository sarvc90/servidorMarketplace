/*package com.servidor.server;

import java.io.*;
import java.net.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;

import com.servidor.excepciones.*;
import com.servidor.modelo.*;
import com.servidor.util.UtilMarketPlace;

public class Servidor {
    private static final int PUERTO = 12345; // Puerto del servidor
    private static Set<ClienteHandler> clientes = new CopyOnWriteArraySet<>(); // Conjunto para manejar múltiples clientes de forma concurrente
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
                    case "GET_PRODUCTS":
                    case "OBTENER_PRODUCTOS":
                        obtenerProductos(partes[1]);
                        break;
                    case "EXPORTAR_ESTADISTICAS":
                        exportarEstadisticas(partes[1]);
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
                        obtenerVendedores(partes[1]);
                        break;
                    case "ELIMINAR_VENDEDOR":
                        eliminarVendedor(partes[1]);
                        break;
                        case "MENSAJES_ENTRE_VENDEDORES":
                        manejarMensajesEntreVendedores(datos);
                        break;
                    case "PRODUCTOS_PUBLICADOS_RANGO":
                        manejarProductosPublicadosRango(datos);
                        break;
                    case "PRODUCTOS_PUBLICADOS_POR_VENDEDOR":
                        manejarProductosPublicadosPorVendedor(datos);
                        break;
                    case "CONTACTOS_POR_VENDEDOR":
                        manejarContactosPorVendedor(datos);
                        break;
                    case "TOP_10_PRODUCTOS_POPULARES":
                        manejarTop10ProductosPopulares();
                        break;
                    default:
                        out.println("Comando no reconocido.");
                }
            } catch (Exception e) {
                out.println("Error procesando el comando: " + e.getMessage());
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

    // Aquí deberías implementar la lógica para obtener los mensajes entre los dos vendedores
    String mensajes = obtenerMensajesEntreVendedores(vendedor1, vendedor2);
    out.println(mensajes);
}

private void manejarProductosPublicadosRango(String datos) {
    String[] fechas = datos.split(",");
    LocalDate startDate = LocalDate.parse(fechas[0]);
    LocalDate endDate = LocalDate.parse(fechas[1]);

    // Lógica para obtener los productos publicados entre las fechas
    String productos = obtenerProductosPublicadosRango(startDate, endDate);
    out.println(productos);
}

private void manejarProductosPublicadosPorVendedor(String datos) {
    // Lógica para obtener los productos publicados por el vendedor
    String productos = obtenerProductosPublicadosPorVendedor(datos);
    out.println(productos);
}

private void manejarContactosPorVendedor(String datos) {
    // Lógica para obtener los contactos del vendedor
    String contactos = obtenerContactosPorVendedor(datos);
    out.println(contactos);
}

private void manejarTop10ProductosPopulares() {
    // Lógica para obtener los 10 productos más populares
    String productos = obtenerTop10ProductosPopulares();
    out.println(productos);
}
        private void procesarMensaje(String mensaje) {
    // Suponiendo que el mensaje contiene información del producto
    String[] partes = mensaje.split(",");
    
    if (partes.length < 8) {
        out.println("Error: Datos del producto incompletos.");
        return;
    }

    String nombre = partes[0];
    String descripcion = partes[1];
    LocalDateTime fechaPublicacion = LocalDateTime.parse(partes[2]);
    String imagenRuta = partes[3];
    int precio = Integer.parseInt(partes[4]);
    int meGustas = Integer.parseInt(partes[5]);
    String estado = partes[6];
    String categoria = partes[7];

    // Crear el objeto ProductoDTO (o similar) y guardar en la base de datos
    ProductoDTO nuevoProducto = new ProductoDTO(
            null, // id (puedes generarlo en la base de datos)
            nombre,
            descripcion,
            fechaPublicacion,
            imagenRuta,
            precio,
            meGustas,
            new ArrayList<>(), // comentarios
            estado,
            categoria
    );

    // Lógica para guardar el producto en la base de datos
    boolean exito = guardarProductoEnBaseDeDatos(nuevoProducto);

    // Enviar respuesta al cliente
    if (exito) {
        out.println("Éxito");
    } else {
        out.println("Error al crear el producto.");
    }
}

// Método para guardar el producto en la base de datos
private boolean guardarProductoEnBaseDeDatos(ProductoDTO producto) {
    // Implementa la lógica para guardar el producto en tu base de datos
    // Retorna true si el producto se guardó correctamente, de lo contrario false
    return true; // Placeholder, cambia esto por la lógica real
}
        private void actualizarVendedor(String datos) {
            
            Vendedor vendedorModificado = crearVendedorDesdeDatos(datos);
            marketPlace.modificarVendedor(vendedorModificado);
            out.println("Actualización exitosa");
            boolean success = marketPlace.modificarVendedor(vendedorModificado);
            out.println(success ? "SUCCESS" : "ERROR");
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

        private void obtenerVendedor(String userId) {
            Vendedor vendedor = marketPlace.getVendedores().stream()
                .filter(v -> v.getId().equals(userId))
                .findFirst()
                .orElse(null);
            if (vendedor != null) {
                out.println(vendedor.toString());
            } else {
                out.println("Error: Vendedor no encontrado.");
            }
        }

        private void obtenerProductos(String userId) {
            List<Producto> productos = marketPlace.obtenerProductos();
            if (!productos.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                for (Producto producto : productos) {
                    sb.append(producto.getId()).append(", ");
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

        private void obtenerVendedores(String userId) {
            List<Vendedor> vendedores = marketPlace.obtenerVendedores();
            if (!vendedores.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                for (Vendedor vendedor : vendedores) {
                    sb.append(vendedor.getId()).append(", ");
                }
                out.println(sb.toString());
            } else {
                out.println("No hay vendedores disponibles.");
            }
        }

        private void eliminarVendedor(String vendedorId) {
            try {
                marketPlace.eliminarVendedor(vendedorId);
                out.println("Vendedor eliminado con éxito");
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
            // Lógica para dar like a un producto (simulada)
            out.println("Like dado con éxito.");
        }

        private void leerLikes(String vendedorId) {
            // Lógica para leer los likes de un vendedor (simulada)
            out.println("Lista de likes del vendedor.");
        }

        private void procesarMensaje(String mensaje) {
            String[] partes = mensaje.split(" ", 2);
            
            if (partes.length < 2) {
                out.println("Error: Comando o datos faltantes.");
                return;
            }
        
            String comando = partes[0];
            String datos = partes[1];
        
            switch (comando) {
                case "EXPORTAR_ESTADISTICAS":
                    manejarExportarEstadisticas(datos);
                    break;
                // Otros casos...
                default:
                    out.println("Comando no reconocido.");
            }
        }
        
        private void manejarExportarEstadisticas(String userId) {
            // Lógica para obtener las estadísticas del usuario
            String estadisticas = obtenerEstadisticasPorUsuario(userId);
            
            if (estadisticas != null) {
                // Enviar las estadísticas al cliente
                out.println(estadisticas);
            } else {
                out.println("Error: No se pudieron obtener las estadísticas.");
            }
        }
        
        // Método que obtiene las estadísticas del usuario (implementación ficticia)
        private String obtenerEstadisticasPorUsuario(String userId) {
            // Aquí deberías implementar la lógica para obtener las estadísticas del usuario
            // Esto puede incluir consultas a la base de datos u otras fuentes de datos
            return "Estadísticas para el usuario " + userId; // Placeholder
        }

        private void cerrarConexiones() {
            try {
                if (out != null) out.close();
                if (in != null) in.close();
                if (socket != null) socket.close();
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
}*/