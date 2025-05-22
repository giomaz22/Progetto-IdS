package org.example.persistence;

import org.example.model.Biglietto;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class gestisciBigliettoDB {

    public void addBiglietto(Biglietto biglietto) {
        String sql = "INSERT INTO biglietti (partenza, destinazione, treno, classe, posto, PNR, nomeU, cognU, cf, dataPart, dataArr, numCarrozza, idFedelta, prezzo, codBiglietto) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = DataBconnect.getConnection().prepareStatement(sql)) {

            stmt.setString(1, biglietto.getStazionePartenza()); // partenza, es. PAOLA
            stmt.setString(2, biglietto.getStazioneArrivo()); // destinazione, es. SALERNO
            stmt.setString(3, biglietto.getID_treno()); // treno, es. IC 755
            stmt.setString(4, biglietto.getClasse()); // classe, es. BUSINESS
            stmt.setString(5, biglietto.getPosto()); // numero posto, es. 6A
            stmt.setString(6, biglietto.getPNR()); // numero prenotazione, es AW887
            stmt.setString(7, biglietto.getNomeUtente()); // nome utente, es. GIOVANNI
            stmt.setString(8, biglietto.getCognomeUtente()); // cognome utente, es. MAZZEI
            stmt.setString(9, biglietto.getCF()); // CF, es. MZZGNN03S22D122I
            stmt.setString(10, biglietto.getDataPartenza()); // data partenza, es. 08-05-2025
            stmt.setString(11, biglietto.getDataArrivo()); // data arrivo, es. 09-08-2025
            stmt.setInt(12, biglietto.getCarrozza()); // numero carrozza, es. 5
            stmt.setInt(13, biglietto.getIDfedelta()); // numero ID fedelta, es. 22112003
            stmt.setDouble(14, biglietto.getPrezzo()); // prezzo, es. 20
            stmt.setString(15, biglietto.getIDbiglietto()); // id biglietto, RFI202501

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Biglietto> trovaBigliettiPerUtente(String cf) {
        List<Biglietto> risultato = new ArrayList<>();
        String sql = "SELECT * FROM biglietti WHERE cf = ?";

        try (PreparedStatement stmt = DataBconnect.getConnection().prepareStatement(sql)) {
            stmt.setString(1, cf);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Biglietto biglietto = new Biglietto();
                biglietto.setStazionePartenza(rs.getString("partenza"));
                biglietto.setStazioneArrivo(rs.getString("destinazione"));
                biglietto.setID_treno(rs.getString("treno"));
                biglietto.setClasse(rs.getString("classe"));
                biglietto.setPosto(rs.getString("posto"));
                biglietto.setPNR(rs.getString("PNR"));
                biglietto.setNomeUtente(rs.getString("nomeUtente"));
                biglietto.setCognomeUtente(rs.getString("cognomeUtente"));
                biglietto.setCF(rs.getString("CF"));
                biglietto.setDataPartenza(rs.getString("dataPartenza"));
                biglietto.setDataArrivo(rs.getString("dataArrivo"));
                biglietto.setCarrozza(rs.getInt("carrozza"));
                biglietto.setIDfedelta(rs.getInt("idFedelta"));
                biglietto.setPrezzo(rs.getDouble("prezzo"));

                risultato.add(biglietto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
