package org.example.servizi;

import org.example.model.Treno;
import org.example.persistence.dao.TrenoDAO;

import java.util.List;

public class TrenoService {
    private final TrenoDAO trenoDataBase;

    public TrenoService() {
        this.trenoDataBase = new TrenoDAO();
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

}
