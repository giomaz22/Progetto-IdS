package org.example.persistence;

import org.example.model.Biglietto;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class gestisciBigliettoDB {

    public void addBiglietto(Biglietto b) {
        String sql = "INSERT INTO biglietti (codBiglietto, idViaggio, classe, posto, PNR, cf, numCarrozza) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = DataBconnect.getConnection().prepareStatement(sql)) {

            stmt.setString(1, b.getCodBiglietto());
            stmt.setInt(2, b.getIdViaggio());
            stmt.setString(3, b.getClasse());
            stmt.setInt(4, b.getPosto());
            stmt.setString(5, b.getPNR());
            stmt.setString(6, b.getCf());
            stmt.setInt(7, b.getNumCarrozza());

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
                biglietto.setCodBiglietto(rs.getString("codBiglietto"));
                biglietto.setIdViaggio(rs.getInt("idViaggio"));
                biglietto.setClasse(rs.getString("Classe"));
                biglietto.setPosto(rs.getInt("posto"));
                biglietto.setPNR(rs.getString("PNR"));
                biglietto.setCf(rs.getString("cf"));
                biglietto.setNumCarrozza(rs.getInt("numCarrozza"));

                risultato.add(biglietto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return risultato;
    }

}
