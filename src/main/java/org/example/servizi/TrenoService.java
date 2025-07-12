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

    public TrenoService() {
        this.trenoDataBase = new TrenoDAO();

    }

    public void addTrain(Treno t){
        trenoDataBase.addTreno(t);
    }

    public void removeTrain(String idtreno){trenoDataBase.eliminaTreno(idtreno);}

    public Treno getTrainByID(String ID){
        return trenoDataBase.trovaTrenoPerID(ID);
    }

    public List<Treno> getAllTrains(){
        return trenoDataBase.tuttiItreni();
    }



}
