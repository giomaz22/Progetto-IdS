package org.example.controller;

import javafx.beans.binding.BooleanBinding;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.client.TrenicalClient;
import org.example.grpc.BigliettoDTO;
import org.example.grpc.ModificaBigliettoResponse;

import java.time.format.DateTimeFormatter;

public class ModificaBigliettoController {
    @FXML
    private DatePicker dataPicker;
    @FXML private TextField oraField;
    @FXML private ComboBox<String> classeComboBox;
    @FXML private Button modificaButton;
    @FXML private TextArea outputArea;
    @FXML private Button chiudiButton;

    private TrenicalClient client;
    private BigliettoDTO bigliettoDaModificare;

    @FXML
    public void initialize() {
        client = new TrenicalClient("localhost", 50051);
        classeComboBox.getItems().addAll("PRIMA", "SECONDA", "BUSINESS");

        // Disabilita modificaButton fino a compilazione campi
        BooleanBinding formValido = dataPicker.valueProperty().isNotNull()
                .and(oraField.textProperty().isNotEmpty())
                .and(classeComboBox.valueProperty().isNotNull());

        modificaButton.disableProperty().bind(formValido.not());
    }

    public void setBigliettoDaModificare(BigliettoDTO b) {
        this.bigliettoDaModificare = b;
    }

    @FXML
    private void handleModifica() {
        String nuovaData = dataPicker.getValue().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        String nuovaOra = oraField.getText().trim();
        String nuovaClasse = classeComboBox.getValue();

        try {
            ModificaBigliettoResponse res = client.modificaBigliettoAcq(
                    bigliettoDaModificare, nuovaData, nuovaOra, nuovaClasse);

            outputArea.setText((res.getSuccesso() ? "[OK] " : "[NO] ") + res.getMessaggioModificaEffettuata());
        } catch (Exception e) {
            outputArea.setText("[ERRORE] " + e.getMessage());
        }
    }

    @FXML
    private void handleChiudi() {
        Stage stage = (Stage) chiudiButton.getScene().getWindow();
        stage.close();
    }
}
