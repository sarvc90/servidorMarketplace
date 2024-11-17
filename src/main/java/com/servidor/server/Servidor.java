package com.servidor.server;

import java.io.*;
import java.net.*;
import java.util.*;

public class Servidor {
    private static final int PUERTO = 12345; // Puerto del servidor
    private static Set<ClienteHandler> clientes = new HashSet<>(); // Conjunto para manejar múltiples clientes

    public static void main(String[] args) {
        System.out.println("Servidor iniciado en el puerto " + PUERTO);
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
            String[] partes = mensaje.split(" ", 2); // Dividir el mensaje en comando y datos
            String comando = partes[0];

            switch (comando) {
                case "LOGIN":
                    // Aquí puedes implementar la lógica de inicio de sesión
                    // Por ejemplo, verificar las credenciales
                    String[] credenciales = partes[1].split(" ");
                    String usuario = credenciales[0];
                    String contrasena = credenciales[1];
                    // Suponiendo que verificamos las credenciales y obtenemos un ID
                    String userId = verificarCredenciales(usuario, contrasena);
                    out.println(userId != null ? userId : "Error");
                    break;

                case "REGISTER":
                    // Lógica para registrar un nuevo vendedor
                    registrarVendedor(partes[1]);
                    break;

                case "GET_VENDEDOR":
                    // Lógica para obtener los datos del vendedor
                    obtenerVendedor(partes[1]);
                    break;

                case "UPDATE_VENDEDOR":
                    // Lógica para actualizar los datos del vendedor
                    actualizarVendedor(partes[1]);
                    break;

                case "GET_PRODUCTS":
                    // Lógica para obtener productos
                    obtenerProductos(partes[1]);
                    break;

                case "EXPORTAR_ESTADISTICAS":
                    // Lógica para exportar estadísticas
                    exportarEstadisticas(partes[1]);
                    break;

                case "OBTENER_PRODUCTOS":
                    // Lógica para obtener productos
                    obtenerProductos(partes[1]);
                    break;

                case "OBTENER_VENDEDORES":
                    // Lógica para obtener vendedores
                    obtenerVendedores(partes[1]);
                    break;

                case "ELIMINAR_PRODUCTO":
                    // Lógica para eliminar un producto
                    eliminarProducto(partes[1]);
                    break;

                case "ELIMINAR_VENDEDOR":
                    // Lógica para eliminar un vendedor
                    eliminarVendedor(partes[1]);
                    break;

                case "GET_NOTIFICATIONS":
                    // Lógica para obtener notificaciones
                    obtenerNotificaciones(partes[1]);
                    break;

                case "SEARCH":
                    // Lógica para buscar productos
                    buscarProductos(partes[1]);
                    break;

                case "COMPRAR":
                    // Lógica para comprar un producto
                    comprarProducto(partes[1]);
                    break;

                case "LIKE":
                    // Lógica para dar like a un producto
                    darLike(partes[1]);
                    break;

                case "LEERLIKES":
                    // Lógica para leer likes de un vendedor
                    leerLikes(partes[1]);
                    break;

                // Agrega más casos según sea necesario

                default:
                    out.println("Comando no reconocido");
            }
        }

        // Ejemplo de método para verificar credenciales
        private String verificarCredenciales(String usuario, String contrasena) {
            // Aquí deberías implementar la lógica para verificar las credenciales
            // Retornar el ID del usuario si es válido, o null si no lo es
            return "1"; // Placeholder para el ID del usuario
        }

        // Ejemplo de método para registrar un vendedor
        private void registrarVendedor(String datos) {
            // Aquí deberías implementar la lógica para registrar un vendedor
            out.println("Registro exitoso");
        }

        // Ejemplo de método para obtener un vendedor
        private void obtenerVendedor(String userId) {
            // Aquí deberías implementar la lógica para obtener los datos del vendedor
            out.println("Nombre,Apellido,Cedula,Contrasena,Direccion,Reputacion");
        }

        // Ejemplo de método para actualizar un vendedor
        private void actualizarVendedor(String datos) {
            // Aquí deberías implementar la lógica para actualizar los datos del vendedor
            out.println("Actualización exitosa");
        }

        // Ejemplo de método para obtener productos
        private void obtenerProductos(String userId) {
            // Aquí deberías implementar la lógica para obtener productos
            out.println("Producto1,Producto2,Producto3");
        }

        // Ejemplo de método para exportar estadísticas
        private void exportarEstadisticas(String userId) {
            // Aquí deberías implementar la lógica para exportar estadísticas
            out.println("Estadísticas exportadas con éxito");
        }

        // Ejemplo de método para obtener vendedores
        private void obtenerVendedores(String userId) {
            // Aquí deberías implementar la lógica para obtener vendedores
            out.println("Vendedor1,Vendedor2,Vendedor3");
        }

        // Ejemplo de método para eliminar un producto
        private void eliminarProducto(String productoId) {
            // Aquí deberías implementar la lógica para eliminar un producto
            out.println("Producto eliminado con éxito");
        }

        // Ejemplo de método para eliminar un vendedor
        private void eliminarVendedor(String vendedorId) {
            // Aquí deberías implementar la lógica para eliminar un vendedor
            out.println("Vendedor eliminado con éxito");
        }

        // Ejemplo de método para obtener notificaciones
        private void obtenerNotificaciones(String userId) {
            // Aquí deberías implementar la lógica para obtener notificaciones
            out.println("Notificación1;Notificación2;Notificación3");
        }

        // Ejemplo de método para buscar productos
        private void buscarProductos(String datos) {
            // Aquí deberías implementar la lógica para buscar productos
            out.println("Resultado de la búsqueda");
        }

        // Ejemplo de método para comprar un producto
        private void comprarProducto(String datos) {
            // Aquí deberías implementar la lógica para comprar un producto
            out.println("Compra realizada con éxito");
        }

        // Ejemplo de método para dar like a un producto
        private void darLike(String datos) {
            // Aquí deberías implementar la lógica para dar like a un producto
            out.println("Like dado con éxito");
        }

        // Ejemplo de método para leer likes de un vendedor
        private void leerLikes(String vendedorId) {
            // Aquí deberías implementar la lógica para leer likes de un vendedor
            out.println("Lista de likes");
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
    }
}