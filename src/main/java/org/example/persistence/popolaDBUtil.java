package org.example.persistence;

import org.example.model.Treno;
import org.example.persistence.dao.TrenoDAO;

public class popolaDBUtil {
    public static void main(String[] args) {
        CreaTabelleUtil.init(); // creo nuove tabelle

        TrenoDAO trenoDB;
        trenoDB = new TrenoDAO();

        Treno treno = new Treno();
        treno.setIDtreno("RE-5584");
        treno.setTipologia("REGIONALE");
        treno.setStatoTreno("In attesa di comunicazioni");
        trenoDB.addTreno(treno);
    }
}
