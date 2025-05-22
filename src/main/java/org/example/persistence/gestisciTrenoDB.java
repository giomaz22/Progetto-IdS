package org.example.persistence;

import org.example.model.Biglietto;
import org.example.model.Treno;
import org.example.model.Utente;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class gestisciTrenoDB {
    public void addTreno(Treno treno) {
        String sql = "INSERT INTO treni (id, tipoTreno, oraPartenza, oraArrivo, statoTreno, postiDisponibili) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = DataBconnect.getConnection().prepareStatement(sql)) {
            stmt.setString(1, treno.getIDtreno());
            stmt.setString(2, treno.getTipologia());
            stmt.setString(3, treno.getOraPartenza());
            stmt.setString(4, treno.getOraArrivo());
            stmt.setInt(5, treno.getDisponibilitaPosti());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Treno trovaTrenoPerID(String id) {
        String sql = "SELECT * FROM treni WHERE id = ?";
        try (PreparedStatement stmt = DataBconnect.getConnection().prepareStatement(sql)) {
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String ID = rs.getString("id");
                String tipoT = rs.getString("tipoTreno");
                String oraArrivo = rs.getString("oraArrivo");
                String oraPartenza = rs.getString("oraPartenza");
                String stato = rs.getString("statoTreno");
                int postiDisponibili = rs.getInt("postiDisponibili");

                Treno treno = new Treno();
                treno.setIDtreno(ID);
                treno.setTipologia(tipoT);
                treno.setOraPartenza(oraPartenza);
                treno.setOraArrivo(oraArrivo);
                treno.setStatoTreno(stato);
                treno.setDisponibilitaPosti(postiDisponibili);
                return treno;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Treno> tuttiItreni(){
        List<Treno> risultato = new ArrayList<>();
        String sql = "SELECT * FROM treni";

        try(PreparedStatement stmt = DataBconnect.getConnection().prepareStatement(sql)){
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                String ID_1 = rs.getString("id");
                String tipoT_1 = rs.getString("tipoTreno");
                String oraArrivo_1 = rs.getString("oraArrivo");
                String oraPartenza_1 = rs.getString("oraPartenza");
                String stato_1 = rs.getString("statoTreno");
                int postiDisponibili_1 = rs.getInt("postiDisponibili");

                Treno treno = new Treno();

                treno.setIDtreno(ID_1);
                treno.setTipologia(tipoT_1);
                treno.setOraArrivo(oraArrivo_1);
                treno.setOraPartenza(oraPartenza_1);
                treno.setStatoTreno(stato_1);
                treno.setDisponibilitaPosti(postiDisponibili_1);

                risultato.add(treno);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return risultato;
    }
}
