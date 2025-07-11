package org.example.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import org.example.client.TrenicalClient;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;


public class LoginController {
    @FXML
    private TextField emailField;
    @FXML private TextField passwordField;
    @FXML private Button loginButton;
    @FXML private Button registratiButton;
    @FXML private Label infoLabel;

    private TrenicalClient client;

    public void initialize() {
        client = new TrenicalClient("localhost", 50051);

    }

    @FXML
    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();

        try {
            var response = client.loginUtente(email, password);
            boolean isAdmin = response.getLoginUt().getAdmin();

            if (isAdmin) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/adminDashboard.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) loginButton.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Dashboard Amministratore");
                stage.show();
            } else {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/dashboard.fxml"));
                Parent root = loader.load();
                DashboardController controller = loader.getController();
                controller.setUtenteCf(response.getLoginUt().getCodiceFiscale());

                Stage stage = (Stage) loginButton.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Dashboard Utente");
                stage.show();
            }

        } catch (Exception e) {
            infoLabel.setText("Login fallito: " + e.getMessage());
        }
    }


    @FXML
    private void handleRegistrazione() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/registrazione.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) registratiButton.getScene().getWindow();

            stage.setScene(new Scene(root));
            stage.setTitle("Registrazione Utente");
        } catch (Exception e) {
            infoLabel.setText("Errore nel caricamento della schermata di registrazione: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

}
