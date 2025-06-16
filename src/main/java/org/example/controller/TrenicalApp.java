package org.example.controller;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class TrenicalApp extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) primaryStage.getScene().getWindow();
        primaryStage.setTitle("TreniCal - Login");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

}
