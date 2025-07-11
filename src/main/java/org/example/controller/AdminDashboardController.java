
package org.example.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.model.Promozione;
import org.example.model.Treno;
import org.example.model.Viaggio;
import org.example.persistence.dao.PromozioneDAO;
import org.example.servizi.PromozioneService;
import org.example.servizi.TrenoService;
import org.example.servizi.ViaggioService;

import java.time.LocalDate;

public class AdminDashboardController {

    @FXML private TableView<Treno> treniTable;
    @FXML private TableColumn<Treno, String> colTrenoId, colTipoTreno, colStatoTreno;
    @FXML private TableColumn<Treno, Integer> colNumCarrozze;
    @FXML private TextField trenoIdField, tipoTrenoField, statoTrenoField;
    @FXML private Spinner<Integer> carrozzeSpinner;
    private final TrenoService gestioneTreni = new TrenoService();
    private final ObservableList<Treno> listaTreni = FXCollections.observableArrayList();


    @FXML private TextField idViaggioField, idTrenoField, postiField, oraPartenzaField, oraArrivoField,
            dataField, stazionePartenzaField, stazioneArrivoField, prezzoField, classiField;
    @FXML private TableView<Viaggio> viaggiTable;
    @FXML private TableColumn<Viaggio, Integer> colIdViaggio;
    @FXML private TableColumn<Viaggio, String> colIdTreno, colData, colPartenza, colArrivo;
    @FXML private TableColumn<Viaggio, Double> colPrezzo;
    private final ViaggioService gestioneViaggio = new ViaggioService();
    private final ObservableList<Viaggio> listaViaggi = FXCollections.observableArrayList();


    @FXML private TextField codicePromoField, scontoField, tipoTrenoPromoField, inizioField, fineField;
    @FXML private CheckBox soloFedeltaCheck;
    @FXML private TableView<Promozione> promoTable;
    @FXML private TableColumn<Promozione, String> colCodPromo, colonTipoTreno, colInizio, colFine;
    @FXML private TableColumn<Promozione, Integer> colSconto;
    @FXML private TableColumn<Promozione, Boolean> colSoloFedelta;
    private final PromozioneDAO promozioneDAO = new PromozioneDAO();
    private final PromozioneService gestisciPromo = new PromozioneService();
    private final ObservableList<Promozione> listaPromozioni = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        carrozzeSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 20, 6));
        colTrenoId.setCellValueFactory(new PropertyValueFactory<>("IDtreno"));
        colTipoTreno.setCellValueFactory(new PropertyValueFactory<>("tipologia"));
        colStatoTreno.setCellValueFactory(new PropertyValueFactory<>("statoTreno"));
        colNumCarrozze.setCellValueFactory(new PropertyValueFactory<>("numCarrozze"));
        listaTreni.setAll(gestioneTreni.getAllTrains());
        treniTable.setItems(listaTreni);
    }

    @FXML
    private void handleAggiungiTreno() {
        String id = trenoIdField.getText().trim();
        String tipo = tipoTrenoField.getText().trim();
        String stato = statoTrenoField.getText().trim();
        int carrozze = carrozzeSpinner.getValue();

        if (id.isEmpty() || tipo.isEmpty() || stato.isEmpty()) {
            showAlert("Tutti i campi devono essere compilati");
            return;
        }

        Treno nuovo = new Treno(id, tipo, stato, carrozze);
        gestioneTreni.addTrain(nuovo);
        listaTreni.setAll(gestioneTreni.getAllTrains());
        clearTrenoForm();
    }

    @FXML
    private void handleEliminaTreno() {
        Treno selected = treniTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        gestioneTreni.removeTrain(selected.getIDtreno());
        listaTreni.setAll(gestioneTreni.getAllTrains());
    }

    private void clearTrenoForm() {
        trenoIdField.clear();
        tipoTrenoField.clear();
        statoTrenoField.clear();
        carrozzeSpinner.getValueFactory().setValue(6);
    }

    @FXML
    private void handleAggiungiViaggio() {
        try {
            Viaggio v = new Viaggio();
            v.setIDViaggio(Integer.parseInt(idViaggioField.getText().trim()));
            v.setIDtreno(idTrenoField.getText().trim());
            v.setNumPostiDisponibili(Integer.parseInt(postiField.getText().trim()));
            v.setOraPartenza(oraPartenzaField.getText().trim());
            v.setOraArrivo(oraArrivoField.getText().trim());
            v.setData(dataField.getText().trim());
            v.setStazionePartenza(stazionePartenzaField.getText().trim());
            v.setStazioneArrivo(stazioneArrivoField.getText().trim());
            v.setPrezzo(Double.parseDouble(prezzoField.getText().trim()));
            v.setClassiDisponibili(classiField.getText().trim());

            gestioneViaggio.addNewViaggio(v);
            listaViaggi.setAll(gestioneViaggio.ricercaViaggi("", "", v.getData(), null));
            clearViaggioForm();
        } catch (Exception e) {
            showAlert("Errore: " + e.getMessage());
        }
    }


    @FXML
    private void handleEliminaViaggio() {
        Viaggio selected = viaggiTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        gestioneViaggio.removeViaggio(selected.getIDViaggio());
        listaViaggi.setAll(gestioneViaggio.ricercaViaggi("", "", LocalDate.now().toString(), null));
    }

    private void clearViaggioForm() {
        idViaggioField.clear(); idTrenoField.clear(); postiField.clear(); oraPartenzaField.clear();
        oraArrivoField.clear(); dataField.clear(); stazionePartenzaField.clear(); stazioneArrivoField.clear();
        prezzoField.clear(); classiField.clear();
    }

    @FXML
    private void handleInserisciPromo() {
        try {
            Promozione promo = new Promozione();
            promo.setCodicePromozione(codicePromoField.getText().trim());
            promo.setPercentualeSconto(Integer.parseInt(scontoField.getText().trim()));
            promo.setTipoTreno(tipoTrenoPromoField.getText().trim());
            promo.setSoloFedelta(soloFedeltaCheck.isSelected());
            promo.setInizioPromo(inizioField.getText().trim());
            promo.setFinePromo(fineField.getText().trim());

            gestisciPromo.addNewPromotion(promo);
            listaPromozioni.setAll(gestisciPromo.getPromozioniPerViaggio("", false, LocalDate.now().toString()));
            clearPromoForm();
        } catch (Exception e) {
            showAlert("Errore: " + e.getMessage());
        }
    }

    @FXML
    private void handleEliminaPromo() {
        Promozione selected = promoTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        gestisciPromo.removePromotion(selected.getCodicePromozione());
        listaPromozioni.setAll(gestisciPromo.getPromozioniPerViaggio("", false, LocalDate.now().toString()));
    }

    private void clearPromoForm() {
        codicePromoField.clear(); scontoField.clear(); tipoTrenoPromoField.clear();
        inizioField.clear(); fineField.clear(); soloFedeltaCheck.setSelected(false);
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
