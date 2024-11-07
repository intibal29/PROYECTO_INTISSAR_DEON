package com.example.proyectoolimpiadas;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("MainView.fxml"));
            BorderPane root = loader.load();
            Scene scene = new Scene(root);
           // scene.getStylesheets().add(getClass().getResource("/css/application.css").toExternalForm());

            primaryStage.setTitle("Gestión de Olimpiadas");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}