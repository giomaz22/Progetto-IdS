package org.example.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import lombok.Setter;
import org.example.client.TrenicalClient;
import org.example.grpc.*;
import org.example.servizi.ClienteService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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

                    Alert scelta = new Alert(Alert.AlertType.CONFIRMATION);
                    scelta.setTitle("Seleziona Azione");
                    scelta.setHeaderText("Vuoi acquistare o prenotare questo viaggio?");
                    scelta.setContentText("Scegli un'opzione:");

                    ButtonType bottoneAcquisto = new ButtonType("Acquista");
                    ButtonType bottonePrenota = new ButtonType("Prenota");
                    ButtonType annulla = new ButtonType("Annulla", ButtonBar.ButtonData.CANCEL_CLOSE);

                    scelta.getButtonTypes().setAll(bottoneAcquisto, bottonePrenota, annulla);

                    scelta.showAndWait().ifPresent(risultato -> {
                        if (risultato == bottoneAcquisto) {
                            mostraDialogAcquisto(viaggio);
                        } else if (risultato == bottonePrenota) {
                            mostraDialogPrenotazione(viaggio);
                        }
                    });
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
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String dataFormattata = data.format(formatter);
            CercaViaggiResponse response = client.utenteCercaViaggi(dataFormattata
                    , tipoTreno, partenza, arrivo
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

            // Se la prenotazione fallisce perché l’utente non esiste
            if (!response.getSuccesso() && response.getMessaggio().contains("Utente non presente nel DB")) {
                chiediLoginORegistrazione();
            } else {
                showAlert(response.getSuccesso() ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR, response.getMessaggio());
            }
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

                    if (!response.getSuccesso()) {
                        String messaggio = response.getMessaggioConferma();
                        if (messaggio.contains("Utente non trovato")) {
                            chiediLoginORegistrazione();
                        } else if (messaggio.contains("Carta non valida")) {
                            showAlert(Alert.AlertType.ERROR, "Carta di credito non valida. Riprovare con una carta valida.");
                        } else if (messaggio.contains("Prenotazione già scaduta")) {
                            showAlert(Alert.AlertType.WARNING, "La prenotazione è scaduta. Prenota di nuovo prima di acquistare.");
                        } else {
                            showAlert(Alert.AlertType.ERROR, messaggio);
                        }
                    } else {
                        showAlert(Alert.AlertType.INFORMATION, response.getMessaggioConferma());
                    }

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

    private void chiediLoginORegistrazione() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Utente non registrato");
        alert.setHeaderText("Non risulti registrato nel sistema.");
        alert.setContentText("Vuoi accedere o registrarti?");

        ButtonType loginButton = new ButtonType("Login");
        ButtonType registerButton = new ButtonType("Registrati");
        ButtonType cancelButton = new ButtonType("Annulla", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(loginButton, registerButton, cancelButton);

        alert.showAndWait().ifPresent(response -> {
            if (response == loginButton) {
                tornaAlLogin();
            } else if (response == registerButton) {
                vaiAllaRegistrazione();
            }
        });
    }

    private void tornaAlLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            stazPartenzaField.getScene().setRoot(loader.load());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Errore nel caricamento del login");
        }
    }

    private void vaiAllaRegistrazione() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/registrati.fxml"));
            stazPartenzaField.getScene().setRoot(loader.load());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Errore nel caricamento della registrazione");
        }
    }
}