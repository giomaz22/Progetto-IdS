package org.example.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.example.client.TrenicalClient;
import org.example.grpc.FedeltaResponse;

public class FedeltaController {

    @FXML
    private TextField cfField;
    @FXML private Button sottoscriviButton;
    @FXML
    private TextArea outputArea;

    private TrenicalClient client;

    public void initialize() {
        client = new TrenicalClient("localhost", 50051);

    }

    @FXML
    private void handleSottoscrizione() {
        String cf = cfField.getText().trim().toUpperCase();

        if (cf.length() != 16) {
            outputArea.setText("Codice Fiscale non valido.");
            return;
        }

        try {
            FedeltaResponse response = client.fedeltaUtente(cf);

            if (response.getSuccesso()) {
                outputArea.setText("Iscrizione avvenuta con successo!\n" +
                        "Carta Fedeltà: " + response.getDettagliCarta().getIDcarta() +
                        "\nPunti: " + response.getDettagliCarta().getPuntiFed());
            } else {
                outputArea.setText("[CARTA] " + response.getMessaggioConfermaFedelta() +
                        "\nCarta: " + response.getDettagliCarta().getIDcarta() +
                        "\nPunti: " + response.getDettagliCarta().getPuntiFed());
            }
        } catch (Exception e) {
            outputArea.setText("[ERRORE] " + e.getMessage());
        }
    }
}
