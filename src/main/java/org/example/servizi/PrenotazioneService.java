package org.example.servizi;

import org.example.model.Biglietto;
import org.example.model.Prenotazione;
import org.example.persistence.DBConnectionSingleton;
import org.example.persistence.dao.PrenotazioneDAO;
import org.example.persistence.dao.ViaggioDAO;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class PrenotazioneService {
    /*
    public void addPrenotazione(Prenotazione p, Connection conn) throws SQLException
    public Prenotazione trovaPerPNR(String pnr)
    public void eliminaPrenotazione(Prenotazione p, Connection conn) throws SQLException
    public List<Prenotazione> tutteLePrenotazioni()
     */
    private final PrenotazioneDAO prenotDataBase; //classe di gestione DB per i biglietti
    private final ViaggioDAO viaggioDataBase;

    public PrenotazioneService() {
        this.prenotDataBase = new PrenotazioneDAO();
        this.viaggioDataBase = new ViaggioDAO();
    }

    public void add(Prenotazione p) throws SQLException {
        Connection conn = null;
        try{
            conn = DBConnectionSingleton.getConnection();
            conn.setAutoCommit(false);
            int viaggioID = p.getId_viaggio();
            int postiDisponibili = viaggioDataBase.trovaViaggioPerID(viaggioID).getNumPostiDisponibili();
            if(postiDisponibili > 0){
                prenotDataBase.addPrenotazione(p, conn);
                viaggioDataBase.aggiornaDisponibilitaPosti(p.getId_viaggio(), postiDisponibili - 1, conn);
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

    public void deleteP(Prenotazione p) throws SQLException {
        Connection conn = null;
        try {
            conn = DBConnectionSingleton.getConnection();
            conn.setAutoCommit(false);
            if(p != null){
                prenotDataBase.eliminaPrenotazione(p, conn);
            }else{
                conn.rollback();
                throw new SQLException("Prenotazione non trovata.");
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

    public Prenotazione findByPNR(String PNR) throws SQLException {
        return prenotDataBase.trovaPerPNR(PNR);
    }

    public List<Prenotazione> findAll() throws SQLException {
        return prenotDataBase.tutteLePrenotazioni();
    }
}
