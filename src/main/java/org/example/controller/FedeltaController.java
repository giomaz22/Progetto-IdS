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
            outputArea.setText("Codice Fiscale non valido. Deve contenere 16 caratteri.");
            return;
        }

        try {
            FedeltaResponse response = client.fedeltaUtente(cf);

            if (response.getSuccesso()) {
                outputArea.setText("""
                Iscrizione avvenuta con successo!
                Carta Fedeltà: %s
                Punti iniziali: %d
                """.formatted(
                        response.getDettagliCarta().getIDcarta(),
                        response.getDettagliCarta().getPuntiFed()
                ));
            } else if (response.getDettagliCarta().getIDcarta() != null && !response.getDettagliCarta().getIDcarta().isEmpty()) {
                outputArea.setText("""
                Utente già iscritto al programma fedeltà.
                Carta esistente: %s
                Punti accumulati: %d
                """.formatted(
                        response.getDettagliCarta().getIDcarta(),
                        response.getDettagliCarta().getPuntiFed()
                ));
            } else {
                outputArea.setText("[ERRORE] " + response.getMessaggioConfermaFedelta());
            }
        } catch (Exception e) {
            outputArea.setText("[ERRORE] " + e.getMessage());
        }
    }

}
