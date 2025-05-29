package org.example.persistence.dao;

import org.example.model.Utente;
import org.example.persistence.DBConnectionSingleton;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ClienteDAO {
    public void addCliente(Utente utente) {
        String sql = "INSERT INTO utenti (cf, nome, cognome, dataNascita) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = DBConnectionSingleton.getConnection().prepareStatement(sql)) {
            stmt.setString(1, utente.getCodiceFiscale());
            stmt.setString(2, utente.getNome());
            stmt.setString(3, utente.getCognome());
            stmt.setString(4, utente.getDataNascita());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Utente trovaPerCF(String cf) {
        String sql = "SELECT * FROM utenti WHERE cf = ?";
        try (PreparedStatement stmt = DBConnectionSingleton.getConnection().prepareStatement(sql)) {
            stmt.setString(1, cf);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String nome = rs.getString("nome");
                String cognome = rs.getString("cognome");
                String codiceFiscale = rs.getString("cf");
                String dataNascita = rs.getString("dataNascita");

                Utente u = new Utente();
                u.setCodiceFiscale(codiceFiscale);
                u.setNome(nome);
                u.setCognome(cognome);
                u.setDataNascita(dataNascita);
                return u;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
