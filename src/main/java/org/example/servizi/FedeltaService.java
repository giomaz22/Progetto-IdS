package org.example.servizi;

import org.example.model.Fedelta;
import org.example.model.Utente;
import org.example.persistence.dao.FedeltaDAO;

public class FedeltaService {
    private final FedeltaDAO fedeltaDataBase;

    public FedeltaService() {
        this.fedeltaDataBase = new FedeltaDAO();
    }

    public void addNewFidelityCard(Utente u, Fedelta carta){
        if(findFedeltaByCF(u.getCodiceFiscale()) == null
                && u.getCodiceFiscale().equals(carta.getCFpossessore()))
            fedeltaDataBase.addCFedeltaPerCliente(carta);
    }

    public Fedelta findFedeltaByCF(String cf){
        return fedeltaDataBase.trovaCartaPerCFUtente(cf);
    }

    // questo metodo può tornarmi utile successivamente per la ricerca delle promozioni
    public boolean isFedeltaUtente(String cf){
        if(findFedeltaByCF(cf) == null)
            return false;
        return true;
    }

}
