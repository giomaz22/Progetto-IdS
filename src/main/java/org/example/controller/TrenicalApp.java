package org.example.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class TrenicalApp  {
    @FXML
    private void handleLogin() {
        caricaVista("/fxml/login.fxml", "Login");
    }

    @FXML
    private void handleRegister() {
        caricaVista("/fxml/registrati.fxml", "Registrazione");
    }

    @FXML
    private void handleStatoTreno() {
        caricaVista("/fxml/statoTreno.fxml", "Andamento Treno");
    }

    @FXML
    private void handleCercaViaggi() {
        caricaVista("/fxml/ricercaViaggi.fxml", "Ricerca Viaggi");
    }


    @FXML
    private void handleExit() {
        System.exit(0);
    }

    private void caricaVista(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
