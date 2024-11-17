package com.servidor.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import com.servidor.modelo.MarketPlace;
import com.servidor.util.UtilMarketPlace;

public class AuthServer {

    private static MarketPlace marketPlace;

    public static void main(String[] args) {
        // Inicializar UtilMarketPlace y MarketPlace
        UtilMarketPlace utilMarketPlace = UtilMarketPlace.getInstance(); // Asegúrate de tener una implementación para esto
        marketPlace = new MarketPlace(utilMarketPlace);

        int port = 12345; // Puerto en el que el servidor escuchará

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Servidor escuchando en el puerto " + port);
            
            while (true) {
                try (Socket clientSocket = serverSocket.accept();
                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {

                    System.out.println("Cliente conectado: " + clientSocket.getInetAddress());

                    // Leer el nombre de usuario y la contraseña del cliente
                    String cedula = in.readLine();
                    String contraseña = in.readLine();

                    // Verificar las credenciales usando el método iniciarSesion
                    String userId = marketPlace.iniciarSesion(cedula, contraseña);
                    
                    if (userId != null) {
                        out.println(userId); // Enviar el ID de usuario
                    } else {
                        out.println("ERROR: Credenciales incorrectas"); // Enviar un mensaje de error
                    }
                } catch (IOException e) {
                    System.err.println("Error al manejar la conexión del cliente: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Error al iniciar el servidor: " + e.getMessage());
        }
    }
}