package org.example.persistence;

import org.example.model.Fedelta;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class gestisciFedeltaDB {
    public void addCFedeltaPerCliente(Fedelta f) {
        String sql = "INSERT INTO fedelta (id, cf, punti) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = DataBconnect.getConnection().prepareStatement(sql)) {
            stmt.setString(1, f.getID());
            stmt.setString(2, f.getCFpossessore());
            stmt.setInt(3, f.getPuntiCarta());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
