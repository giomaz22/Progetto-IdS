package org.example.servizi;

import lombok.SneakyThrows;
import org.example.model.Treno;
import org.example.model.Viaggio;
import org.example.persistence.DBConnectionSingleton;
import org.example.persistence.dao.TrenoDAO;
import org.example.persistence.dao.ViaggioDAO;
import org.example.server.TrenicalServiceImpl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class ViaggioService {
    private final ViaggioDAO viaggioDataBase;
    private final TrenoDAO trenoDataBase;

    public ViaggioService(){
        this.viaggioDataBase = new ViaggioDAO();
        this.trenoDataBase = new TrenoDAO();
    }

    public void addNewViaggio(Viaggio v){
        viaggioDataBase.addNewViaggio(v);
    }

    public List<Viaggio> getTuttiIViaggi(){
        return viaggioDataBase.tuttiIViaggi();
    }

    public void removeViaggio(int idViaggio){viaggioDataBase.eliminaViaggio(idViaggio);}

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

    public List<Viaggio> findViaggiNewDataOra(String ora, String data){
        return viaggioDataBase.cercaViaggiDataOraNew(ora, data);
    }

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

    public double calcolaDifferenzaPrezzoClasse(Viaggio viaggio, String classeAttuale, String nuovaClasse) {
        double prezzoBase = viaggio.getPrezzo();
        if (classeAttuale.equals(nuovaClasse)) return 0.0;
        if (classeAttuale.equals("SECONDA") && nuovaClasse.equals("PRIMA")) {
            return prezzoBase * 0.3;
        } else if (classeAttuale.equals("PRIMA") && nuovaClasse.equals("SECONDA")) {
            return -prezzoBase * 0.3;
        }
        return 0.0;
    }

    public String dettagliAttualiViaggio(int idViaggio) {
        Viaggio v = findViaggioById(idViaggio);
        if (v == null) {
            return "Viaggio non trovato.";
        }

        Treno t = trenoDataBase.trovaTrenoPerID(v.getIDtreno());
        if (t == null) {
            return "Treno associato al viaggio non trovato.";
        }

        return String.format(
                " Stato del viaggio %d con treno %s:\n" +
                        " Ora partenza: %s\n" +
                        " Ora arrivo prevista: %s\n" +
                        " Stato treno: %s",
                v.getIDViaggio(),
                t.getIDtreno(),
                v.getOraPartenza(),
                v.getOraArrivo(),
                t.getStatoTreno()
        );
    }

}


