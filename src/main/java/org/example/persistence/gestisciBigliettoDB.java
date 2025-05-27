package org.example.persistence;

import org.example.model.Biglietto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/*
Questa classe serve per la gestione del Database riferito ai biglietti. Di seguito vengono
implementati metodi utili per:
- aggiunta di un nuovo biglietto;
  -- in questo metodo deve essere garantita la gestione tramite transazione
- ricerca dei biglietti per codice fiscale utente;
- modifica di un biglietto;
- ricerca dei biglietti per codice del biglietto;
- elimina biglietti.
 */

public class gestisciBigliettoDB {

    // Inserimento biglietto con connessione esterna (per transazione)
    public void addBiglietto(Biglietto b, Connection conn) throws SQLException {
        String sql = "INSERT INTO biglietti (codBiglietto, classe, PNR, cf, idViaggio, numCarrozza, posto) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, b.getCodBiglietto());
            stmt.setString(2, b.getClasse());
            stmt.setString(3, b.getPNR());
            stmt.setString(4, b.getCf());
            stmt.setInt(5, b.getIdViaggio());
            stmt.setInt(6, b.getNumCarrozza());
            stmt.setInt(7, b.getPosto());

            stmt.executeUpdate();
        }
    }

    // Metodo di ricerca biglietto (senza transazione)
    public Biglietto trovaBigliettoPerCodice(String codice) {
        String sql = "SELECT * FROM biglietti WHERE codBiglietto = ?";
        try (PreparedStatement stmt = DataBconnect.getConnection().prepareStatement(sql)) {
            stmt.setString(1, codice);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Biglietto b = new Biglietto();
                b.setCodBiglietto(rs.getString("codBiglietto"));
                b.setClasse(rs.getString("classe"));
                b.setPNR(rs.getString("PNR"));
                b.setCf(rs.getString("cf"));
                b.setIdViaggio(rs.getInt("idViaggio"));
                b.setNumCarrozza(rs.getInt("numCarrozza"));
                b.setPosto(rs.getInt("posto"));
                return b;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void aggiornaBiglietto(Biglietto b, Connection conn) throws SQLException {
        String sql = "UPDATE biglietti SET classe = ?, PNR = ?, cf = ?, idViaggio = ?, numCarrozza = ?, posto = ? WHERE codBiglietto = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, b.getClasse());
            stmt.setString(2, b.getPNR());
            stmt.setString(3, b.getCf());
            stmt.setInt(4, b.getIdViaggio());
            stmt.setInt(5, b.getNumCarrozza());
            stmt.setInt(6, b.getPosto());
            stmt.setString(7, b.getCodBiglietto());
            stmt.executeUpdate();
        }
    }

    // Recupera tutti i biglietti di un utente
    public List<Biglietto> bigliettiPerUtente(String cf) {
        List<Biglietto> lista = new ArrayList<>();
        String sql = "SELECT * FROM biglietti WHERE cf = ?";
        try (PreparedStatement stmt = DataBconnect.getConnection().prepareStatement(sql)) {
            stmt.setString(1, cf);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Biglietto b = new Biglietto();
                b.setCodBiglietto(rs.getString("codBiglietto"));
                b.setClasse(rs.getString("classe"));
                b.setPNR(rs.getString("PNR"));
                b.setCf(rs.getString("cf"));
                b.setIdViaggio(rs.getInt("idViaggio"));
                b.setNumCarrozza(rs.getInt("numCarrozza"));
                b.setPosto(rs.getInt("posto"));
                lista.add(b);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
}
