package org.example.persistence;

import org.example.model.Treno;

public class popolaDataB {
    public static void main(String[] args) {
        DataBcreaTabelle.init(); // creo nuove tabelle

        gestisciTrenoDB trenoDB;
        trenoDB = new gestisciTrenoDB();

        Treno treno = new Treno();
        treno.setIDtreno("RE-5584");
        treno.setTipologia("REGIONALE");
        treno.setStatoTreno("In attesa di comunicazioni");
        trenoDB.addTreno(treno);
    }
}
