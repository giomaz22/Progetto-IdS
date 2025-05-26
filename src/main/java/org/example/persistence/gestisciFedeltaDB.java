package org.example.persistence;

import org.example.model.Fedelta;
import org.example.model.Treno;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
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

    public Fedelta trovaCartaPerIDUtente(String cf) {
        String sql = "SELECT * FROM fedelta WHERE cf = ?";
        try (PreparedStatement stmt = DataBconnect.getConnection().prepareStatement(sql)) {
            stmt.setString(1, cf);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String ID = rs.getString("id");
                String codf = rs.getString("cf");
                int punti = rs.getInt("puntiFedelta");

                Fedelta f = new Fedelta();
                f.setID(ID);
                f.setCFpossessore(codf);
                f.setPuntiCarta(punti);
                return f;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


}
