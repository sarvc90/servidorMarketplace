package com.servidor.server;

import java.io.*;
import java.net.*;
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
                    //case "ELIMINAR_PRODUCTO":
                        //eliminarProducto(partes[1]);
                        //break;
                    case "ELIMINAR_VENDEDOR":
                        eliminarVendedor(partes[1]);
                        break;
                    default:
                        out.println("Comando no reconocido.");
                }
            } catch (Exception e) {
                out.println("Error procesando el comando: " + e.getMessage());
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

        private void obtenerVendedor(String userId) {
            Vendedor vendedor = marketPlace.obtenerVendedores().stream()
                .filter(v -> v.getId().equals(userId))
                .findFirst()
                .orElse(null);
            if (vendedor != null) {
                out.println(vendedor.toString());
            } else {
                out.println("Error: Vendedor no encontrado.");
            }
        }

        private void actualizarVendedor(String datos) {
            Vendedor vendedorModificado = crearVendedorDesdeDatos(datos);
            try {
                marketPlace.modificarVendedor(vendedorModificado);
                out.println("Actualización exitosa");
            } catch (UsuarioNoEncontradoException e) {
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
/* 
        private void eliminarProducto(String productoId) {
            try {
                marketPlace.eliminarProducto(productoId);
                out.println("Producto eliminado con éxito");
            } catch (ProductoNoEncontradoException e) {
                out.println("Error: Producto no encontrado.");
            }
        }
*/
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
            return new Vendedor(info[0], info[1], info[2], info[3], info[4], info[5], null, null, null, 0, 0.0);
        }
    }
}
