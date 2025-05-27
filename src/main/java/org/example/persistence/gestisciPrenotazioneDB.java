package org.example.persistence;


import org.example.model.Prenotazione;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class gestisciPrenotazioneDB {
    public void addPrenotazione(Prenotazione p, Connection conn) throws SQLException {
        String sql = "INSERT INTO prenotazioni (PNR, cf, idViaggio, dataScadenza, posto, numCarrozza) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, p.getPNR());
            stmt.setString(2, p.getCf());
            stmt.setInt(3, p.getId_viaggio());
            stmt.setString(4, p.getDataScadenza());
            stmt.setInt(5, p.getPosto());
            stmt.setInt(6, p.getNumCarrozza());
            stmt.executeUpdate();
        }
    }

    public Prenotazione trovaPerPNR(String pnr) {
        String sql = "SELECT * FROM prenotazioni WHERE PNR = ?";
        try (PreparedStatement stmt = DataBconnect.getConnection().prepareStatement(sql)) {
            stmt.setString(1, pnr);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Prenotazione p = new Prenotazione();
                p.setPNR(rs.getString("PNR"));
                p.setCf(rs.getString("cf"));
                p.setId_viaggio(rs.getInt("idViaggio"));
                p.setDataScadenza(rs.getString("dataScadenza"));
                p.setPosto(rs.getInt("posto"));
                p.setNumCarrozza(rs.getInt("numCarrozza"));
                return p;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void eliminaPrenotazione(String pnr, Connection conn) throws SQLException {
        String sql = "DELETE FROM prenotazioni WHERE PNR = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, pnr);
            stmt.executeUpdate();
        }
    }

    public List<Prenotazione> tutteLePrenotazioni() {
        List<Prenotazione> lista = new ArrayList<>();
        String sql = "SELECT * FROM prenotazioni";
        try (PreparedStatement stmt = DataBconnect.getConnection().prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Prenotazione p = new Prenotazione();
                p.setPNR(rs.getString("PNR"));
                p.setCf(rs.getString("cf"));
                p.setId_viaggio(rs.getInt("idViaggio"));
                p.setDataScadenza(rs.getString("dataScadenza"));
                p.setPosto(rs.getInt("posto"));
                p.setNumCarrozza(rs.getInt("numCarrozza"));
                lista.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

}
