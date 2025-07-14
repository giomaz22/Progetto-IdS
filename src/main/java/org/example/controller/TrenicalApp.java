package org.example.controller;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import javafx.stage.Stage;

public class TrenicalApp extends Application {

    /*
    Per far partire l'applicazione è necessario avviare prima la classe TrenicalServer
    e poi con il comando maven clean javafx:run l'applicazione.
     */


    @Override
    public void start(Stage primaryStage) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/home.fxml"));

            primaryStage.setTitle("Trenical - Home");
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
