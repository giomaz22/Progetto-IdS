package org.example.persistence;

import org.example.model.Viaggio;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class gestisciViaggioDB {
    public void addNewViaggio(Viaggio v) {
        String sql = "INSERT INTO viaggi (id, idTreno, numPostiDisponibili, oraPartenza, oraArrivo, stazionePartenza, stazioneArrivo, prezzo, classiDisponibili) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = DataBconnect.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, v.getIDViaggio());
            stmt.setString(2, v.getIDtreno());
            stmt.setInt(3, v.getNumPostiDisponibili());
            stmt.setString(4, v.getOraPartenza());
            stmt.setString(5, v.getOraArrivo());
            stmt.setString(6, v.getStazionePartenza());
            stmt.setString(7, v.getStazioneArrivo());
            stmt.setDouble(8, v.getPrezzo());
            stmt.setString(9, v.getClassiDisponibili());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
