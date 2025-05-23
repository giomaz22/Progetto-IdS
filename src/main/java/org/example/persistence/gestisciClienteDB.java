package org.example.persistence;

import org.example.model.Utente;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class gestisciClienteDB {
    public void addCliente(Utente utente) {
        String sql = "INSERT INTO utenti (cf, nome, cognome, dataNascita, IDfedelta) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = DataBconnect.getConnection().prepareStatement(sql)) {
            stmt.setString(1, utente.getCodiceFiscale());
            stmt.setString(2, utente.getNome());
            stmt.setString(3, utente.getCognome());
            stmt.setString(4, utente.getDataNascita());
            stmt.setString(5, utente.getFedelta()); //devo fare controllo se è null?
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Utente trovaPerCF(String cf) {
        String sql = "SELECT * FROM utenti WHERE cf = ?";
        try (PreparedStatement stmt = DataBconnect.getConnection().prepareStatement(sql)) {
            stmt.setString(1, cf);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String nome = rs.getString("nome");
                String cognome = rs.getString("cognome");
                String codiceFiscale = rs.getString("cf");
                String dataNascita = rs.getString("dataNascita");
                String fedelta = rs.getString("IDfedelta");

                Utente u = new Utente();
                u.setCodiceFiscale(cf);
                u.setNome(nome);
                u.setCognome(cognome);
                u.setDataNascita(dataNascita);
                u.setFedelta(fedelta);
                return u;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
