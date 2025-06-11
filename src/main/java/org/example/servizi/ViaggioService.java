package org.example.servizi;

import lombok.SneakyThrows;
import org.example.model.Viaggio;
import org.example.persistence.DBConnectionSingleton;
import org.example.persistence.dao.ViaggioDAO;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class ViaggioService {
    private final ViaggioDAO viaggioDataBase;

    public ViaggioService(){
        this.viaggioDataBase = new ViaggioDAO();
    }

    public void addNewViaggio(Viaggio v){
        viaggioDataBase.addNewViaggio(v);
    }

    public List<Viaggio> ricercaViaggi(String S_partenza, String S_arrivo, String data, String tipoTreno){
        return viaggioDataBase.cercaViaggi(S_partenza, S_arrivo, data, tipoTreno);
    }

    private Viaggio trovaConId(int id) throws SQLException {
        Connection conn = DBConnectionSingleton.getConnection();
        return viaggioDataBase.trovaViaggioPerID(id, conn);
    }

    @SneakyThrows
    public Viaggio findViaggioById(int id){
        return trovaConId(id);
    }

    // questo metodo si usa quando si cancella una prenotazione o un biglietto
    public void incrementaPostiDisponibili(int idViaggio, int postiCancellati) throws SQLException {
        Connection conn = null;
        try {
            conn = DBConnectionSingleton.getConnection();
            conn.setAutoCommit(false);
            Viaggio v = findViaggioById(idViaggio);
            if (v != null) {
                viaggioDataBase.aggiornaDisponibilitaPosti(v.getIDViaggio(), v.getNumPostiDisponibili() + postiCancellati, conn);
                conn.commit();
            } else {
                conn.rollback();
                throw new SQLException("Viaggio non trovato. Operazione non effettuata!");
            }
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();
            }
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    // questo metodo si usa quando si effettua una prenotazione o un acquisto biglietto
    public void decrementaPostiDisponibili( int idViaggio, int postiAcquistati) throws SQLException {
        Connection conn = null;
        try {
            conn = DBConnectionSingleton.getConnection();
            conn.setAutoCommit(false);
            Viaggio v = findViaggioById(idViaggio);
            if (v != null) {
                viaggioDataBase.aggiornaDisponibilitaPosti(v.getIDViaggio(), v.getNumPostiDisponibili() - postiAcquistati, conn);
                conn.commit();
            } else {
                conn.rollback();
                throw new SQLException("Viaggio non trovato. Operazione non effettuata!");
            }
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();
            }
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }

    }

}
