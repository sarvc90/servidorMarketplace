package com.servidor;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;

public class IniciarSesionController {

    @FXML
    private TextField nombreField;

    @FXML
    private PasswordField contrasenaField;

    @FXML
    private void handleLogin() {
        String nombre = nombreField.getText();
        String contrasena = contrasenaField.getText();

        // Aquí puedes agregar la lógica para verificar las credenciales
        if (nombre.isEmpty() || contrasena.isEmpty()) {
            mostrarAlerta("Por favor, complete todos los campos.");
        } else {
            // Lógica de autenticación
            // Si las credenciales son correctas, proceder a la siguiente vista
            // Si no, mostrar un mensaje de error
            mostrarAlerta("Iniciando sesión...");
        }
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}