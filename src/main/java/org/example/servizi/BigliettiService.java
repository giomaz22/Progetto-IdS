package org.example.servizi;

import org.example.model.Biglietto;
import org.example.persistence.DBConnectionSingleton;
import org.example.persistence.dao.BigliettoDAO;
import org.example.persistence.dao.ViaggioDAO;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/*
Questa è una classe di servizio. Utile per la gestione dell'acquisto
di uno o più biglietti da parte degli utenti/clienti, modifica dei
biglietti e/o eventuale cancellazione.
 */

/**
 *
 public void addBiglietto(Biglietto b, Connection conn) throws SQLException
 public Biglietto trovaBigliettoPerCodice(String codice)
 public void aggiornaBiglietto(Biglietto b, Connection conn) throws SQLException
 public List<Biglietto> bigliettiPerUtente(String cf)
 */

public class BigliettiService {
    private final BigliettoDAO bigliettiDataBase; //classe di gestione DB per i biglietti
    private final ViaggioDAO viaggioDataBase;

    public BigliettiService() {
        this.bigliettiDataBase = new BigliettoDAO();
        this.viaggioDataBase = new ViaggioDAO();
    }


    public void add(Biglietto b) throws SQLException {
        Connection conn = null;
        try{
            conn = DBConnectionSingleton.getConnection();
            conn.setAutoCommit(false);
            int viaggioID = b.getIdViaggio();
            int postiDisponibili = viaggioDataBase.trovaViaggioPerID(viaggioID, conn).getNumPostiDisponibili();
            if(postiDisponibili > 0){
                bigliettiDataBase.addBiglietto(b, conn);
                viaggioDataBase.aggiornaDisponibilitaPosti(b.getIdViaggio(), postiDisponibili - 1, conn);
                conn.commit();
            }else{
                conn.rollback();
                throw new SQLException("Posti disponibili non sufficienti.");
            }
        }catch (SQLException e){
            if(conn != null){
                conn.rollback();
            }
        }finally {
            if(conn != null){
                conn.setAutoCommit(true);
                conn.close();
            }
        }

    }

    public void update(Biglietto b) throws SQLException {
        Connection conn = null;
        try {
            conn = DBConnectionSingleton.getConnection();
            conn.setAutoCommit(false);
            if(b != null){
                bigliettiDataBase.aggiornaBiglietto(b, conn);
            } else{
                conn.rollback();
                throw new SQLException("Biglietto non trovato.");
            }
        } catch (SQLException e) {
            if(conn != null){
                conn.rollback();
            }
        } finally {
            if(conn != null){
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    public Biglietto findById(String id) throws SQLException {
        return bigliettiDataBase.trovaBigliettoPerCodice(id);
    }

    public List<Biglietto> findAllByCF(String cf) throws SQLException {
        return bigliettiDataBase.bigliettiPerUtente(cf);
    }


    public static boolean checkLuhn(String numCarta) {
        if (numCarta == null || numCarta.length() != 16 || !numCarta.matches("\\d+")) return false;

        int somma = 0;
        for (int i = 0; i < 16; i++) {
            int cifra = Character.getNumericValue(numCarta.charAt(15 - i));
            if (i % 2 == 1) { // ogni seconda cifra da destra
                cifra *= 2;
                if (cifra > 9) cifra -= 9;
            }
            somma += cifra;
        }
        return somma % 10 == 0;
    }
}