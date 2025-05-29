package org.example.persistence.dao;

import org.example.model.Promozione;
import org.example.persistence.DBConnectionSingleton;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PromozioneDAO {
    public void addPromozione(Promozione p, Connection conn) throws SQLException {
        String sql = "INSERT INTO promozioni (CodicePromozione, PercentualeSconto, soloFedelta, tipoTreno, inizioPromo, finePromo) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, p.getCodicePromozione());
            stmt.setInt(2, p.getPercentualeSconto());
            stmt.setBoolean(3, p.isSoloFedelta());
            stmt.setString(4, p.getTipoTreno());
            stmt.setString(5, p.getInizioPromo());
            stmt.setString(6, p.getFinePromo());
            stmt.executeUpdate();
        }
    }

    public List<Promozione> promozioniAttive(String tipoTreno, boolean isFedelta, String dataViaggio) {
        List<Promozione> lista = new ArrayList<>();
        String sql = "SELECT * FROM promozioni WHERE " +
                "(tipoTreno = ? OR tipoTreno IS NULL) AND " +
                "(? BETWEEN inizioPromo AND finePromo) AND " +
                "(soloFedelta = FALSE OR soloFedelta = ?)";
        try (PreparedStatement stmt = DBConnectionSingleton.getConnection().prepareStatement(sql)) {
            stmt.setString(1, tipoTreno);
            stmt.setString(2, dataViaggio);
            stmt.setBoolean(3, isFedelta);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Promozione p = new Promozione();
                p.setCodicePromozione(rs.getString("CodicePromozione"));
                p.setPercentualeSconto(rs.getInt("PercentualeSconto"));
                p.setSoloFedelta(rs.getBoolean("soloFedelta"));
                p.setTipoTreno(rs.getString("tipoTreno"));
                p.setInizioPromo(rs.getString("inizioPromo"));
                p.setFinePromo(rs.getString("finePromo"));
                lista.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
}
