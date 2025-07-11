package org.example.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import lombok.Setter;
import org.example.client.TrenicalClient;
import org.example.grpc.*;

import java.time.LocalDate;
import java.util.List;

public class RicercaViaggiController {

    @FXML private TextField stazPartenzaField;
    @FXML private TextField stazArrivoField;
    @FXML private TextField tipoTrenoField;
    @FXML private DatePicker dataPicker;
    @FXML private TableView<ViaggioDTO> viaggiTable;

    @FXML private TableColumn<ViaggioDTO, String> colId;
    @FXML private TableColumn<ViaggioDTO, String> colPartenza;
    @FXML private TableColumn<ViaggioDTO, String> colArrivo;
    @FXML private TableColumn<ViaggioDTO, String> colData;
    @FXML private TableColumn<ViaggioDTO, String> colOraPart;
    @FXML private TableColumn<ViaggioDTO, String> colOraArr;
    @FXML private TableColumn<ViaggioDTO, String> colPrezzo;
    @FXML private TableColumn<ViaggioDTO, String> colClasse;
    @FXML private TableColumn<ViaggioDTO, Void> colAzioni;

    private TrenicalClient client;

    @FXML
    public void initialize() {
        client = new TrenicalClient("localhost", 50051);
        setupTabella();
    }

    private void setupTabella() {
        colId.setCellValueFactory(v -> new SimpleStringProperty(String.valueOf(v.getValue().getIdViaggio())));
        colPartenza.setCellValueFactory(v -> new SimpleStringProperty(v.getValue().getStazionePartenza()));
        colArrivo.setCellValueFactory(v -> new SimpleStringProperty(v.getValue().getStazioneArrivo()));
        colData.setCellValueFactory(v -> new SimpleStringProperty(v.getValue().getData()));
        colOraPart.setCellValueFactory(v -> new SimpleStringProperty(v.getValue().getOraPartenza()));
        colOraArr.setCellValueFactory(v -> new SimpleStringProperty(v.getValue().getOraArrivo()));
        colPrezzo.setCellValueFactory(v -> new SimpleStringProperty(String.format("%.2f €", v.getValue().getPrezzo())));
        colClasse.setCellValueFactory(v -> new SimpleStringProperty(String.join(", ", v.getValue().getClassiDisponibili())));

        colAzioni.setCellFactory(col -> new TableCell<>() {
            private final Button azioneButton = new Button("Azione");

            {
                azioneButton.setOnAction(e -> {
                    ViaggioDTO viaggio = getTableView().getItems().get(getIndex());
                    if (modalitaAcquisto) {
                        mostraDialogAcquisto(viaggio);
                    } else {
                        mostraDialogPrenotazione(viaggio);
                    }
                });

            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(azioneButton);
                }
            }
        });

    }

    @Setter
    private String utenteCf;

    @Setter
    private boolean modalitaAcquisto = false;

    @FXML
    private void handleCerca() {
        String partenza = stazPartenzaField.getText().trim();
        String arrivo = stazArrivoField.getText().trim();
        String tipoTreno = tipoTrenoField.getText().trim();
        LocalDate data = dataPicker.getValue();

        if (partenza.isEmpty() || arrivo.isEmpty() || data == null) {
            showAlert(Alert.AlertType.WARNING, "Compila i campi richiesti.");
            return;
        }

        try {
            CercaViaggiResponse response = client.utenteCercaViaggi(
                    data.toString(), tipoTreno, partenza, arrivo
            );

            List<ViaggioDTO> viaggi = response.getViaggiList();
            viaggiTable.setItems(FXCollections.observableArrayList(viaggi));

            if (viaggi.isEmpty()) {
                showAlert(Alert.AlertType.INFORMATION, "Nessun viaggio trovato.");
            }

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Errore nella ricerca: " + e.getMessage());
        }
    }

    private void mostraDialogPrenotazione(ViaggioDTO viaggio) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Prenotazione");
        dialog.setHeaderText("Inserisci il codice fiscale");
        dialog.setContentText("Codice Fiscale:");

        dialog.showAndWait().ifPresent(cf -> {
            PrenotaResponse response = client.prenotaViaggioUtente(viaggio.getIdViaggio(), cf);
            showAlert(response.getSuccesso() ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR, response.getMessaggio());
        });
    }

    private void mostraDialogAcquisto(ViaggioDTO viaggio) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/acquistaBiglietto.fxml"));
            DialogPane pane = loader.load();
            AcquistaBigliettoController controller = loader.getController();
            controller.setDatiViaggio(viaggio, utenteCf);


            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Acquisto Biglietto");
            dialog.setDialogPane(pane);

            dialog.showAndWait().ifPresent(result -> {
                if (result == ButtonType.OK && controller.validate()) {
                    String cf = controller.getCodiceFiscale();
                    String carta = controller.getNumeroCarta();
                    String pnr = controller.getPnr();

                    AcquistaBigliettoResponse response = client.acquistaBigliettoUtente(
                            viaggio.getIdViaggio(), cf, carta, pnr
                    );

                    showAlert(response.getSuccesso() ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR,
                            response.getMessaggioConferma());
                    if (controller.isMonitoraggioRichiesto()) {
                        StatusViaggioTResponse stato = client.statusAttualeViaggio(viaggio.getIdViaggio());
                        StatoTrenoController.mostraConMonitoraggio(stato.getIdTreno(), stato.getMessage());
                    }

                }
            });

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Errore nel caricamento della finestra: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}