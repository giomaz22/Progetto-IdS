package org.example.persistence.dao;

import org.example.model.Promozione;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PromozioneDAO {
    public void addPromozione(Promozione p, Connection conn) throws SQLException {
        String sql = "INSERT INTO promozioni (CodicePromozione, PercentualeSconto) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, p.getCodicePromozione());
            stmt.setInt(2, p.getPercentualeSconto());
            stmt.executeUpdate();
        }
    }
}
