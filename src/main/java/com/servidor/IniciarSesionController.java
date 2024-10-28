package com.servidor;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import com.servidor.modelo.Vendedor;
import com.servidor.util.UtilMarketPlace;
import com.servidor.excepciones.UsuarioNoEncontradoException;

public class IniciarSesionController {

    @FXML
    private TextField cedulaField;

    @FXML
    private PasswordField contrasenaField;

    // Obtención de la instancia única de UtilMarketPlace
    private UtilMarketPlace utilMarketplace = UtilMarketPlace.getInstance();

    @FXML
    private void handleLogin() throws UsuarioNoEncontradoException {
        String cedula = cedulaField.getText();
        String contrasena = contrasenaField.getText();

        if (cedula.isEmpty() || contrasena.isEmpty()) {
            mostrarAlerta("Por favor, complete todos los campos.");
        } else {
            Vendedor vendedor = utilMarketplace.iniciarSesion(cedula, contrasena);

            if (vendedor != null) {
                mostrarAlerta("Sesión iniciada con éxito. Bienvenido, " + vendedor.getNombre() + "!");
                // Aquí puedes cargar la siguiente vista o proceder con la sesión
            } else {
                mostrarAlerta("Contraseña incorrecta. Inténtelo de nuevo.");
            }
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
