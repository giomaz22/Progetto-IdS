package org.example.persistence;


import org.example.model.Prenotazione;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class gestisciPrenotazioneDB {
    public void addPrenotazione(Prenotazione p) {
        String sql = "INSERT INTO prenotazioni (PNR, cf, idViaggio, dataScadenza, posto, numCarrozza) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = DataBconnect.getConnection().prepareStatement(sql)) {

            stmt.setString(1, p.getPNR());
            stmt.setString(2, p.getCf());
            stmt.setInt(3, p.getId_viaggio());
            stmt.setString(4, p.getDataScadenza());
            stmt.setInt(5, p.getPosto());
            stmt.setInt(6, p.getNumCarrozza());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Prenotazione> trovaPrenotazioniPerUtente(String cf) {
        List<Prenotazione> risultato = new ArrayList<>();
        String sql = "SELECT * FROM prenotazioni WHERE cf = ?";

        try (PreparedStatement stmt = DataBconnect.getConnection().prepareStatement(sql)) {
            stmt.setString(1, cf);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Prenotazione prenotazione = new Prenotazione();
                prenotazione.setPNR(rs.getString("PNR"));
                prenotazione.setCf(rs.getString("cf"));
                prenotazione.setId_viaggio(rs.getInt("idViaggio"));
                prenotazione.setPosto(rs.getInt("posto"));
                prenotazione.setDataScadenza(rs.getString("dataScadenza"));
                prenotazione.setNumCarrozza(rs.getInt("numCarrozza"));

                risultato.add(prenotazione);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return risultato;
    }

}
