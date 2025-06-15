package org.example.servizi;

import org.example.model.Treno;
import org.example.persistence.DBConnectionSingleton;
import org.example.persistence.dao.TrenoDAO;
import org.example.server.TrenicalServiceImpl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class TrenoService {
    private final TrenoDAO trenoDataBase;
    private final TrenicalServiceImpl trenicalService;

    public TrenoService() {
        this.trenoDataBase = new TrenoDAO();
        this.trenicalService = new TrenicalServiceImpl();
    }

    public void addTrain(Treno t){
        trenoDataBase.addTreno(t);
    }

    public Treno getTrainByID(String ID){
        return trenoDataBase.trovaTrenoPerID(ID);
    }

    public List<Treno> getAllTrains(){
        return trenoDataBase.tuttiItreni();
    }

    public void updateStatoTreno(String idTreno, String newStato) throws SQLException {
        String message = "Il treno " + idTreno + " ha cambiato stato in: " + newStato;
        Connection conn = null;
        try {
            conn = DBConnectionSingleton.getConnection();
            conn.setAutoCommit(false);
            if(idTreno != null){
                trenoDataBase.aggiornaStatoTreno(idTreno, newStato, conn);
            } else{
                conn.rollback();
                throw new SQLException("Treno non trovato");
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
        trenicalService.notificaCambiamentoTreno(idTreno, newStato, null, null, message);
    }

}
