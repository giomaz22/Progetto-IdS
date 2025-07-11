package org.example.servizi;

import org.example.model.Promozione;
import org.example.persistence.DBConnectionSingleton;
import org.example.persistence.dao.PromozioneDAO;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class PromozioneService {
    private final PromozioneDAO promoDataBase;
    private final FedeltaService serviceFedelta;

    public PromozioneService(){
        this.promoDataBase = new PromozioneDAO();
        this.serviceFedelta = new FedeltaService();
    }

    public void addNewPromotion(Promozione promo) throws SQLException {
        Connection conn = DBConnectionSingleton.getConnection();
        promoDataBase.addPromozione(promo, conn);
    }

    public void removePromotion(String codice) {promoDataBase.eliminaPromozione(codice);}

    public List<Promozione> promoSoloFedelta(String CF){
        boolean check = serviceFedelta.isFedeltaUtente(CF);
        return promoDataBase.promozioniAttive( null, check, null);
    }

    public List<Promozione> getPromozioniPerViaggio(String tipoTreno, boolean isFed, String data){
        return promoDataBase.promozioniAttive(tipoTreno, isFed, data);
    }

}
