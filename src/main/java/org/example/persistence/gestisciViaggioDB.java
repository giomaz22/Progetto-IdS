package org.example.persistence;

import org.example.model.Viaggio;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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

    public List<Viaggio> cercaViaggi(String partenza, String arrivo,
                                     String data, String tipoTreno) {
        List<Viaggio> risultati = new ArrayList<>();
        String sql = "SELECT * FROM viaggi v " +
                "JOIN treni t ON v.idTreno = t.id " +
                "WHERE v.stazionePartenza = ? AND v.stazioneArrivo = ? AND v.data = ?";

        if (tipoTreno != null && !tipoTreno.isEmpty()) {
            sql += " AND t.tipoTreno = ?";
        }

        try (PreparedStatement stmt = org.example.persistence.DataBconnect.getConnection().prepareStatement(sql)) {
            stmt.setString(1, partenza);
            stmt.setString(2, arrivo);
            stmt.setDate(3, Date.valueOf(data));
            if (tipoTreno != null && !tipoTreno.isEmpty()) {
                stmt.setString(4, tipoTreno);
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Viaggio viaggio = new Viaggio();
                viaggio.setIDViaggio(rs.getInt("id"));
                viaggio.setIDtreno(rs.getString("idTreno"));
                viaggio.setNumPostiDisponibili(rs.getInt("numPostiDisponibili"));
                viaggio.setOraPartenza(rs.getString("oraPartenza"));
                viaggio.setOraArrivo(rs.getString("oraArrivo"));
                viaggio.setStazionePartenza(rs.getString("stazionePartenza"));
                viaggio.setStazioneArrivo(rs.getString("stazioneArrivo"));
                viaggio.setPrezzo(rs.getDouble("prezzo"));
                viaggio.setClassiDisponibili(rs.getString("classiDisponibili"));

                risultati.add(viaggio);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return risultati;
    }

    public Viaggio trovaViaggioPerID(int id) {
        String sql = "SELECT * FROM viaggi WHERE id = ?";
        try (PreparedStatement stmt = DataBconnect.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int idViaggio = rs.getInt("id");
                String idTreno = rs.getString("idTreno");
                int postiDisponibili = rs.getInt("numPostiDisponibili");
                String oraPartenza = rs.getString("oraPartenza");
                String oraArrivo = rs.getString("oraArrivo");
                String stazionePartenza = rs.getString("stazionePartenza");
                String stazioneArrivo = rs.getString("stazioneArrivo");
                double prezzo = rs.getDouble("prezzo");
                String classiDisponibili = rs.getString("classiDisponibili");


                Viaggio v = new Viaggio();
                v.setIDViaggio(idViaggio);
                v.setIDtreno(idTreno);
                v.setNumPostiDisponibili(postiDisponibili);
                v.setOraPartenza(oraPartenza);
                v.setOraArrivo(oraArrivo);
                v.setStazionePartenza(stazionePartenza);
                v.setStazioneArrivo(stazioneArrivo);
                v.setPrezzo(prezzo);
                v.setClassiDisponibili(classiDisponibili);
                return v;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
}
