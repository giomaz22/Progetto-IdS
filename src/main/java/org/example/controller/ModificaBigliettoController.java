package org.example.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.client.TrenicalClient;
import org.example.grpc.BigliettoDTO;
import org.example.grpc.ModificaBigliettoResponse;

public class ModificaBigliettoController {

    @FXML private TextField codBigliettoField;
    @FXML private TextField carrozzaField;
    @FXML private TextField postoField;
    @FXML private DatePicker dataPicker;
    @FXML private TextField oraField;
    @FXML private ComboBox<String> classeComboBox;
    @FXML private Button modificaButton;
    @FXML private TextArea outputArea;

    private TrenicalClient client;

    @FXML
    public void initialize() {
        setupComboBox();
        setupListeners();
    }

    // Metodo per iniettare il client gRPC dal chiamante
    public void setClient(TrenicalClient client) {
        this.client = client;
    }

    private void setupComboBox() {
        classeComboBox.getItems().addAll("PRIMA", "SECONDA", "Business");
    }

    private void setupListeners() {
        modificaButton.setDisable(true);

        codBigliettoField.textProperty().addListener((obs, oldV, newV) -> checkForm());
        carrozzaField.textProperty().addListener((obs, oldV, newV) -> checkForm());
        postoField.textProperty().addListener((obs, oldV, newV) -> checkForm());
        dataPicker.valueProperty().addListener((obs, oldV, newV) -> checkForm());
        oraField.textProperty().addListener((obs, oldV, newV) -> checkForm());
        classeComboBox.valueProperty().addListener((obs, oldV, newV) -> checkForm());
    }

    private void checkForm() {
        boolean allFilled = !codBigliettoField.getText().isEmpty()
                && !carrozzaField.getText().isEmpty()
                && !postoField.getText().isEmpty()
                && dataPicker.getValue() != null
                && !oraField.getText().isEmpty()
                && classeComboBox.getValue() != null;

        modificaButton.setDisable(!allFilled);
    }

    @FXML
    private void handleModifica() {
        try {
            String codBiglietto = codBigliettoField.getText().trim();
            int numCarrozza = Integer.parseInt(carrozzaField.getText().trim());
            int posto = Integer.parseInt(postoField.getText().trim());
            String nuovaData = dataPicker.getValue().toString();
            String nuovaOra = oraField.getText().trim();
            String nuovaClasse = classeComboBox.getValue();

            BigliettoDTO biglietto = BigliettoDTO.newBuilder()
                    .setCodBiglietto(codBiglietto)
                    .setNumCarrozza(numCarrozza)
                    .setPostoAssegnato(posto)
                    .build();

            ModificaBigliettoResponse response = client.modificaBigliettoAcq(biglietto, nuovaData, nuovaOra, nuovaClasse);
            outputArea.setText(response.getMessaggioModificaEffettuata());

        } catch (NumberFormatException e) {
            outputArea.setText("Numero carrozza o posto non valido.");
        } catch (Exception e) {
            outputArea.setText("Errore nella modifica del biglietto: " + e.getMessage());
        }
    }

    @FXML
    private void handleChiudi() {
        modificaButton.getScene().getWindow().hide();
    }
}


