package com.servidor;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import com.servidor.modelo.MarketPlace;
import com.servidor.util.UtilMarketPlace;

public class Main1 extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Inicializar utilMarketplace
            UtilMarketPlace utilMarketPlace = UtilMarketPlace.getInstance();

            
            MarketPlace marketPlace = new MarketPlace(utilMarketPlace); 

            
            utilMarketPlace.setMarketPlace(marketPlace);

            // carga el archivo XML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("IniciarSesion.fxml"));
            AnchorPane root = loader.load();

            // Crea la escena
            Scene scene = new Scene(root, 300, 200);
            primaryStage.setTitle("Inicio de Sesión");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
// ayuda