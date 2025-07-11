package org.example.persistence.dao;

import org.example.model.Treno;
import org.example.persistence.DBConnectionSingleton;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TrenoDAO {

    // Metodo che aggiunge un nuovo treno al DB
    public void addTreno(Treno treno) {
        String sql = "INSERT INTO treni (id, tipoTreno, statoTreno, numCarrozze) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = DBConnectionSingleton.getConnection().prepareStatement(sql)) {
            stmt.setString(1, treno.getIDtreno());
            stmt.setString(2, treno.getTipologia());
            stmt.setString(3, treno.getStatoTreno());
            stmt.setInt(4, treno.getNumCarrozze());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Metodo che, dato l'id di un treno, ne aggiorna lo stato
    public void aggiornaStatoTreno(String idTreno, String stato, Connection conn) throws SQLException {
        String sql = "UPDATE treni SET statoTreno = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, stato);
            stmt.setString(2, idTreno);
            stmt.executeUpdate();
        }
    }

    // Metodo che trova un treno dato un id specifico
    public Treno trovaTrenoPerID(String id) {
        String sql = "SELECT * FROM treni WHERE id = ?";
        try (PreparedStatement stmt = DBConnectionSingleton.getConnection().prepareStatement(sql)) {
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String ID = rs.getString("id");
                String tipoT = rs.getString("tipoTreno");
                String stato = rs.getString("statoTreno");
                int numCarr = rs.getInt("numCarrozze");

                Treno treno = new Treno();
                treno.setIDtreno(ID);
                treno.setTipologia(tipoT);
                treno.setStatoTreno(stato);
                treno.setNumCarrozze(numCarr);
                return treno;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Metodo che ritorna tutti i treni interni al DB
    public List<Treno> tuttiItreni(){
        List<Treno> risultato = new ArrayList<>();
        String sql = "SELECT * FROM treni";

        try(PreparedStatement stmt = DBConnectionSingleton.getConnection().prepareStatement(sql)){
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String ID_1 = rs.getString("id");
                String tipoT_1 = rs.getString("tipoTreno");
                String stato_1 = rs.getString("statoTreno");
                int numCarr_1 = rs.getInt("numCarrozze");

                Treno treno = new Treno();

                treno.setIDtreno(ID_1);
                treno.setTipologia(tipoT_1);
                treno.setStatoTreno(stato_1);
                treno.setNumCarrozze(numCarr_1);

                risultato.add(treno);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return risultato;
    }

    // Metodo che elimina un treno dato un id specifico
    public void eliminaTreno(String idTreno) {
        String sql = "DELETE FROM treni WHERE id = ?";
        try (PreparedStatement stmt = DBConnectionSingleton.getConnection().prepareStatement(sql)) {
            stmt.setString(1, idTreno);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
