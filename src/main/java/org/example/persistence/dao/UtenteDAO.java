package org.example.persistence.dao;

import org.example.model.Utente;
import org.example.persistence.DBConnectionSingleton;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UtenteDAO {
    public void addCliente(Utente utente) {
        String sql = "INSERT INTO utenti (cf, nome, cognome, dataNascita, email, password, isAdmin) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = DBConnectionSingleton.getConnection().prepareStatement(sql)) {
            stmt.setString(1, utente.getCodiceFiscale());
            stmt.setString(2, utente.getNome());
            stmt.setString(3, utente.getCognome());
            stmt.setString(4, utente.getDataNascita());
            stmt.setString(5, utente.getEmail());
            stmt.setString(6, utente.getPassword());
            stmt.setBoolean(7, utente.isAmministratore());
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
                String email = rs.getString("email");
                boolean admin = rs.getBoolean("isAdmin");

                Utente u = new Utente();
                u.setCodiceFiscale(codiceFiscale);
                u.setNome(nome);
                u.setCognome(cognome);
                u.setDataNascita(dataNascita);
                u.setEmail(email);
                u.setPassword("");
                u.setAmministratore(admin);
                return u;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Utente trovaPerEmail(String email) {
        String sql = "SELECT * FROM utenti WHERE email = ?";
        try (PreparedStatement stmt = DBConnectionSingleton.getConnection().prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String nome = rs.getString("nome");
                String cognome = rs.getString("cognome");
                String codiceFiscale = rs.getString("cf");
                String dataNascita = rs.getString("dataNascita");
                String emailU = rs.getString("email");
                String password = rs.getString("password");
                boolean admin = rs.getBoolean("isAdmin");

                Utente u = new Utente();
                u.setCodiceFiscale(codiceFiscale);
                u.setNome(nome);
                u.setCognome(cognome);
                u.setDataNascita(dataNascita);
                u.setEmail(emailU);
                u.setPassword(password);
                u.setAmministratore(admin);
                return u;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
