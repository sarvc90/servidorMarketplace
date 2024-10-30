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
            // Initialize UtilMarketPlace
            UtilMarketPlace utilMarketPlace = UtilMarketPlace.getInstance();

            // Initialize MarketPlace by passing the UtilMarketPlace instance
            MarketPlace marketPlace = new MarketPlace(utilMarketPlace); // Pass UtilMarketPlace instance to MarketPlace

            // Set MarketPlace instance in UtilMarketPlace
            utilMarketPlace.setMarketPlace(marketPlace);

            // Load the FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("IniciarSesion.fxml"));
            AnchorPane root = loader.load();

            // Create the scene
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