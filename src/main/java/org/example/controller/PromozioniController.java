package org.example.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.client.TrenicalClient;
import org.example.grpc.PromoAttiveResponse;

import java.time.format.DateTimeFormatter;

public class PromozioniController {

    @FXML
    private TextField tipoTrenoField;
    @FXML
    private DatePicker dataPicker;
    @FXML
    private CheckBox fedeltaCheckBox;
    @FXML
    private TextArea outputArea;
    @FXML
    private Button chiudiButton;

    private TrenicalClient client;

    @FXML
    public void initialize() {
        client = new TrenicalClient("localhost", 50051);
        chiudiButton.setDisable(true);
    }

    @FXML
    private void handleChiudi() {
        Stage stage = (Stage) chiudiButton.getScene().getWindow();
        stage.close();
    }
    @FXML
    private void handleCercaPromozione() {
        String tipoTreno = tipoTrenoField.getText().trim();
        var data = dataPicker.getValue();
        boolean haiFedelta = fedeltaCheckBox.isSelected();

        if (tipoTreno.isEmpty() || data == null) {
            outputArea.setText("Inserisci tipo treno e data di partenza.");
            return;
        }

        String dataStr = data.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        try {
            PromoAttiveResponse response = client.richiediPromoAttive(haiFedelta, tipoTreno, dataStr);

            outputArea.appendText("Codice promozione: " + response.getCodPromoAttiva());

            chiudiButton.setDisable(false);

        } catch (Exception e) {
            outputArea.setText("[ERRORE] " + e.getMessage());
            chiudiButton.setDisable(false);
        }
    }
}
