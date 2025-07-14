package org.example.persistence.dao;

import org.example.model.Promozione;
import org.example.model.Treno;
import org.example.persistence.DBConnectionSingleton;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class PromozioneDAO {

    // Metodo che aggiunge una nuova promozione al DB
    public void addPromozione(Promozione p, Connection conn) throws SQLException {
        String sql = "INSERT INTO promozioni (CodicePromozione, PercentualeSconto, soloFedelta, tipoTreno, inizioPromo, finePromo) VALUES (?, ?, ?, ?, ?, ?)";
        LocalDate localDateInizio = LocalDate.parse(p.getInizioPromo());
        LocalDate localDateFine = LocalDate.parse(p.getFinePromo());

        java.sql.Date dataInizioOk = java.sql.Date.valueOf(localDateInizio);
        java.sql.Date dataFineOk = java.sql.Date.valueOf(localDateFine);
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, p.getCodicePromozione());
            stmt.setInt(2, p.getPercentualeSconto());
            stmt.setBoolean(3, p.isSoloFedelta());
            stmt.setString(4, p.getTipoTreno());
            stmt.setDate(5, dataInizioOk);
            stmt.setDate(6, dataFineOk);
            stmt.executeUpdate();
        }
    }

    // Metodo che restituisce una lista di promozioni attive nel DB
    public List<Promozione> promozioniAttive(String tipoTreno, boolean isFedelta, String dataViaggio) {
        List<Promozione> lista = new ArrayList<>();

        java.sql.Date dataViaggioOk = null;

        // Solo se dataViaggio è presente e valida
        if (dataViaggio != null && !dataViaggio.isBlank()) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                LocalDate localDate = LocalDate.parse(dataViaggio, formatter);
                dataViaggioOk = java.sql.Date.valueOf(localDate);
            } catch (Exception e) {
                System.err.println("Formato dataViaggio non valido: " + dataViaggio);

                return lista;
            }
        } else {

            return lista;
        }

        String sql = "SELECT * FROM promozioni WHERE " +
                "(tipoTreno = ? OR tipoTreno IS NULL) AND " +
                "(? BETWEEN inizioPromo AND finePromo) AND " +
                "(soloFedelta = FALSE OR soloFedelta = ?)";

        try (PreparedStatement stmt = DBConnectionSingleton.getConnection().prepareStatement(sql)) {
            stmt.setString(1, tipoTreno);
            stmt.setDate(2, dataViaggioOk);  // usa java.sql.Date
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

    // Metodo che elimina una promozione presente nel DB dato un codice specifico
    public void eliminaPromozione(String codicePromo) {
        String sql = "DELETE FROM promozioni WHERE CodicePromozione = ?";
        try (PreparedStatement stmt = DBConnectionSingleton.getConnection().prepareStatement(sql)) {
            stmt.setString(1, codicePromo);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Metodo che restituisce tutte le promozioni presenti nel DB
    public List<Promozione> tuttiLePromozioni(){
        List<Promozione> risultato = new ArrayList<>();
        String sql = "SELECT * FROM promozioni";

        try(PreparedStatement stmt = DBConnectionSingleton.getConnection().prepareStatement(sql)){
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Promozione p = new Promozione();
                p.setCodicePromozione(rs.getString("CodicePromozione"));
                p.setPercentualeSconto(rs.getInt("PercentualeSconto"));
                p.setSoloFedelta(rs.getBoolean("soloFedelta"));
                p.setTipoTreno(rs.getString("tipoTreno"));
                p.setInizioPromo(rs.getString("inizioPromo"));
                p.setFinePromo(rs.getString("finePromo"));
                risultato.add(p);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return risultato;
    }

}
