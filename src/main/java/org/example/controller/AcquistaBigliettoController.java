package org.example.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import lombok.Getter;
import org.example.grpc.ViaggioDTO;

public class AcquistaBigliettoController {

    @FXML
    private DialogPane dialogPane;
    @FXML private TextField cfField;
    @FXML private TextField cartaField;
    @FXML private TextField pnrField;
    @FXML private CheckBox prenotazioneCheck;
    @FXML private CheckBox monitorCheck;
    @FXML
    private Label infoViaggioLabel;

    public boolean isMonitoraggioRichiesto() {
        return monitorCheck.isSelected();
    }


    // Variabili accessibili dopo validazione
    @Getter
    private String codiceFiscale;
    @Getter private String numeroCarta;
    @Getter private String pnr;

    private ViaggioDTO viaggioSelezionato;
    private String codiceFiscaleUtente;

    public void setDatiViaggio(ViaggioDTO viaggio, String cfUtente) {
        this.viaggioSelezionato = viaggio;
        this.codiceFiscaleUtente = cfUtente;

        cfField.setText(cfUtente);
        infoViaggioLabel.setText(
                "Partenza: " + viaggio.getStazionePartenza() + "\n" +
                        "Arrivo: " + viaggio.getStazioneArrivo() + "\n" +
                        "Ora: " + viaggio.getOraPartenza() + " ➜ " + viaggio.getOraArrivo() + "\n" +
                        "Prezzo: €" + viaggio.getPrezzo()
        );
    }

    @FXML
    public void togglePNRField() {
        boolean checked = prenotazioneCheck.isSelected();
        pnrField.setDisable(!checked);
        if (!checked) pnrField.clear();
    }

    public boolean validate() {
        codiceFiscale = cfField.getText().trim();
        numeroCarta = cartaField.getText().trim();
        pnr = pnrField.getText().trim().isEmpty() ? null : pnrField.getText().trim();

        if (codiceFiscale.isEmpty() || numeroCarta.isEmpty()) {
            showAlert("I campi obbligatori devono essere compilati.");
            return false;
        }

        if (!numeroCarta.matches("\\d{16}")) {
            showAlert("La carta di credito deve contenere 16 cifre.");
            return false;
        }

        if (prenotazioneCheck.isSelected() && (pnr == null || pnr.isEmpty())) {
            showAlert("Inserisci un PNR valido o deseleziona la casella.");
            return false;
        }

        return true;
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    public DialogPane getDialogPane() {
        return dialogPane;
    }

}