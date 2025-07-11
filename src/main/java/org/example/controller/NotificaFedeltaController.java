package org.example.controller;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.example.client.TrenicalClient;
import org.example.grpc.AccettiNotificaFedResponse;
import org.example.grpc.FedeltaDTO;

public class NotificaFedeltaController {

    @FXML
    private TextField cfField;
    @FXML private TextField idCartaField;
    @FXML private TextField puntiField;
    @FXML private CheckBox checkNotifica;
    @FXML private TextArea outputArea;

    private TrenicalClient client;

    @FXML
    public void initialize() {
        client = new TrenicalClient("localhost", 50051);
    }

    @FXML
    private void handleNotifica() {
        String cf = cfField.getText().trim();
        String idCarta = idCartaField.getText().trim();
        int punti;

        try {
            punti = Integer.parseInt(puntiField.getText().trim());
        } catch (NumberFormatException e) {
            outputArea.setText("Inserisci un numero valido per i punti.");
            return;
        }

        boolean desideraContatto = checkNotifica.isSelected();

        FedeltaDTO fedelta = FedeltaDTO.newBuilder()
                .setCFpossess(cf)
                .setIDcarta(idCarta)
                .setPuntiFed(punti)
                .build();

        try {
            AccettiNotificaFedResponse response = client.notificaFedelta(fedelta, desideraContatto);
            if (response.hasPromoPerTe()) {
                var promo = response.getPromoPerTe();
                outputArea.setText("[PROMO] " + response.getMessage() +
                        "\nCodice: " + promo.getPromoCode() +
                        "\nSconto: " + promo.getPercentSconto() + "%");
            } else {
                outputArea.setText("[INFO] " + response.getMessage());
            }
        } catch (Exception e) {
            outputArea.setText("[ERRORE] " + e.getMessage());
        }
    }
}
