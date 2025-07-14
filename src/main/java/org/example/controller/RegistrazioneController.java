package org.example.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;


import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.client.TrenicalClient;
import org.example.grpc.RegistraResponse;
import org.example.grpc.UtenteDTO;


public class RegistrazioneController {

    @FXML
    private TextField nomeField;
    @FXML private TextField cognomeField;
    @FXML private TextField cfField;
    @FXML private DatePicker dataNascitaPicker;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private SplitMenuButton adminMenu;
    @FXML private Button confermaButton;
    @FXML private Button tornaLoginButton;
    @FXML private Label messageLabel;


    private boolean isAdmin = false;
    private TrenicalClient client;

    @FXML
    public void initialize() {
        client = new TrenicalClient("localhost", 50051);
    }

    @FXML
    private void handleConferma() {
        LocalDate selectedDate = dataNascitaPicker.getValue();

        if (selectedDate == null) {
            showAlert(Alert.AlertType.WARNING, "Data di nascita mancante", "Seleziona una data.");
            return;
        }

        String cf = cfField.getText();
        if (cf.length() != 16) {
            showAlert(Alert.AlertType.ERROR, "Codice Fiscale", "Il codice fiscale deve avere 16 caratteri.");
            return;
        }

        if (nomeField.getText().isBlank() ||
                cognomeField.getText().isBlank() ||
                cfField.getText().isBlank() ||
                emailField.getText().isBlank() ||
                passwordField.getText().isBlank()) {

            showAlert(Alert.AlertType.WARNING, "Campi mancanti", "I campi sono obbligatori.");
            return;
        }

        String dataNascitaString = selectedDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        try {
            UtenteDTO utente = UtenteDTO.newBuilder()
                    .setNome(nomeField.getText())
                    .setCognome(cognomeField.getText())
                    .setCodiceFiscale(cf)
                    .setDataDiNascita(dataNascitaString)
                    .setEmail(emailField.getText())
                    .setPassword(passwordField.getText())
                    .setAdmin(isAdmin)
                    .build();

            RegistraResponse response = client.utenteRegistrami(utente);

            if (response.getSuccesso()) {
                showAlert(Alert.AlertType.INFORMATION, "Registrazione avvenuta", response.getMessage());
                resetCampi();
            } else {
                showAlert(Alert.AlertType.ERROR, "Registrazione fallita", response.getMessage());
            }

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Errore", "Errore interno: " + Arrays.toString(e.getStackTrace()));
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void resetCampi() {
        nomeField.clear();
        cognomeField.clear();
        cfField.clear();
        emailField.clear();
        passwordField.clear();
        dataNascitaPicker.setValue(null);
        adminMenu.setText("isAdmin");
        isAdmin = false;
    }


    @FXML
    private void setAdminTrue() {
        isAdmin = true;
        adminMenu.setText("TRUE");
    }

    @FXML
    private void setAdminFalse() {
        isAdmin = false;
        adminMenu.setText("FALSE");
    }

    @FXML
    private void handleTornaLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) tornaLoginButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Login");
        } catch (Exception e) {
            messageLabel.setText("Impossibile tornare al login: " + e.getMessage());
        }
    }
}