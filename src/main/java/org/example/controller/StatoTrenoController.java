package org.example.controller;

import io.grpc.stub.StreamObserver;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.client.TrenicalClient;
import org.example.grpc.IscrivimiTrenoSpeResponse;
import org.example.grpc.StatusViaggioTResponse;

public class StatoTrenoController {

    @FXML
    private TextField viaggioIdField;
    @FXML private TextArea statoOutput;

    private TrenicalClient client;

    @FXML
    public void initialize() {
        client = new TrenicalClient("localhost", 50051);
        statoOutput.setEditable(false);
    }

    @FXML
    private void handleControllaStato() {
        String idText = viaggioIdField.getText().trim();

        if (!idText.matches("\\d+")) {
            showError("Inserisci un ID numerico valido");
            return;
        }

        int idViaggio = Integer.parseInt(idText);

        // 1. Chiamata sincrona: stato attuale
        try {
            StatusViaggioTResponse stato = client.statusAttualeViaggio(idViaggio);
            appendOutput("Stato attuale del treno: " + stato.getMessage());

            // 2. Iscrizione agli aggiornamenti
            client.getNotifier().andamentoTreno(stato.getIdTreno(), new StreamObserver<>() {
                @Override
                public void onNext(IscrivimiTrenoSpeResponse value) {
                    Platform.runLater(() -> appendOutput("[UPDATE] " + value.getMessaggio()));
                }

                @Override
                public void onError(Throwable t) {
                    Platform.runLater(() -> appendOutput("[ERRORE]: " + t.getMessage()));
                }

                @Override
                public void onCompleted() {
                    Platform.runLater(() -> appendOutput("Il treno ha terminato la corsa."));
                }
            });

        } catch (Exception e) {
            showError("Errore: " + e.getMessage());
        }
    }

    public void avviaMonitoraggio(String idTreno, String statoIniziale) {
        statoOutput.clear();
        appendOutput("Stato attuale del treno: " + statoIniziale);
        client.getNotifier().andamentoTreno(idTreno, new StreamObserver<>() {
            public void onNext(IscrivimiTrenoSpeResponse value) {
                Platform.runLater(() -> appendOutput("[UPDATE] " + value.getMessaggio()));
            }
            public void onError(Throwable t) {
                Platform.runLater(() -> appendOutput("[ERRORE]: " + t.getMessage()));
            }
            public void onCompleted() {
                Platform.runLater(() -> appendOutput("Treno terminato."));
            }
        });
    }


    public static void mostraConMonitoraggio(String idTreno, String statoIniziale) {
        try {
            FXMLLoader loader = new FXMLLoader(StatoTrenoController.class.getResource("/fxml/stato_treno.fxml"));
            Parent root = loader.load();

            StatoTrenoController controller = loader.getController();
            controller.avviaMonitoraggio(idTreno, statoIniziale);

            Stage stage = new Stage();
            stage.setTitle("Monitoraggio Treno " + idTreno);
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void appendOutput(String msg) {
        statoOutput.appendText(msg + "\n");
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
