package org.example.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class DashboardController {

    private String cfUtente;

    public void setUtenteCf(String cfUtente) {
        this.cfUtente = cfUtente;
    }

    @FXML
    private Button logoutButton;

    @FXML
    private void handleCerca() {
        caricaVista("/fxml/ricercaViaggi.fxml", "Cerca Viaggi");
    }

    @FXML
    private void handlePrenota() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ricercaViaggi.fxml"));
        try {
            Parent root = loader.load();
            RicercaViaggiController controller = loader.getController();
            controller.setModalitaAcquisto(false); // modalità prenotazione
            controller.setUtenteCf(cfUtente); // imposta il CF utente loggato
            Stage stage = new Stage();
            stage.setTitle("Prenota Viaggio");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAcquista() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ricercaViaggi.fxml"));
        try {
            Parent root = loader.load();
            RicercaViaggiController controller = loader.getController();
            controller.setModalitaAcquisto(true); // modalità acquisto
            controller.setUtenteCf(cfUtente); // imposta il CF utente loggato
            Stage stage = new Stage();
            stage.setTitle("Acquista Biglietto");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleFedelta() {
        caricaVista("/fxml/fedelta.fxml", "Sottoscrizione Fedeltà");
    }

    @FXML
    private void handlePromo() {
        caricaVista("/fxml/promozioni.fxml", "Promozioni Attive");
    }

    @FXML
    private void handleModifica() {
        caricaVista("/fxml/modificaBiglietto.fxml", "Modifica Biglietto");
    }

    @FXML
    private void handleNotifiche() {
        caricaVista("/fxml/notificaFedelta.fxml", "Notifiche Fedeltà");
    }

    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/home.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Trenical - Home");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void caricaVista(String path, String titolo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle(titolo);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
